package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{DeploymentByRole, DpwRoles}
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

case class RetrieveDeploymentsForRoleBySystemCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole) extends Command[Int, DeploymentByRole, RetrieveDeploymentsForRoleBySystemResponse](repository, entity){
  override def execute = {
    repository.getAllDeploymentsForRoleInActorSystem(dpwRole = DpwRoles(roleId = entity.roleId, roleLabel = "", roleDescription = ""), actorSystemName = entity.actorSystemName)
      .map(RetrieveDeploymentsForRoleBySystemResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: RetrieveDeploymentsForRoleBySystemCommand"
}

case class RetrieveDeploymentsForRoleBySystemResponse(response: Seq[DeploymentByRole]) extends CommandResponseWrapper[Seq[DeploymentByRole]](response)
