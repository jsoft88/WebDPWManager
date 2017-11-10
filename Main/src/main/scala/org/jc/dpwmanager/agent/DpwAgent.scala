package org.jc.dpwmanager.agent

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props, RootActorPath, Status}
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
    system.actorOf(Props[Agent], actorName)
  }
}

class Agent extends Actor {

  val cluster = Cluster(context.system)

  var procHandler: Option[Process] = None

  implicit val timeout = Timeout(60 seconds)

  val persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty

  var agentId: Short = 0

  var agentExecution: Option[AgentExecution] = None

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case PersistenceActorsInformation(paths) => paths map { entry => persistenceActors :+ context.actorSelection(RootActorPath(entry._2) + "/user/" + entry._1)}

    case DeployMasterStart(messageWrapper) => {
      Try {
        val process = Process(messageWrapper.execArgs.commandToString())
        procHandler = Some(process.run())
      } match {
        case Success(_) => sender ! DeployMasterCompleted(messageWrapper)
        case Failure(ex) => sender ! Status.Failure(new Exception("Failed to start master in selected host. Exception was: " + ex.getMessage))
      }
    }

    case StopMaster(msgWrapper) => {
      procHandler match {
        case Some(p) => p.destroy()
        case None =>
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
  }
}
