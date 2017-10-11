package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._

case class AgentExecutionDetails(executionDetailId: Int, agentExecId: Int, value: String, fieldId: Int) extends BaseModel

class AgentExecutionDetailsTable(tag: Tag) extends Table[AgentExecutionDetails](tag, "agent_execution_details"){
  def executionDetailId: Rep[Int] = column[Int]("execution_detail_id", O.PrimaryKey, O.Unique, O.AutoInc)
  def agentExecId: Rep[Int] = column[Int]("agent_exec_id")
  def value: Rep[String] = column[String]("value")
  def fieldId: Rep[Int] = column[Int]("field_id")

  def * = (executionDetailId, agentExecId, value, fieldId) <> (AgentExecutionDetails.tupled, AgentExecutionDetails.unapply)

}
