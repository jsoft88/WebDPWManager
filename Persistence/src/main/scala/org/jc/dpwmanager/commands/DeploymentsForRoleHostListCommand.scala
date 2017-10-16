package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{DeploymentByRole, DpwRoles}
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

case class DeploymentsForRoleHostListCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole) extends Command[Int, DeploymentByRole, DeploymentsForRoleHostListResponse](repository, entity){
  override def execute = {
    val dummyDpwRole = DpwRoles(roleId = entity.roleId, roleDescription = "", roleLabel = "")
    repository.getAllDeploymentsForRole(dummyDpwRole).map(DeploymentsForRoleHostListResponse(_)) recover {
      case ex => throw ex
    }
  }

  override def toString: String = "Command is: DeploymentsForRoleHostListCommand"
}

case class DeploymentsForRoleHostListResponse(response: Seq[DeploymentByRole]) extends CommandResponseWrapper[Seq[DeploymentByRole]](response)