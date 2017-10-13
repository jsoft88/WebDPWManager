package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{DeploymentByRole, Host}
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

case class DeploymentsForHostListCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole) extends Command[Int, DeploymentByRole, DeploymentsForHostListResponse](repository, entity){
  override def execute = {
    val dummyHost = Host(hostId = entity.hostId, address = "")
    repository.getAllDeploymentsInHost(dummyHost).map(DeploymentsForHostListResponse(_)) recover {
      case ex => throw ex
    }
  }

  override def toString: String = "Command is: DeploymentsForHostListCommand"
}

case class DeploymentsForHostListResponse(response: Seq[DeploymentByRole]) extends CommandResponseWrapper[Seq[DeploymentByRole]](response = response)
