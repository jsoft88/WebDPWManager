package controllers

import javax.inject.{Inject, Named, Singleton}

import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentExecutionDetailsRepositoryImpl, DefaultAgentExecutionRepositoryImpl}
import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetails}
import play.api.mvc._
import utils.ReachPersistenceAgentWith
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

  implicit val masterFieldWrites: Writes[MasterFieldUIModel] = (
    (JsPath \ "fieldId").write[Int] and
      (JsPath \ "fieldName").write[String] and
      (JsPath \ "fieldDescription").write[String]
  )(unlift(MasterFieldUIModel.unapply))

  implicit val agentExecutionDetailsWrites: Writes[AgentExecutionDetailsUIModel] = (
    (JsPath \ "field").write[MasterFieldUIModel] and
      (JsPath \ "value").write[String] and
      (JsPath \ "fieldEnabled").write[Boolean])(unlift(AgentExecutionDetailsUIModel.unapply))

  implicit val agentExecutionWrites: Writes[AgentExecutionUIModel] = (
    (JsPath \ "agentExecId").write[Int] and
      (JsPath \ "command").write[String] and
      (JsPath \ "agentId").write[Short] and
      (JsPath \ "masterType").write[MasterTypeUIModel] and
      (JsPath \ "executionTimestamp").write[Long] and
      (JsPath \ "cleanStop").write[Boolean] and
      (JsPath \ "agentExecutionDetails").write[Seq[AgentExecutionDetailsUIModel]])(unlift(AgentExecutionUIModel.unapply))

  def noBusinessActor: String = "No business actor available to fulfill this request"

  def listAllAgentExecutions = Action.async { implicit request =>
    val agentExecution = AgentExecution(agentExecId = 0, agentId = request.getQueryString("agentId").getOrElse("0").toShort, cleanStop = false, executionTimestamp = 0L, masterTypeId = 0, command = "")
    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentExecutionListCommand(new DefaultAgentExecutionRepositoryImpl(), agentExecution))))
        .mapTo[AgentExecutionListResponse]
        .map(s => Ok(
          Json.toJson(s.response.map(
            ae => AgentExecutionUIModel(agentExecId = ae.agentExecId, command = ae.command, agentId = ae.agentId, masterType = MasterTypeUIModel(masterTypeId = ae.masterTypeId, masterLabel = ""), executionTimestamp = 0L, cleanStop = ae.cleanStop, agentExecutionDetails = IndexedSeq.empty)
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

case class AgentExecutionUIModel(agentExecId: Int, command: String, agentId: Short, masterType: MasterTypeUIModel, executionTimestamp: Long, cleanStop: Boolean, agentExecutionDetails: Seq[AgentExecutionDetailsUIModel])

case class AgentExecutionDetailsUIModel(field: MasterFieldUIModel, value: String, fieldEnabled: Boolean)