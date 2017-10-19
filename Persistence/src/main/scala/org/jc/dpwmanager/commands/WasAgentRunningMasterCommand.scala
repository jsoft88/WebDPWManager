package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.AgentExecution
import org.jc.dpwmanager.repository.AgentExecutionRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 10/7/2017.
  */
class WasAgentRunningMasterCommand(repository: AgentExecutionRepository, entity: AgentExecution)(implicit ec: ExecutionContext) extends Command[Int, AgentExecution, WasAgentRunningMasterResponse](repository, entity) {
  val dummyExecution = AgentExecution(agentExecId = 0, command = "", masterTypeId = 0, cleanStop = false, deployId = entity.deployId, executionTimestamp = 0L)

  override def execute: Future[WasAgentRunningMasterResponse] = {
    repository.getUnstoppedMasterForAgent(entity.deployId).map(u => u headOption match { case Some(me) => WasAgentRunningMasterResponse(me) case None => WasAgentRunningMasterResponse(dummyExecution) }) recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: WasAgentRunningMasterCommand"
}

case class WasAgentRunningMasterResponse(response: AgentExecution) extends CommandResponseWrapper[AgentExecution](response)