package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.Agent
import org.jc.dpwmanager.repository.AgentRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 3/7/2017.
  */
case class AgentInsertCommand(repository: AgentRepository, entity: Agent)(implicit ec: ExecutionContext) extends Command[Short, Agent, AgentInsertResponse](repository, entity) {

  override def execute: Future[AgentInsertResponse] = {
    repository.save(entity).map(a => AgentInsertResponse(a)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: AgentInsertCommand"
}

case class AgentInsertResponse(response: Agent) extends CommandResponseWrapper[Agent](response)
