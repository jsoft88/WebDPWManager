package org.jc.dpwmanager.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props, RootActorPath, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.jc.dpwmanager.impl.DefaultAgentRepositoryImpl
import org.jc.dpwmanager.util._
import org.slf4j.LoggerFactory
import akka.pattern.ask
import org.jc.dpwmanager.commands.{AgentInsertCommand, AgentInsertResponse}
import org.jc.dpwmanager.models.Agent

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by jorge on 8/6/2017.
  */
class DpwManagerServer(persistenceClusterSeedNodes: Array[String], persistenceActorName: String) extends Actor {

  val logger = LoggerFactory.getLogger(this.getClass)
  var agents: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  var persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  val cluster = Cluster(context.system)

  implicit val timeout = Timeout(60 seconds)

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case DeployMasterStart if agents.isEmpty => sender() ! DeployMasterFailed("There are no agents available for executing task.")
    case DeployMasterStart(messageWrapper) => {
      (agents filter(_.path.name.equals(messageWrapper.actorName)) headOption) match {
        case Some(a) => a ? DeployMasterStart(messageWrapper) onComplete {
          case Success(messageWrapper: MessageWrapper) => persistenceActors.headOption match {
            case Some(pa) => pa ? AgentInsertCommand(new DefaultAgentRepositoryImpl, Agent(port = a.path.address.port.get, host = a.path.address.host.get, agentId = 0, actorSystemName = context.system.name, actorName = a.path.name)) onComplete {
              case Success(_) => sender ! DeployMasterCompleted(messageWrapper)
              case Failure(ex) => {
                sender ! DeployMasterFailed("Registering started master failed. A shutdown request has been sent to agent: " + ex.getMessage)
                a ! StopMaster(messageWrapper)
              }
            }
            case None => a ! StopMaster(messageWrapper.asInstanceOf[MessageWrapper])
          }
          case Failure(ex) => sender() ! DeployMasterFailed(ex.getMessage)
        }
        case None => sender() ! DeployMasterFailed("Selected agent does not exist")
      }
    }
    case AgentRegistration if !agents.contains(sender()) =>
      context watch sender()
      agents :+ sender()
    case Terminated(a) => agents = agents.filterNot(_ == a)
    case state: CurrentClusterState =>
      for (m <- state.members) {
        if (m.status == akka.cluster.MemberStatus.up && m.hasRole(PersistenceRole.toString)) persistenceActors :+ context.actorSelection(RootActorPath(m.address) + "/user/" + PersistenceRole.toString)
        else if (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(PersistenceRole.toString)) persistenceActors.diff(state.members.filter(m => (m.status == akka.cluster.MemberStatus.exiting && m.hasRole(PersistenceRole.toString))).map(m => context.actorSelection(RootActorPath(m.address) + "/user/" + PersistenceRole.toString)).toSeq)
        else if (m.status == akka.cluster.MemberStatus.up && m.hasRole(BusinessRole.toString)) {
          persistenceActors.foreach(r => context.actorSelection(RootActorPath(m.address) + "/user/" + BusinessRole.toString) resolveOne(30 seconds) onComplete {
            case Success(r) => r ! PersistenceActorsInformation(persistenceActors.map(pa => RootActorPath(pa.path.address) + "/user/" + PersistenceRole.toString).toArray)
            case Failure(_) =>
          })
          context.actorSelection(RootActorPath(m.address) + "/user/" + BusinessRole.toString) resolveOne(30 seconds) onComplete {
            case Success(r) => r ! ServerActorsInformation(RootActorPath(self.path.address) + "/user/" + ServerRole.toString)
            case Failure(_) =>
          }
        }
      }
  }

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
    //block on preStart to retrieve persistence actors, if none found, fail
    for (s <- persistenceClusterSeedNodes) { (context.actorSelection(s + "/user/" + persistenceActorName) resolveOne) onComplete { case Success(ar) => persistenceActors :+ ar } }
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }
}

object Server {
  def main(args: Array[String]): Unit = {
    if (args.length < 4) throw new Exception("Insufficient arguments for server initialization")

    val Array(port, host, actorSystemName, serverName, persistenceClusterSeedNodes, persistenceActorName) = args
    val role = ServerComponent.toString
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]"))
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
      .withFallback(ConfigFactory.parseString(s"akka.cluster.seed-nodes = [$persistenceClusterSeedNodes]"))
      .withFallback(ConfigFactory.load())

    val system = ActorSystem(actorSystemName, config)
    system.actorOf(Props(new DpwManagerServer(persistenceClusterSeedNodes.split(","), persistenceActorName)), name = serverName)
  }
}