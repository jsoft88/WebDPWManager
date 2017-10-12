package controllers

import javax.inject.{Inject, Singleton}

import initialization.InitialConfiguration
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.ReachPersistenceAgentWith
import akka.pattern.ask
import akka.util.Timeout
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentRepositoryImpl, DefaultDpwRolesRepositoryImpl}
import org.jc.dpwmanager.models.{Agent, DpwRoles}
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class HostController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc){

  implicit val timeout = Timeout(15 seconds)

  implicit val dpwRolesWrites = new Writes[DpwRoles] {
    override def writes(o: DpwRoles): JsValue = Json.obj(
      "roleId" -> o.roleId,
      "roleLabel" -> o.roleLabel,
      "roleDescription" -> o.roleDescription
    )
  }

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
}

case class HostUIModel(hostId: String, address: String, port: Int, name: String, agentId: Short, masters: Seq[MasterTypeUIModel], role: DpwRolesUIModel)

case class MasterFieldUIModel(fieldId: Int, fieldName: String, fieldDescription: String)

case class DpwRolesUIModel(roleId: String, roleLabel: String, roleDescription: String)