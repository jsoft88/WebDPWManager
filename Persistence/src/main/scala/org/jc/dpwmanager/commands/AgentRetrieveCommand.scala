package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.Agent
import org.jc.dpwmanager.repository.AgentRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 29/8/2017.
  */
case class AgentRetrieveCommand(repository: AgentRepository, entity: Agent)(implicit ec: ExecutionContext) extends Command[Short, Agent, AgentRetrieveResponse](repository, entity) {
  override def execute: Future[AgentRetrieveResponse] = {
    repository.get(entity.agentId).map(a => AgentRetrieveResponse(a)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }
}

case class AgentRetrieveResponse(response: Option[Agent]) extends CommandResponseWrapper[Option[Agent]](response)