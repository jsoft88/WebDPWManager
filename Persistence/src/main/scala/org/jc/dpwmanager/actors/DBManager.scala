package org.jc.dpwmanager.actors

import akka.actor.{Actor, PoisonPill, Status}
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentRepositoryImpl, DefaultDeploymentByRoleRepositoryImpl, DefaultHostRepositoryImpl}
import org.jc.dpwmanager.models.{Agent, DeploymentByRole, Host}
import org.jc.dpwmanager.util.{BusinessRole, PersistenceRole}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
  * Created by jorge on 30/6/2017.
  */
class DBManager(actorName: String, actorSystemName: String) extends Actor {

  var initializing: Boolean = true

  var stopping: Boolean = false

  var myAgentId: Short = 0

  var deploymentByRole: DeploymentByRole = DeploymentByRole(
    deployId = 0,
    hostId = 0,
    actorName = self.path.name,
    actorSystemName = self.path.address.system,
    port = self.path.address.port.get.asInstanceOf[Short],
    roleId = PersistenceRole.toString,
    componentId = 0)

  implicit val ec: ExecutionContext = context.dispatcher

  implicit val timeout = Timeout(60 seconds)

  override def receive = {
    case CommandWrapper(command) => command execute
  }


  override def postStop(): Unit = {
    //issue a command to retrieve agent, on response, remove it from db.
    stopping = true

    self ! CommandWrapper(DeploymentByRoleRemoveCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentByRole))
  }

  override def preStart(): Unit = {
    (self ? CommandWrapper(HostListRetrieveCommand(new DefaultHostRepositoryImpl(), Host(hostId = 0, address = self.path.address.host.get)))).mapTo[HostListRetrieveResponse] onComplete {
      case Success(r) => {
        (self ? CommandWrapper(DeploymentInsertCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentByRole.copy(hostId = r.response.filter(_.address == self.path.address.host).head.hostId)))).mapTo[DeploymentInsertResponse] onComplete {
          case Failure(ex) => self ! PoisonPill
          case Success(deploymentResponse) => deploymentByRole = deploymentByRole.copy(deployId = deploymentResponse.response.deployId)
        }
      }
      case Failure(_) => self ! PoisonPill
    }
  }
}
