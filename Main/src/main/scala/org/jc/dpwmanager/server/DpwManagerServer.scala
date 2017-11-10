package org.jc.dpwmanager.server

import akka.actor.{Actor, ActorRef, ActorSystem, Address, Deploy, Props, RootActorPath, Status, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.jc.dpwmanager.impl._
import org.jc.dpwmanager.util._
import org.slf4j.LoggerFactory
import akka.pattern.ask
import akka.remote.RemoteScope
import org.jc.dpwmanager.agent
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.models._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by jorge on 8/6/2017.
  */
class DpwManagerServer extends Actor {

  val logger = LoggerFactory.getLogger(this.getClass)
  var agents: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  var persistenceActors: IndexedSeq[ActorRef] = IndexedSeq.empty[ActorRef]
  val cluster = Cluster(context.system)
  var paIndex = -1
  implicit val timeout = Timeout(60 seconds)

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case PersistenceActorsInformation(pathsForResolution) => {
      pathsForResolution foreach { entry => {
        persistenceActors :+ context.actorSelection(RootActorPath(entry._2) + "/user/" + entry._1)
      }}
    }

    case StartRoleInHost(deployWrapper) => {
      val address = Address("akka.tcp", deployWrapper.actorSystemName, deployWrapper.address, deployWrapper.port)
      val actorRef = context.system.actorOf(Props[agent.Agent].withDeploy(Deploy(scope = RemoteScope(address))), deployWrapper.actorName)

      actorRef ! PersistenceActorsInformation(persistenceActors map { r => (r.path.name, r.path.address)} toMap)
      sender ! StartRoleSuccess(deployWrapper)

      agents :+ actorRef
    }

    case DeployMasterStart(messageWrapper) if persistenceActors.isEmpty => sender ! Status.Failure(new Exception("There are no agents available for executing task."))

    case DeployMasterStart(messageWrapper) => {
      (persistenceActors(paIndex) ? CommandWrapper(SingleDeploymentByRoleRetrieveCommand(
        new DefaultDeploymentByRoleRepositoryImpl(),
        DeploymentByRole(deployId = messageWrapper.deployId, hostId = 0, actorName = "", actorSystemName = "", roleId = "", componentId = 0, port = 0))
      )).mapTo[SingleDeploymentByRoleRetrieveResponse].onComplete {
        case Failure(ex) => sender ! Status.Failure(new Exception("Could not retrieve the given role deployment: " + ex.getMessage))
        case Success(deployment) => {
          deployment.response match {
            case Some(d) => {
              //get role constraints
              (persistenceActors(paIndex) ? CommandWrapper(ConstraintForRoleRetrieveCommand(new DefaultDpwRoleConstraintRepositoryImpl(), DpwRoleConstraint(roleId = d.roleId, canExecute = false, constraintId = 0))))
                .mapTo[ConstraintForRoleRetrieveResponse].onComplete {
                case Success(canExecute) => {
                  if (canExecute.response) {
                    val dummyAgentExec = AgentExecution(agentExecId = 0, command = messageWrapper.execArgs.commandToString(), cleanStop = false, System.currentTimeMillis(), deployId = messageWrapper.deployId, masterTypeId = messageWrapper.masterTypeId)
                    (persistenceActors(paIndex) ? CommandWrapper(
                      AgentExecutionInsertCommand(
                        new DefaultAgentExecutionRepositoryImpl(), dummyAgentExec)))
                      .mapTo[AgentExecutionInsertResponse]
                      .onComplete {
                        case Failure(ex) => sender ! Status.Failure(new Exception("Registering started master failed. A shutdown request has been sent to agent: " + ex.getMessage))
                        case Success(response) => {
                          agents.filter(a => a.path.address.host == messageWrapper.address && a.path.address.port.asInstanceOf[Short] == messageWrapper.port) headOption match {
                            case Some(ref) => (ref ? DeployMasterStart(messageWrapper)) onComplete {
                              case Success(_) => sender ! DeployMasterCompleted(messageWrapper)
                              case Failure(ex) => {
                                //remove execution from db
                                (persistenceActors(paIndex) ? CommandWrapper(MarkMasterExecutionAsCleanStop(new DefaultAgentExecutionRepositoryImpl(), dummyAgentExec.copy(agentExecId =  response.response.agentExecId)))) onComplete {
                                  case Success(_) => sender ! Status.Failure(new Exception("Delegating execution to agent failed, but DB state has been successfully restored. Exception was: " + ex.getMessage))
                                  case Failure(cmdEx) => sender ! Status.Failure(new Exception("Delegating execution to agent failed, and DB state could not be restored. Even though it might show like process is running, that is not the case. Exception was: " + cmdEx.getMessage))
                                }
                              }
                            }
                          }
                        }
                      }
                  } else {
                    sender ! Status.Failure(new Exception("This role is not authorized to execute any processes. Change its role to one that is allowed to do so."))
                  }
                }
                case Failure(ex) => sender ! Status.Failure(new Exception("It was not possible to read if the role has permission to execute processes: " + ex.getMessage))
              }
            }
            case None => sender ! Status.Failure(new Exception("Make sure to deploy a role first before trying to run a process."))
          }
        }
      }
    }

    case StopMaster(messageWrapper) if (agents.isEmpty || persistenceActors.isEmpty) => sender ! Status.Failure(new Exception("There are deployed roles in cluster. No master could be stopped."))
    case StopMaster(messageWrapper) => {
      val dummyAgentExec = AgentExecution(agentExecId = 0, command = messageWrapper.execArgs.commandToString(), cleanStop = false, System.currentTimeMillis(), deployId = messageWrapper.deployId, masterTypeId = messageWrapper.masterTypeId)
      agents.filter(a => a.path.address.host == messageWrapper.address && a.path.address.port == messageWrapper.port) headOption match {
        case Some(ref) => ref ? StopMaster(messageWrapper) onComplete {
          case Success(_) => persistenceActors(paIndex) ? CommandWrapper(MarkMasterExecutionAsCleanStop(new DefaultAgentExecutionRepositoryImpl(), dummyAgentExec)) onComplete {
            case Success(_) => sender ! MasterStopped(messageWrapper)
            case Failure(ex) => sender ! Status.Failure(new Exception("Failed to stop master because DB state couldn't be restored, that is, mark process as stopped: " + ex.getMessage))
          }
          case Failure(ex) => sender ! Status.Failure(new Exception("Failed to stop running process in agent: " + ex.getMessage))
        }
        case None => sender ! Status.Failure(new Exception("The agent where the process is running does not exist."))
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
      }
  }

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
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
    system.actorOf(Props[DpwManagerServer], name = serverName)
  }
}