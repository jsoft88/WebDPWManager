package controllers

import javax.inject.{Inject, Singleton}

import initialization.InitialConfiguration
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.ReachPersistenceAgentWith
import akka.pattern.ask
import akka.util.Timeout
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentRepositoryImpl, DefaultDeploymentByRoleRepositoryImpl, DefaultDpwRolesRepositoryImpl, DefaultHostRepositoryImpl}
import org.jc.dpwmanager.models.{Agent, DeploymentByRole, DpwRoles, Host}
import play.api.libs.json.{JsPath, JsValue, Json, Writes}
import play.api.libs.functional.syntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class HostController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc){

  implicit val timeout = Timeout(15 seconds)

  implicit val dpwRolesWrites: Writes[DpwRolesUIModel] = (
    (JsPath \ "roleId").write[String] and
      (JsPath \ "roleLabel").write[String] and
      (JsPath \ "roleDescription").write[String]
  )(unlift(DpwRolesUIModel.unapply))

  implicit val deploymentByRoleWrites: Writes[DeploymentByRoleUIModel] = (
    (JsPath \ "deployId").write[Int] and
      (JsPath \ "actorName").write[String] and
      (JsPath \ "actorSystemName").write[String] and
      (JsPath \ "port").write[Short] and
      (JsPath \ "role").write[DpwRolesUIModel] and
      (JsPath \ "componentId").write[Short]
  )(unlift(DeploymentByRoleUIModel.unapply))

  implicit val hostWrites: Writes[HostUIModel] = (
    (JsPath \ "hostId").write[Short] and
      (JsPath \ "address").write[String] and
      (JsPath \ "deployments").write[Seq[DeploymentByRoleUIModel]] and
      (JsPath \ "executions").write[Seq[AgentExecutionUIModel]]
  )(unlift(HostUIModel.unapply))

  def getAllRoles = Action.async { implicit request =>
    val dpwRole = DpwRoles(roleId = "none", roleLabel = "", roleDescription = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(RetrieveDpwRolesCommand(new DefaultDpwRolesRepositoryImpl(), dpwRole)))).mapTo[RetrieveDpwRolesResponse].map(s => Ok(Json.toJson(s.response))) recover { case ex: Exception => InternalServerError("An error occurred while retrieving DPW roles: " + ex.getMessage)}
      case None => Future.successful(InternalServerError("No business actor available to fulfill this request"))
    }
  }

  def getAgentRole(agentId: Int) = Action.async { implicit request =>
    val agent = Agent(port = 0, agentId = agentId.toShort, host = "", actorName = "", actorSystemName = "", roleId = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentRetrieveCommand(new DefaultAgentRepositoryImpl(), agent)))).mapTo[AgentRetrieveResponse].flatMap(s =>
        s.response match {
          case Some(ag) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DpwRoleRetrieveCommand(new DefaultDpwRolesRepositoryImpl(), DpwRoles(roleId = ag.roleId, roleLabel = "", roleDescription = ""))))).mapTo[DpwRoleRetrieveResponse].map(r => r.response headOption match {
            case Some(role) => Ok(Json.toJson(role))
            case None => Ok(Json.toJson(DpwRoles(roleId = "-1", roleLabel = "No roles assigned", roleDescription = "Assign a role first.")))
          })
          case None => Future.successful(InternalServerError("There isn't an agent for the given ID. Please retry."))
        }
      )
      case None => Future.successful(InternalServerError("No business actor available to fulfill this request"))
    }
  }

  private def noBusinessActor = "No business actor available to fulfill this request"

  def getAllHosts() = Action.async { implicit request =>
    val dummyHost = Host(hostId = 0, address = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(HostListRetrieveCommand(new DefaultHostRepositoryImpl(), dummyHost)))).mapTo[HostListRetrieveResponse]
        .map(s => Ok(Json.toJson(s.response.map(h => HostUIModel(hostId = h.hostId, address = h.address, deployments = IndexedSeq.empty, executions = IndexedSeq.empty))))) recover {
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getDeploymentsForHost(hostId: Short) = Action.async { implicit request =>
    val dummyDeploymentByRole = DeploymentByRole(deployId = 0, hostId = hostId, actorName = "", actorSystemName = "", componentId = 0, port = 0, roleId = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DeploymentsForHostListCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeploymentByRole)))).mapTo[DeploymentsForHostListResponse]
        .map(s => Ok(Json.toJson(s.response.map(dp => DeploymentByRoleUIModel(deployId = dp.deployId, actorName = dp.actorName, actorSystemName = dp.actorSystemName, port = dp.port, componentId = dp.componentId, role = DpwRolesUIModel(roleId = dp.roleId, roleLabel = "", roleDescription = "")))))) recover {
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getAllHostsWhereRoleDeployed(roleId: String) = Action.async { implicit request =>
    val dummyRoleDeploy = DeploymentByRole(deployId = 0, roleId = roleId, hostId = 0, actorName = "", actorSystemName = "", componentId = 0, port = 0)

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DeploymentsForRoleHostListCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyRoleDeploy)))).mapTo[DeploymentsForRoleHostListResponse]
        .map(s => Ok(Json.toJson(s.response.map(dp => DeploymentByRoleUIModel(deployId = dp.deployId, actorName = dp.actorName, actorSystemName = dp.actorSystemName, port = dp.port, componentId = dp.componentId, role = DpwRolesUIModel(roleId = dp.roleId, roleLabel = "", roleDescription = "")))))) recover {
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }
}

case class HostUIModel(hostId: Short, address: String, deployments: Seq[DeploymentByRoleUIModel], executions: Seq[AgentExecutionUIModel])

case class MasterFieldUIModel(fieldId: Int, fieldName: String, fieldDescription: String)

case class DpwRolesUIModel(roleId: String, roleLabel: String, roleDescription: String)

case class DeploymentByRoleUIModel(deployId: Int, actorName: String, actorSystemName: String, port: Short, role: DpwRolesUIModel, componentId: Short)