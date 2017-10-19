package org.jc.dpwmanager.models

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

/**
  * Created by jorge on 11/7/2017.
  */
case class AgentExecution(agentExecId: Int, command: String, cleanStop: Boolean, executionTimestamp: Long, deployId: Int, masterTypeId: Short) extends BaseModel

class AgentExecutionTable(tag: Tag) extends Table[AgentExecution](tag, "execution") {
  val masterTypes = TableQuery[MasterTypeTable]
  val deploymentsByRole = TableQuery[DeploymentByRoleTable]

  def agentExecId: Rep[Int] = column[Int]("agent_exec_id", O.PrimaryKey, O.AutoInc)
  def command: Rep[String] = column[String]("command")
  def cleanStop: Rep[Boolean] = column[Boolean]("clean_stop")
  def executionTimestamp: Rep[Long] = column[Long]("execution_timestamp")
  def deployId: Rep[Int] = column[Int]("deploy_id")
  def masterTypeId: Rep[Short] = column[Short]("masterTypeId")

  def * = (agentExecId, command, cleanStop, executionTimestamp, deployId, masterTypeId) <> (AgentExecution.tupled, AgentExecution.unapply)

  def masterTypesFk = foreignKey("master_type_fk", masterTypeId, masterTypes)(_.masterTypeId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  def agentFk = foreignKey("agent_execution_fk", deployId, deploymentsByRole)(_.deployId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
}
