package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultAgentExecutionDetailsRepositoryImpl, DefaultAgentExecutionRepositoryImpl}
import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetails}
import play.api.mvc._
import utils.{ReachPersistenceAgentWith, ReadsAndWrites}
import akka.pattern.ask
import akka.util.Timeout
import initialization.InitialConfiguration
import org.jc.dpwmanager.interaction.ExecutableFactory
import org.jc.dpwmanager.util.{DeployMasterCompleted, DeployMasterStart, MessageWrapper}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by jorge on 26/7/2017.
  */

@Singleton
class AgentExecutionController @Inject()(@Named("businessActor") businessActor: ActorRef, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with ReadsAndWrites {

  implicit val timeout = Timeout(15 seconds)

  InitialConfiguration.businessActor_(Some(businessActor))

  /*implicit val masterFieldWrites: Writes[MasterFieldUIModel] = ReadsAndWrites.masterFieldWrites

  implicit val agentExecutionDetailsWrites: Writes[AgentExecutionDetailsUIModel] = ReadsAndWrites.agentExecutionDetailsWrites

  implicit val dpwRoleWrites: Writes[DpwRolesUIModel] = ReadsAndWrites.dpwRolesWrites

  implicit val deploymentByRoleWrites: Writes[DeploymentByRoleUIModel] = ReadsAndWrites.deploymentByRoleWrites

  implicit val agentExecutionWrites: Writes[AgentExecutionUIModel] = ReadsAndWrites.agentExecutionWrites

  implicit val agentExecutionReads: Reads[AgentExecutionUIModel] = ReadsAndWrites.agentExecutionReads*/

  implicit val addExecutionResponseWrites: Writes[AddExecutionResponse] = (
    (JsPath \ "agentExecution").write[AgentExecutionUIModel] and
      (JsPath \ "errors").write[Seq[String]]
  )(unlift(AddExecutionResponse.unapply))

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
              agentExecutionDetails = IndexedSeq.empty, deployId = ae.deployId)
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
            Ok(Json.toJson(s.response.map(det => AgentExecutionDetailsUIModel(field = MasterFieldUIModel(fieldId = det.fieldId, fieldDescription = "", fieldEnabled = false, fieldName = ""), value = det.value))))
          }) recover {
          case ex => InternalServerError("Error while retrieving details of this execution. Exception is: " + ex.getMessage)
        }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def addNewExecution = Action(parse.json).async { request =>
    val agentExecutionResult = request.body.validate[AgentExecutionUIModel]
    agentExecutionResult.fold(
      errors => {
        val errAgentExecution = AgentExecutionUIModel(
          agentExecId = 0,
          command = "",
          executionTimestamp = 0L,
          deployment = DeploymentByRoleUIModel(
            deployId = 0,
            actorName = "",
            actorSystemName = "",
            port = 0,
            role = DpwRolesUIModel(roleId = "", roleLabel = "", roleDescription = ""),
            componentId = 0),
          masterType = MasterTypeUIModel(masterTypeId = 0, masterLabel = ""),
          cleanStop = false,
          agentExecutionDetails = Seq.empty[AgentExecutionDetailsUIModel], deployId = 0)

        Future.successful(
          InternalServerError(
            Json.toJson(AddExecutionResponse(errAgentExecution, errors = Seq(JsError.toJson(errors).toString())))))
      },
      agentExecution => {
        InitialConfiguration.businessActor match {
          case Some(ar) => (ar ? DeployMasterStart(MessageWrapper(execArgs = ExecutableFactory.getExecutable(masterTypeId = 0, masterTypeLabel = ExecutableFactory.DUMMY_EXECUTABLE).head, deployId = agentExecution.deployment.deployId, actorName = Some(agentExecution.deployment.actorName), masterTypeId = agentExecution.masterType.masterTypeId, address = "", port = agentExecution.deployment.port)))
            .mapTo[DeployMasterCompleted].flatMap(deployCompleted => {
            val newAgentExec = AgentExecution(agentExecId = 0, command = deployCompleted.msgWrapper.execArgs.toString, cleanStop = false, executionTimestamp = System.currentTimeMillis(), deployId = agentExecution.deployment.deployId, masterTypeId = agentExecution.masterType.masterTypeId)
            (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentExecutionInsertCommand(new DefaultAgentExecutionRepositoryImpl(), newAgentExec)))).mapTo[AgentExecutionInsertResponse].map(s => Ok(Json.toJson(AddExecutionResponse(agentExecution.copy(agentExecId = s.response.agentExecId, executionTimestamp = s.response.executionTimestamp), errors = Seq.empty))))
              .recover {
                case ex => InternalServerError(Json.toJson(AddExecutionResponse(agentExecution = agentExecution, errors = Seq(ex.getMessage))))
              }
          }).recover {
            case ex => InternalServerError(Json.toJson(AddExecutionResponse(agentExecution = agentExecution, errors = Seq("Failed to launch master in selected deployment", ex.getMessage))))
          }
          case None => Future.successful(InternalServerError(noBusinessActor))
        }
      })
  }
}

case class AddExecutionResponse(agentExecution: AgentExecutionUIModel, errors: Seq[String])

case class AgentExecutionUIModel(agentExecId: Int, command: String, deployment: DeploymentByRoleUIModel, masterType: MasterTypeUIModel, executionTimestamp: Long, cleanStop: Boolean, agentExecutionDetails: Seq[AgentExecutionDetailsUIModel], deployId: Int)

case class AgentExecutionDetailsUIModel(field: MasterFieldUIModel, value: String)