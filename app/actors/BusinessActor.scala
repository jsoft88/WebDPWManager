package actors

import scala.util.Success
import akka.actor.{Actor, ActorNotFound, ActorRef, RootActorPath}
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.util.Timeout
import utils.ReachPersistenceAgentWith
import akka.pattern.ask
import com.google.inject.Inject
import initialization.InitialConfiguration
import org.jc.dpwmanager.util._
import play.api.Configuration
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Failure

/**
  * Created by jorge on 20/7/2017.
  *///persistenceActorName: String, actorSystemName: String, persistenceActorsHosts: Seq[String], persistenceActorsPorts: Seq[Int]
class BusinessActor @Inject() (configuration: Configuration) extends Actor {

  //Index for simple round-robin routing to persistence agents
  var paIndex = 0

  //Index for simple round-robin routing to server actors
  var saIndex = 0

  val persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  val serverActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  implicit lazy val askTimeout = Timeout(60 seconds)

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case ReachPersistenceAgentWith(command) => if (persistenceActors.nonEmpty) persistenceActors(paIndex) ? command onComplete { case _ => }
    case PersistenceActorsInformation(actorsToResolve) => actorsToResolve.foreach(ap => context.actorSelection(ap).resolveOne(30 seconds) onComplete {
      case Success(ar: ActorRef) => persistenceActors :+ ar
      case Failure(ex: ActorNotFound) =>
      case Failure(_) => self ! PersistenceActorsInformation(actorsToResolve)
    })
    case ServerActorsInformation(pathForResolution) => context.actorSelection(pathForResolution) resolveOne(30 seconds) onComplete {
      case Success(ar: ActorRef) => serverActors :+ ar
      case Failure(ex: ActorNotFound) =>
      case Failure(_) => self ! ServerActorsInformation(pathForResolution)
    }
    case DeployMasterStart(messageWrapper) if serverActors.isEmpty => self ! DeployMasterFailed("No servers enabled to handle master deploy request")
    case DeployMasterStart(messageWrapper) => serverActors((saIndex + 1) % serverActors.length) ? DeployMasterStart(messageWrapper) onComplete {
      case Success(_) => sender ! messageWrapper
      case Failure(ex) => sender ! DeployMasterFailed(ex.getMessage)
    }
    case state: CurrentClusterState => {
      for (m <- state.members) {
        if (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(ServerRole.toString)) serverActors.filterNot(_.path.address == m.address)
        else if (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(PersistenceRole.toString)) persistenceActors.filterNot(_.path.address == m.address)
        else if (m.status == akka.cluster.MemberStatus.up && m.hasRole(ServerRole.toString)) serverActors :+ context.actorSelection(RootActorPath(m.address) + "/user/" + ServerRole.toString)
        else if (m.status == akka.cluster.MemberStatus.up && m.hasRole(PersistenceRole.toString)) persistenceActors :+ context.actorSelection(RootActorPath(m.address) + "/user/" + PersistenceRole.toString)
      }
    }
  }

  override def preStart(): Unit = {
    val persistenceActorName = configuration.getOptional[String]("persistenceActor.name") match {
      case Some(an) => an
      case None => ""
    }

    val actorSystemName = configuration.getOptional[String]("persistenceActor.actorSystemName") match {
      case Some(asn) => asn
      case None => ""
    }

    val persistenceActorsHosts = configuration.getOptional[Seq[String]]("persistenceActor.hosts") match {
      case Some(hosts) => hosts.toIndexedSeq
      case None => IndexedSeq.empty
    }

    val persistenceActorsPorts = configuration.getOptional[Seq[String]]("persistenceActor.ports") match {
      case Some(ports) => ports.toIndexedSeq
      case None => IndexedSeq.empty
    }
    if (persistenceActorName.isEmpty || actorSystemName.isEmpty || persistenceActorsHosts.isEmpty || persistenceActorsPorts.isEmpty) {
      context.stop(self)
    } else {
      InitialConfiguration.persistenceActorsHosts_(persistenceActorsHosts)
      InitialConfiguration.actorSystemName_(actorSystemName)
      InitialConfiguration.businessActor_(Some(self))
      InitialConfiguration.businessActorName_(persistenceActorName)
      InitialConfiguration.persistenceActorsPorts_(persistenceActorsPorts.map(_.toInt))
    }
  }
}

object BusinessActorDefaults {
  val PERSISTENCE_ACTOR_NAME = "persistence"
}