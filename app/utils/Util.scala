package utils

import controllers._
import org.jc.dpwmanager.commands.CommandWrapper
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, JsonValidationError, Reads, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by jorge on 24/7/2017.
  */
case class ReachPersistenceAgentWith(command: CommandWrapper)

object ReadsAndWrites {
  val addressFormat: Reads[String] = Reads.StringReads.filter(JsonValidationError("Invalid IP address."))(str => str.matches("""\d{1, 3}\.\d{1, 3}\.\d{1, 3}\.d{1, 3}"""))

  val masterTypeReads: Reads[MasterTypeUIModel] = (
    (JsPath \ "masterTypeId").read[Short] and
      (JsPath \ "masterLabel").read[String]
    )(MasterTypeUIModel.apply _)

  val masterFieldReads: Reads[MasterFieldUIModel] = (
    (JsPath \ "fieldId").read[Int] and
      (JsPath \ "fieldName").read[String] and
      (JsPath \ "fieldDescription").read[String]
    )(MasterFieldUIModel.apply _)

  val executionDetailsReads: Reads[AgentExecutionDetailsUIModel] = (
    (JsPath \ "field").read[MasterFieldUIModel] and
      (JsPath \ "value").read[String] and
      (JsPath \ "fieldEnabled").read[Boolean]
    )(AgentExecutionDetailsUIModel.apply _)

  val agentExecutionReads: Reads[AgentExecutionUIModel] = (
    (JsPath \ "agentExecId").read[Int] and
      (JsPath \ "command").read[String] and
      (JsPath \ "deployment").read[DeploymentByRoleUIModel] and
      (JsPath \ "masterType").read[MasterTypeUIModel] and
      (JsPath \ "executionTimestamp").read[Long] and
      (JsPath \ "cleanStop").read[Boolean] and
      (JsPath \ "agentExecutionDetails").read[Seq[AgentExecutionDetailsUIModel]]
    )(AgentExecutionUIModel.apply _)

  val hostReads: Reads[HostUIModel] = (
    (JsPath \ "hostId").read[Short] and
    (JsPath \ "address").read[String](addressFormat) and
      (JsPath \ "deployments").read[Seq[DeploymentByRoleUIModel]] and
      (JsPath \ "executions").read[Seq[AgentExecutionUIModel]]
    )(HostUIModel.apply _)

  val deploymentByRoleReads: Reads[DeploymentByRoleUIModel] = (
    (JsPath \ "deployId").read[Int] and
      (JsPath \ "actorName").read[String] and
      (JsPath \ "actorSystemName").read[String] and
      (JsPath \ "port").read[Short] and
      (JsPath \ "role").read[DpwRolesUIModel] and
      (JsPath \ "componentId").read[Short]
  )(DeploymentByRoleUIModel)

  val dpwRolesWrites: Writes[DpwRolesUIModel] = (
    (JsPath \ "roleId").write[String] and
      (JsPath \ "roleLabel").write[String] and
      (JsPath \ "roleDescription").write[String]
    )(unlift(DpwRolesUIModel.unapply))

  val deploymentByRoleWrites: Writes[DeploymentByRoleUIModel] = (
    (JsPath \ "deployId").write[Int] and
      (JsPath \ "actorName").write[String] and
      (JsPath \ "actorSystemName").write[String] and
      (JsPath \ "port").write[Short] and
      (JsPath \ "role").write[DpwRolesUIModel] and
      (JsPath \ "componentId").write[Short]
    )(unlift(DeploymentByRoleUIModel.unapply))

  val hostWrites: Writes[HostUIModel] = (
    (JsPath \ "hostId").write[Short] and
      (JsPath \ "address").write[String] and
      (JsPath \ "deployments").write[Seq[DeploymentByRoleUIModel]] and
      (JsPath \ "executions").write[Seq[AgentExecutionUIModel]]
    )(unlift(HostUIModel.unapply))

  val masterFieldWrites: Writes[MasterFieldUIModel] = (
    (JsPath \ "fieldId").write[Int] and
      (JsPath \ "fieldName").write[String] and
      (JsPath \ "fieldDescription").write[String]
    )(unlift(MasterFieldUIModel.unapply))

  val agentExecutionDetailsWrites: Writes[AgentExecutionDetailsUIModel] = (
    (JsPath \ "field").write[MasterFieldUIModel] and
      (JsPath \ "value").write[String] and
      (JsPath \ "fieldEnabled").write[Boolean])(unlift(AgentExecutionDetailsUIModel.unapply))

  val agentExecutionWrites: Writes[AgentExecutionUIModel] = (
    (JsPath \ "agentExecId").write[Int] and
      (JsPath \ "command").write[String] and
      (JsPath \ "deployment").write[DeploymentByRoleUIModel] and
      (JsPath \ "masterType").write[MasterTypeUIModel] and
      (JsPath \ "executionTimestamp").write[Long] and
      (JsPath \ "cleanStop").write[Boolean] and
      (JsPath \ "agentExecutionDetails").write[Seq[AgentExecutionDetailsUIModel]])(unlift(AgentExecutionUIModel.unapply))
}
