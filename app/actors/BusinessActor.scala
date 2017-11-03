package actors

import scala.util.Success
import akka.actor.{Actor, ActorNotFound, ActorRef, Address, Deploy, Props, RootActorPath, Status, Terminated}
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.util.Timeout
import utils.{BusinessMessage, BusinessRegisterFlowActor, ReachPersistenceAgentWith}
import akka.pattern.ask
import akka.remote.RemoteScope
import akka.remote.WireFormats.RemoteScope
import com.google.inject.Inject
import initialization.InitialConfiguration
import org.jc.dpwmanager.actors.DBManager
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
  var paIndex = -1

  //Index for simple round-robin routing to server actors
  var saIndex = -1

  var persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  var serverActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  var notificationActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]

  implicit lazy val askTimeout = Timeout(60 seconds)

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(PersistenceRole.toString)) => {
      val address = Address("akka.tcp", deployWrapper.actorSystemName, deployWrapper.address, deployWrapper.port)
      val perRef = context.system.actorOf(Props[DBManager].withDeploy(Deploy(scope = RemoteScope(address))))
      context.watch(perRef)
      persistenceActors :+ perRef
    }

    //case Terminated(actor) => if (persistenceActors.filterNot(_.path == actor.path).length == 0) notificationActors foreach (_ ! "There are no persistence agents in this cluster, please add at least one or subsequent requests will fail.")

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(ServerRole.toString) && persistenceActors.nonEmpty) => {
      val address = Address("akka.tcp", deployWrapper.actorSystemName, deployWrapper.address, deployWrapper.port)
      val serRef = context.system.actorOf(Props[DBManager].withDeploy(Deploy(scope = RemoteScope(address))))
      serverActors :+ serRef
      serRef ! PersistenceActorsInformation(persistenceActors map {r => (r.path.name, r.path.address) } toMap)
    }

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(ServerRole.toString)) => {
      sender ! Status.Failure(new Exception("No persistence roles were deployed prior to this deployment. Please add at least one persistence role to cluster."))
    }

    case StartRoleInHost(deployWrapper) => {

      saIndex = (saIndex + 1) % serverActors.length

      (serverActors(saIndex) ? StartRoleInHost(deployWrapper)) map _
    }

    case ReachPersistenceAgentWith(command) if (persistenceActors.nonEmpty) => (persistenceActors((paIndex + 1) % persistenceActors.length) ? command) map _

    case StartRoleFailed(reason, role) if (role.equals(BusinessRole.toString)) =>

    case DeployMasterStart(_) if serverActors.isEmpty => self ! DeployMasterFailed("No servers enabled to handle master deploy request")

    case DeployMasterStart(messageWrapper) => serverActors((saIndex + 1) % serverActors.length) ? DeployMasterStart(messageWrapper) map _

    case state: CurrentClusterState => {
      for (m <- state.members) {
        if (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(ServerRole.toString)) {
          serverActors = serverActors.filterNot(_.path.address == m.address)
          serverActors headOption match { case None => notificationActors foreach (_ ! "No server actors are available in cluster, please, add some or subsequent operations will fail.")}
        }
        else if (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(PersistenceRole.toString)) {
          persistenceActors = persistenceActors.filterNot(_.path.address == m.address)
          persistenceActors headOption match { case None => notificationActors foreach (_ ! "There are no persistence agents in this cluster, please add at least one or subsequent requests will fail.")}
        }
        else if (m.status == akka.cluster.MemberStatus.up && m.hasRole(ServerRole.toString)) serverActors :+ context.actorSelection(RootActorPath(m.address) + "/user/" + ServerRole.toString)
        else if (m.status == akka.cluster.MemberStatus.up && m.hasRole(PersistenceRole.toString)) persistenceActors :+ context.actorSelection(RootActorPath(m.address) + "/user/" + PersistenceRole.toString)
      }
    }

    case BusinessRegisterFlowActor(out) => notificationActors :+ out

    case BusinessMessage(message) if (notificationActors.nonEmpty) => notificationActors foreach { _ ! message }
  }

  override def preStart(): Unit = {

  }
}

object BusinessActorDefaults {
  val PERSISTENCE_ACTOR_NAME = "persistence"
}