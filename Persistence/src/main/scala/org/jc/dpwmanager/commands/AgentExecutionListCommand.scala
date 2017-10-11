package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.AgentExecution
import org.jc.dpwmanager.repository.AgentExecutionRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 25/7/2017.
  */
case class AgentExecutionListCommand(repository: AgentExecutionRepository, entity: AgentExecution)(implicit ec: ExecutionContext) extends Command[Int, AgentExecution, AgentExecutionListResponse](repository, entity){
  override def execute: Future[AgentExecutionListResponse] = {
    repository.getAllMastersForAgent(entity.agentId).map(ae => AgentExecutionListResponse(ae)).recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: AgentExecutionListCommand"
}

case class AgentExecutionListResponse(response: Seq[AgentExecution]) extends CommandResponseWrapper[Seq[AgentExecution]](response)
