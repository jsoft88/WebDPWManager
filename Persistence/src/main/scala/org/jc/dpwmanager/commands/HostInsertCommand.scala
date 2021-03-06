package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.Host
import org.jc.dpwmanager.repository.HostRepository

import scala.concurrent.ExecutionContext

case class HostInsertCommand(repository: HostRepository, entity: Host)(implicit ec: ExecutionContext) extends Command[Short, Host, HostInsertResponse](repository, entity){
  override def execute = {
    repository.save(entity).map(HostInsertResponse(_)) recover {
      case ex => throw ex
    }
  }

  override def toString: String = "Command is: HostInsertCommand"
}

case class HostInsertResponse(response: Host) extends CommandResponseWrapper[Host](response)