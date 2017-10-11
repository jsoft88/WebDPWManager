package org.jc.dpwmanager.agent

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props, RootActorPath}
import akka.cluster.{Cluster, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent}
import akka.util.Timeout
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl._
import org.jc.dpwmanager.models.{Agent, AgentExecution}
import org.jc.dpwmanager.util._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import sys.process._
import akka.pattern.{AskTimeoutException, ask}
import com.typesafe.config.ConfigFactory
import org.jc.dpwmanager.models

import scala.concurrent.ExecutionContext

/**
  * Created by jorge on 25/5/2017.
  */
object DpwAgent {

  def main(args: Array[String]): Unit = {
    val Array(host, port, actorName, actorSystemName, persistenceClusterSeedNodes, persistenceActorName) = args
    val role = AgentComponent.toString
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles = [$role]"))
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
      .withFallback(ConfigFactory.parseString(s"akka.cluster.seed-nodes = [$persistenceClusterSeedNodes]"))
      .withFallback(ConfigFactory.load())

    val system = ActorSystem(actorSystemName, config = config)
    system.actorOf(Props(new Agent(persistenceClusterSeedNodes.split(","), persistenceActorName, actorSystemName)), actorName)
  }
}

class Agent(persistenceClusterSeedNodes: Array[String], persistenceActorName: String, actorSystemName: String) extends Actor {

  val cluster = Cluster(context.system)

  var procHandler: Option[Process] = None

  implicit val timeout = Timeout(60 seconds)

  val persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty

  var agentId: Short = 0

  var agentExecution: Option[AgentExecution] = None

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case DeployMasterStart(msgWrapper) => {
      persistenceActors headOption match {
        case Some(pa) => {
          Try {
            val process = Process(msgWrapper.execArgs.commandToString())
            procHandler = Some(process.run())
          } match {
            case Success(_) => {
              DeployMasterCompleted(msgWrapper)
              agentExecution = Some(AgentExecution(agentExecId = 0, command = msgWrapper.execArgs.commandToString(), cleanStop = false, executionTimestamp = System.currentTimeMillis(), agentId = agentId, masterTypeId = 0))
              pa ? AgentExecutionInsertCommand onComplete {
                case Success(a: AgentExecution) => {
                  agentExecution = Some(a)
                  sender() ! DeployMasterCompleted(msgWrapper)
                }
                case Failure(ex) => {
                  procHandler match { case Some(p) => p.destroy() case None => }
                  sender() ! DeployMasterFailed(ex.getMessage)
                }
              }
            }
            case Failure(ex) => DeployMasterFailed(ex.getMessage)
          }
        }
        case None => sender() ! DeployMasterFailed("No persistence agents are available.")
      }
    }
    case StopMaster(msgWrapper) => {
      procHandler match {
        case Some(p) => p.destroy()
        case None =>
      }
      persistenceActors headOption match {
        case Some(pa) => pa ? MarkMasterExecutionAsCleanStop(new DefaultAgentExecutionRepositoryImpl, agentExecution.get) onComplete {
          case Success(_) => sender() ! MasterStopped(msgWrapper)
          case Failure(_) => self ! StopMaster(msgWrapper)
        }
      }
    }

    case WasAgentRunningMasterResponse(running: AgentExecution) => running match {
      case AgentExecution(agentExecId, command, cleanStop, _, _, _) => sender() ? MarkMasterExecutionAsCleanStop(new DefaultAgentExecutionRepositoryImpl, running) onComplete {
        case Success(msg) => //everything went well, do not do anything else
        case Failure(ex: AskTimeoutException) => self ! WasAgentRunningMasterResponse(running)
        case Failure(_) => self ! PoisonPill //kill self
      }
      case AgentExecution(0, "", false, 0L, _, 0) => //the agent restarted but it was not doing anything, so nothing else needs to be done.
    }

    case PoisonPill =>

    case state: CurrentClusterState => for (m <- state.members) {
      val rootActorPath = RootActorPath(m.address)
      if (m.status == MemberStatus.Up && m.hasRole(PersistenceRole.toString)) {
        persistenceActors :+ context.actorSelection(rootActorPath + "/user/" + rootActorPath.name)
      } else if (m.status == MemberStatus.Exiting && m.hasRole(PersistenceRole.toString)) {
        persistenceActors.diff(Seq(context.actorSelection(rootActorPath + "/user/" + rootActorPath.name)))
      }
    }
  }

  override def postStop(): Unit = {
    procHandler match {
      case Some(p) => p.destroy()
      case None =>
    }
    cluster.unsubscribe(self)
    println("Agent is shutting down")
  }

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
    //block on preStart to retrieve persistence actors, if none found, fail
    for (s <- persistenceClusterSeedNodes) { (context.actorSelection(s + "/user/" + persistenceActorName) resolveOne) onComplete { case Success(ar) => persistenceActors :+ ar } }
    persistenceActors headOption match {
      case Some(pa) => {
        pa ? AgentInsertCommand(new DefaultAgentRepositoryImpl, Agent(port = self.path.address.port.get, host = self.path.address.host.get, agentId = 0, actorName = self.path.name, actorSystemName = actorSystemName)) onComplete {
          case Success(a: models.Agent) => agentId = a.agentId
          case Failure(_) => context.stop(self)
        }
      }
      case None => context.stop(self)
    }
  }
}
