package org.jc.dpwmanager.server

import akka.actor.{Actor, ActorRef, ActorSystem, Address, Deploy, Props, RootActorPath, Status, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.jc.dpwmanager.impl.{DefaultAgentRepositoryImpl, DefaultDeploymentByRoleRepositoryImpl, DefaultDpwRoleConstraintRepositoryImpl, DefaultHostRepositoryImpl}
import org.jc.dpwmanager.util._
import org.slf4j.LoggerFactory
import akka.pattern.ask
import akka.remote.RemoteScope
import org.jc.dpwmanager.agent
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.models.{Agent, DeploymentByRole, DpwRoleConstraint, Host}

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
  var paIndex = 0
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
      val actorRef = context.system.actorOf(Props[agent.Agent].withDeploy(Deploy(scope = RemoteScope(address))))

      actorRef ! PersistenceActorsInformation(persistenceActors map { r => (r.path.name, r.path.address)} toMap)
      sender ! StartRoleSuccess(deployWrapper)
    }

    case DeployMasterStart(messageWrapper) if (persistenceActors.nonEmpty) => {
      val dummyDeployByRole = DeploymentByRole(deployId = messageWrapper.deployId, actorName = "", actorSystemName = "", port = 0, componentId = 0, roleId = "", hostId = 0)
      (persistenceActors(paIndex) ? SingleDeploymentByRoleRetrieveCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeployByRole)).mapTo[SingleDeploymentByRoleRetrieveResponse]
        .onComplete {
          case Success(r) => r.response match {
            case Some(d) => {
              (persistenceActors(paIndex) ? HostListRetrieveCommand(new DefaultHostRepositoryImpl(), Host(hostId = d.hostId, address = ""))).mapTo[HostListRetrieveResponse]
                .onComplete {
                  case Success(res) => {
                    val host = res.response.filter(_.hostId == d.deployId).headOption
                    host match {
                      case Some(h) => {
                        (persistenceActors(paIndex) ? ConstraintForRoleRetrieveCommand(new DefaultDpwRoleConstraintRepositoryImpl(), DpwRoleConstraint(constraintId = 0, roleId = d.roleId, canExecute = false)))
                          .mapTo[ConstraintForRoleRetrieveResponse].onComplete {
                          case Success(constraintResponse) => {
                            if (constraintResponse.response) {
                              (context.actorSelection(RootActorPath(Address("akka.tcp", d.actorSystemName, h.address, d.port)) + "/user/" + d.actorName) ? DeployMasterStart(messageWrapper)) onComplete {
                                case Success(_) => sender ! _
                                case Failure(ex) => sender ! Status.Failure(ex)
                              }
                            } else {
                              sender ! Status.Failure(new Exception("This role is not allowed to execute!"))
                            }
                          }
                          case Failure(ex) => sender ! Status.Failure(ex)
                        }
                      }
                      case None => sender ! Status.Failure(new Exception("The host is not currently registered in cluster."))
                    }
                  }
                  case Failure(ex) => sender ! Status.Failure(ex)
                }
            }
            case None => sender ! Status.Failure(new Exception("No role has been deployed in host."))
          }
          case Failure(ex) => sender ! Status.Failure(ex)
      }
    }

    case DeployMasterStart if persistenceActors.isEmpty => sender ! Status.Failure(new Exception("There are no agents available for executing task."))
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