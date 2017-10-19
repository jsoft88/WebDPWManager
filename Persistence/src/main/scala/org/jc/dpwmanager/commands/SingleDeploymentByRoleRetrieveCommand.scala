package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DeploymentByRole
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

case class SingleDeploymentByRoleRetrieveCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole) extends Command[Int, DeploymentByRole, SingleDeploymentByRoleRetrieveResponse](repository, entity){
  override def execute = {
    repository.get(entity.deployId).map(SingleDeploymentByRoleRetrieveResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: SingleDeploymentByRoleRetrieve"
}

case class SingleDeploymentByRoleRetrieveResponse(response: Option[DeploymentByRole]) extends CommandResponseWrapper[Option[DeploymentByRole]](response)
