package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.Host
import org.jc.dpwmanager.repository.HostRepository

case class HostListRetrieveCommand(repository: HostRepository, entity: Host) extends Command[Short, Host, HostListRetrieveResponse](repository, entity){
  override def execute = {
    repository.getHosts().map(HostListRetrieveResponse(_)) recover {
      case ex => throw ex
    }
  }

  override def toString: String = "Command is: HostListRetrieveCommand"
}

case class HostListRetrieveResponse(response: Seq[Host]) extends CommandResponseWrapper[Seq[Host]](response)