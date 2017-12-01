package utils

import controllers._
import org.jc.dpwmanager.commands.CommandWrapper
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, JsonValidationError, Reads, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by jorge on 24/7/2017.
  */
object LackOfRolesConstants {
  final val NO_SERVER = 100
  final val NO_AGENT = 200
  final val NO_PERSISTENCE = 300
  final val NO_BUSINESS = 400
}

class LackOfRoleException(errorCode: Int, message: String) extends Exception {
  override def getMessage: String = this.message

  def getErrorCode: Int = this.errorCode
}

case class ReachPersistenceAgentWith(command: CommandWrapper)

case class LackOfRolesResponse(errorCode: Int, errorDescription: String) {
  override def toString: String = s"$errorCode&$errorDescription"

  def _errorCode(msg: Option[String]): Int = msg match { case Some(s) => s.split("&").head.toInt case None => this.toString.split("&").head.toInt }

  def _errorDescription(msg: Option[String]): String = msg match { case None => this.toString.split("&").tail.head case Some(s) => s.split("&").tail.head }

  def _toJson(msg: Option[String]): String = msg match {
    case Some(_) => {
      val errC = this._errorCode(msg)
      val errD = this._errorDescription(msg)

      s"""{"errorCode": $errC, "errorDescription": "$errD"}"""
    }
    case None => s"""{"errorCode": $errorCode, "errorDescription": "$errorDescription"}"""
  }
}

trait ReadsAndWrites {
  implicit val lackOfRolesReads: Reads[LackOfRolesResponse] = (
    (JsPath \ "errorCode").read[Int] and
      (JsPath \ "errorDescription").read[String]
  )(LackOfRolesResponse.apply _)

  implicit val lackOfRolesWrites: Writes[LackOfRolesResponse] = (
    (JsPath \ "errorCode").write[Int] and
      (JsPath \ "errorDescription").write[String]
  )(unlift(LackOfRolesResponse.unapply))

  implicit val masterTypeReads: Reads[MasterTypeUIModel] = (
    (JsPath \ "masterTypeId").read[Short] and
      (JsPath \ "masterLabel").read[String]
    )(MasterTypeUIModel.apply _)

  implicit val masterTypeWrites: Writes[MasterTypeUIModel] = (
    (JsPath \ "masterTypeId").write[Short] and
      (JsPath \ "masterLabel").write[String]
  )(unlift(MasterTypeUIModel.unapply))

  implicit val masterFieldReads: Reads[MasterFieldUIModel] = (
    (JsPath \ "fieldId").read[Int] and
      (JsPath \ "fieldName").read[String] and
      (JsPath \ "fieldDescription").read[String] and
      (JsPath \ "fieldEnabled").read[Boolean]
    )(MasterFieldUIModel.apply _)

  implicit val dpwRolesReads: Reads[DpwRolesUIModel] = (
    (JsPath \ "roleId").read[String] and
      (JsPath \ "roleLabel").read[String] and
      (JsPath \ "roleDescription").read[String]
    )(DpwRolesUIModel.apply _)

  implicit val deploymentByRoleReads: Reads[DeploymentByRoleUIModel] = (
    (JsPath \ "deployId").read[Int] and
      (JsPath \ "actorName").read[String] and
      (JsPath \ "actorSystemName").read[String] and
      (JsPath \ "port").read[Short] and
      (JsPath \ "role").read[DpwRolesUIModel] and
      (JsPath \ "componentId").read[Short]
    )(DeploymentByRoleUIModel.apply _)

  implicit val executionDetailsReads: Reads[AgentExecutionDetailsUIModel] = (
    (JsPath \ "field").read[MasterFieldUIModel] and
      (JsPath \ "value").read[String]
    )(AgentExecutionDetailsUIModel.apply _)

  implicit val agentExecutionReads: Reads[AgentExecutionUIModel] = (
    (JsPath \ "agentExecId").read[Int] and
      (JsPath \ "command").read[String] and
      (JsPath \ "deployment").read[DeploymentByRoleUIModel] and
      (JsPath \ "masterType").read[MasterTypeUIModel] and
      (JsPath \ "executionTimestamp").read[Long] and
      (JsPath \ "cleanStop").read[Boolean] and
      (JsPath \ "agentExecutionDetails").read[Seq[AgentExecutionDetailsUIModel]] and
      (JsPath \ "deployId").read[Int]
    )(AgentExecutionUIModel.apply _)

  implicit val hostReads: Reads[HostUIModel] = (
    (JsPath \ "hostId").read[Short] and
    (JsPath \ "address").read[String] and
      (JsPath \ "deployments").read[List[DeploymentByRoleUIModel]] and
      (JsPath \ "executions").read[List[AgentExecutionUIModel]]
    )(HostUIModel.apply _)


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

  implicit val masterFieldWrites: Writes[MasterFieldUIModel] = (
    (JsPath \ "fieldId").write[Int] and
      (JsPath \ "fieldName").write[String] and
      (JsPath \ "fieldDescription").write[String] and
      (JsPath \ "fieldEnabled").write[Boolean]
    )(unlift(MasterFieldUIModel.unapply))

  implicit val agentExecutionDetailsWrites: Writes[AgentExecutionDetailsUIModel] = (
    (JsPath \ "field").write[MasterFieldUIModel] and
      (JsPath \ "value").write[String])(unlift(AgentExecutionDetailsUIModel.unapply))

  implicit val agentExecutionWrites: Writes[AgentExecutionUIModel] = (
    (JsPath \ "agentExecId").write[Int] and
      (JsPath \ "command").write[String] and
      (JsPath \ "deployment").write[DeploymentByRoleUIModel] and
      (JsPath \ "masterType").write[MasterTypeUIModel] and
      (JsPath \ "executionTimestamp").write[Long] and
      (JsPath \ "cleanStop").write[Boolean] and
      (JsPath \ "agentExecutionDetails").write[Seq[AgentExecutionDetailsUIModel]] and
      (JsPath \ "deployId").write[Int])(unlift(AgentExecutionUIModel.unapply))

  implicit val hostWrites: Writes[HostUIModel] = (
    (JsPath \ "hostId").write[Short] and
      (JsPath \ "address").write[String] and
      (JsPath \ "deployments").write[List[DeploymentByRoleUIModel]] and
      (JsPath \ "executions").write[List[AgentExecutionUIModel]]
    )(unlift(HostUIModel.unapply))
}
