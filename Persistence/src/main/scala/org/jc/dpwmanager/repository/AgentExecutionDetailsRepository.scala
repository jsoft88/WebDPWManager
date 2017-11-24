package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.AgentExecutionDetails

import scala.concurrent.Future

trait AgentExecutionDetailsRepository extends BaseRepository[Int, AgentExecutionDetails]{
  def getExecutionDetails(agentExecId: Int): Future[Seq[AgentExecutionDetails]]
}
