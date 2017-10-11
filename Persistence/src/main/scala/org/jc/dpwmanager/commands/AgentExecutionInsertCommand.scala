package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.AgentExecution
import org.jc.dpwmanager.repository.AgentExecutionRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 14/7/2017.
  */
case class AgentExecutionInsertCommand(repository: AgentExecutionRepository, entity: AgentExecution)(implicit ec: ExecutionContext) extends Command[Int, AgentExecution, AgentExecutionInsertResponse](repository, entity) {
  override def execute: Future[AgentExecutionInsertResponse] = {
    repository.save(entity).map(AgentExecutionInsertResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". It failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: AgentExecutionInsertCommand"
}

case class AgentExecutionInsertResponse(response: AgentExecution) extends CommandResponseWrapper[AgentExecution](response)
