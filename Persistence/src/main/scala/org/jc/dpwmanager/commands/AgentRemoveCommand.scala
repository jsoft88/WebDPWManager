package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.Agent
import org.jc.dpwmanager.repository.AgentRepository

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 29/8/2017.
  */
case class AgentRemoveCommand(repository: AgentRepository, entity: Agent)(implicit ec: ExecutionContext) extends Command[Short, Agent, AgentRemoveResponse](repository, entity){
  override def execute: Future[AgentRemoveResponse] = {
    repository.delete(entity).map(count => AgentRemoveResponse(count != 0)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }
}

case class AgentRemoveResponse(response: Boolean) extends CommandResponseWrapper[Boolean](response)
