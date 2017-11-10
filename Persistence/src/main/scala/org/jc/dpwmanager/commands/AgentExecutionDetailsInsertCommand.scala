package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.AgentExecutionDetails
import org.jc.dpwmanager.repository.AgentExecutionDetailsRepository

import scala.concurrent.ExecutionContext

case class AgentExecutionDetailsInsertCommand(repository: AgentExecutionDetailsRepository, entity: AgentExecutionDetails)(implicit ec: ExecutionContext) extends Command[Int, AgentExecutionDetails, AgentExecutionDetailsInsertResponse](repository, entity){
  override def execute = {
    repository.save(entity).map(AgentExecutionDetailsInsertResponse(_)).recover {
      case ex => throw new Exception("Command " + this.toString + ", failed with error: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: AgentExecutionDetailsInsertCommand"
}

case class AgentExecutionDetailsInsertResponse(response: AgentExecutionDetails) extends CommandResponseWrapper[AgentExecutionDetails](response)