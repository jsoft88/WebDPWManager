package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.AgentExecution
import org.jc.dpwmanager.repository.AgentExecutionRepository
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jorge on 10/7/2017.
  */
case class MarkMasterExecutionAsCleanStop(repository: AgentExecutionRepository, entity: AgentExecution)(implicit ec: ExecutionContext) extends Command[Int, AgentExecution, MasterExecutionAsCleanStopResponse](repository, entity) {
  override def execute: Future[MasterExecutionAsCleanStopResponse] = {
    repository.update(entity).map(MasterExecutionAsCleanStopResponse(_)) recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }
}

case class MasterExecutionAsCleanStopResponse(response: Int) extends CommandResponseWrapper[Int](response)
