package controllers

import javax.inject.{Inject, Named, Singleton}

import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentExecutionDetailsRepositoryImpl, DefaultAgentExecutionRepositoryImpl}
import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetails}
import play.api.mvc._
import utils.{ReachPersistenceAgentWith, ReadsAndWrites}
import akka.pattern.ask
import akka.util.Timeout
import initialization.InitialConfiguration
import play.api.libs.json.{JsPath, JsValue, Json, Writes}
import play.api.libs.functional.syntax._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by jorge on 26/7/2017.
  */

@Singleton
class AgentExecutionController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout = Timeout(15 seconds)

  implicit val masterFieldWrites: Writes[MasterFieldUIModel] = ReadsAndWrites.masterFieldWrites

  implicit val agentExecutionDetailsWrites: Writes[AgentExecutionDetailsUIModel] = ReadsAndWrites.agentExecutionDetailsWrites

  implicit val dpwRoleWrites: Writes[DpwRolesUIModel] = ReadsAndWrites.dpwRolesWrites

  implicit val deploymentByRoleWrites: Writes[DeploymentByRoleUIModel] = ReadsAndWrites.deploymentByRoleWrites

  implicit val agentExecutionWrites: Writes[AgentExecutionUIModel] = ReadsAndWrites.agentExecutionWrites

  def noBusinessActor: String = "No business actor available to fulfill this request"

  def listAllAgentExecutions(deployId: Int) = Action.async { implicit request =>
    val agentExecution = AgentExecution(agentExecId = 0, deployId = deployId, cleanStop = false, executionTimestamp = 0L, masterTypeId = 0, command = "")
    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentExecutionListCommand(new DefaultAgentExecutionRepositoryImpl(), agentExecution))))
        .mapTo[AgentExecutionListResponse]
        .map(s => Ok(
          Json.toJson(s.response.map(
            ae => AgentExecutionUIModel(
              agentExecId = ae.agentExecId,
              command = ae.command,
              deployment = DeploymentByRoleUIModel(deployId = ae.deployId, actorName = "", actorSystemName = "", port = 0, role = DpwRolesUIModel(roleId = "", roleLabel = "", roleDescription = ""), componentId = 0),
              masterType = MasterTypeUIModel(masterTypeId = ae.masterTypeId, masterLabel = ""),
              executionTimestamp = ae.executionTimestamp,
              cleanStop = ae.cleanStop,
              agentExecutionDetails = IndexedSeq.empty)
          ))
        )) recover {
          case ex: Exception => InternalServerError("An error occurred while retrieving executions from agent: " + ex.getMessage)
        }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getAgentExecutionDetails(agentExecId: Int) = Action.async { implicit request =>
    val agentExecutionDetails = AgentExecutionDetails(executionDetailId = 0, agentExecId = agentExecId, fieldId = 0, value = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentExecutionDetailsRetrieveCommand(new DefaultAgentExecutionDetailsRepositoryImpl(), agentExecutionDetails))))
        .mapTo[AgentExecutionDetailsRetrieveResponse].map(s => {
            Ok(Json.toJson(AgentExecutionDetailsUIModel(field = MasterFieldUIModel(fieldId = s.response.fieldId, fieldName = "", fieldDescription = ""), value = s.response.value, fieldEnabled = false)))
          }) recover {
          case ex => InternalServerError("Error while retrieving details of this execution. Exception is: " + ex.getMessage)
        }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }
}

case class AgentExecutionUIModel(agentExecId: Int, command: String, deployment: DeploymentByRoleUIModel, masterType: MasterTypeUIModel, executionTimestamp: Long, cleanStop: Boolean, agentExecutionDetails: Seq[AgentExecutionDetailsUIModel])

case class AgentExecutionDetailsUIModel(field: MasterFieldUIModel, value: String, fieldEnabled: Boolean)