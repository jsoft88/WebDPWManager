package actors

import akka.actor.{Actor, ActorRef, Address, Deploy, Props, RootActorPath, Status, Terminated}
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.util.Timeout
import utils._
import akka.pattern.ask
import akka.remote.RemoteScope
import com.google.inject.Inject
import org.jc.dpwmanager.actors.DBManager
import org.jc.dpwmanager.commands
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultDeploymentByRoleRepositoryImpl, DefaultHostRepositoryImpl}
import org.jc.dpwmanager.models.{DeploymentByRole, Host}
import org.jc.dpwmanager.util._
import play.api.Configuration
import akka.pattern.ask

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

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

  var deploymentByRole: DeploymentByRole = DeploymentByRole(deployId =  0, hostId = 0, actorName = "", actorSystemName = "", port = 0, componentId = 0, roleId = "")

  implicit lazy val askTimeout = Timeout(60 seconds)

  implicit val ec: ExecutionContext = context.dispatcher

  def registerSelfAtDB(host: Host): Unit = {
    deploymentByRole = DeploymentByRole(
      deployId = 0,
      actorName = self.path.name,
      hostId = host.hostId,
      roleId = BusinessRole.toString,
      actorSystemName = self.path.address.system,
      componentId = 0,
      port = self.path.address.port.get.asInstanceOf[Short])

    ((persistenceActors head) ? CommandWrapper(DeploymentInsertCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentByRole))).mapTo[DeploymentInsertResponse].onComplete {
      case Success(d) => deploymentByRole = deploymentByRole.copy(deployId = d.response.deployId, componentId = d.response.componentId)
      case Failure(_) =>
    }
  }

  override def receive: Receive = {

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(PersistenceRole.toString)) => {
      val address = Address("akka.tcp", deployWrapper.actorSystemName, deployWrapper.address, deployWrapper.port)
      val perRef = context.system.actorOf(Props[DBManager].withDeploy(Deploy(scope = RemoteScope(address))), deployWrapper.actorName)
      context.watch(perRef)
      persistenceActors :+ perRef

      if (persistenceActors.length == 1) {
        //register this deployment only the first time a persistence role is deployed.
        ((persistenceActors head) ? commands.CommandWrapper(HostListRetrieveCommand(new DefaultHostRepositoryImpl(), Host(hostId = 0, address = "")))).mapTo[HostListRetrieveResponse] onComplete {
          case Success(hl) => hl.response.filter(_.address == self.path.address.host.get) headOption match {
            case Some(host) => registerSelfAtDB(host)
            case None => {
              ((persistenceActors head) ? CommandWrapper(HostInsertCommand(new DefaultHostRepositoryImpl(), Host(hostId = 0, address = self.path.address.host.get)))).mapTo[HostInsertResponse].onComplete {
                case Success(response) => registerSelfAtDB(response.response)
                case Failure(_) =>
              }
            }
          }
        }
      }
    }

    case Terminated(actor) if (persistenceActors.filter(_.path == actor.path).headOption != None) => notificationActors foreach (_ ! "There are no persistence agents in this cluster, please add at least one or subsequent requests will fail.")

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(ServerRole.toString) && persistenceActors.nonEmpty) => {
      val address = Address("akka.tcp", deployWrapper.actorSystemName, deployWrapper.address, deployWrapper.port)
      val serRef = context.system.actorOf(Props[DBManager].withDeploy(Deploy(scope = RemoteScope(address))), deployWrapper.actorName)
      serverActors :+ serRef
      serRef ! PersistenceActorsInformation(persistenceActors map {r => (r.path.name, r.path.address) } toMap)
    }

    case StartRoleInHost(deployWrapper) if (deployWrapper.role.toString.equals(ServerRole.toString)) => {
      sender ! Status.Failure(new Exception("No persistence roles were deployed prior to this deployment. Please add at least one persistence role to cluster."))
    }

    case StartRoleInHost(deployWrapper) => {

      saIndex = (saIndex + 1) % serverActors.length

      (serverActors(saIndex) ? StartRoleInHost(deployWrapper)).onComplete {
        case Success(_) => sender ! _
        case Failure(ex) => sender ! Status.Failure(ex)
      }
    }

    case ReachPersistenceAgentWith(command) if (persistenceActors.nonEmpty) => (persistenceActors((paIndex + 1) % persistenceActors.length) ? command).onComplete {
      case Success(_) => sender ! _
      case Failure(ex) => sender ! Status.Failure(ex)
    }

    case ReachPersistenceAgentWith(_) => sender ! Status.Failure(new LackOfRoleException(LackOfRolesConstants.NO_PERSISTENCE, "No persistence roles were deployed. Add at least one"))

    case DeployMasterStart(_) if serverActors.isEmpty => self ! Status.Failure(new Exception("No servers enabled to handle master deploy request"))

    case DeployMasterStart(messageWrapper) => (serverActors((saIndex + 1) % serverActors.length) ? DeployMasterStart(messageWrapper)).onComplete {
      case Success(_) => sender ! _
      case Failure(ex) => sender ! Status.Failure(ex)
    }

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

  override def postStop(): Unit = {
    this.persistenceActors headOption match {
      case Some(pa) => {
        (pa ? CommandWrapper(DeploymentByRoleRemoveCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentByRole))).mapTo[DeploymentByRoleRemoveResponse].onComplete {
          case Failure(ex) => println("Failed to remove deployment of Business actor from DB: " + ex.getMessage)
        }
      }
      case None => println("Stopping Business actor but there are no persistence actors available to remove deployment from db")
    }
  }
}

object BusinessActorDefaults {
  val PERSISTENCE_ACTOR_NAME = "persistence"
}