package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetails}
import org.jc.dpwmanager.repository.{AgentExecutionDetailsRepository}

case class AgentExecutionDetailsRetrieveCommand(repository: AgentExecutionDetailsRepository, entity: AgentExecutionDetails) extends Command[Int, AgentExecutionDetails, AgentExecutionDetailsRetrieveResponse](repository, entity) {
  override def execute = {
    repository.getSingleExecutionDetails(entity.agentExecId).map(AgentExecutionDetailsRetrieveResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: AgentExecutionDetailsRetrieveCommand"
}

case class AgentExecutionDetailsRetrieveResponse(response: AgentExecutionDetails) extends CommandResponseWrapper[AgentExecutionDetails](response = response)