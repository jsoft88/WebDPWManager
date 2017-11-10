package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{DeploymentByRole, Host}
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

import scala.concurrent.ExecutionContext

case class HostsPerClusterRetrieveCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole)(implicit ec: ExecutionContext) extends Command[Int, DeploymentByRole, HostsPerClusterRetrieveResponse](repository, entity){
  override def execute = {
    repository getActorSystemHosts(entity.actorSystemName) map(HostsPerClusterRetrieveResponse(_)) recover { case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)}
  }

  override def toString: String = "Command is: HostsPerClusterRetrieve"
}

case class HostsPerClusterRetrieveResponse(response: Seq[Host]) extends CommandResponseWrapper[Seq[Host]](response)
