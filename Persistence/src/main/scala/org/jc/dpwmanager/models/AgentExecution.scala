package org.jc.dpwmanager.models

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

/**
  * Created by jorge on 11/7/2017.
  */
case class AgentExecution(agentExecId: Int, command: String, cleanStop: Boolean, executionTimestamp: Long, agentId: Short, masterTypeId: Short) extends BaseModel

class AgentExecutionTable(tag: Tag) extends Table[AgentExecution](tag, "execution") {
  val masterTypes = TableQuery[MasterTypeTable]
  val agent = TableQuery[AgentTable]

  def agentExecId: Rep[Int] = column[Int]("agent_exec_id", O.PrimaryKey, O.AutoInc)
  def command: Rep[String] = column[String]("command")
  def cleanStop: Rep[Boolean] = column[Boolean]("clean_stop")
  def executionTimestamp: Rep[Long] = column[Long]("execution_timestamp")
  def agentId: Rep[Short] = column[Short]("agent_id")
  def masterTypeId: Rep[Short] = column[Short]("masterTypeId")

  def * = (agentExecId, command, cleanStop, executionTimestamp, agentId, masterTypeId) <> (AgentExecution.tupled, AgentExecution.unapply)

  def masterTypesFk = foreignKey("master_type_fk", masterTypeId, masterTypes)(_.masterTypeId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  def agentFk = foreignKey("agent_execution_fk", agentId, agent)(_.agentId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
}
