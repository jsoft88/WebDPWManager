package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DeploymentByRole
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

case class DeploymentInsertCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole) extends Command[Int, DeploymentByRole, DeploymentInsertResponse](repository, entity){
  override def execute = {
    repository.save(entity).map(DeploymentInsertResponse(_)) recover {
      case ex => throw ex
    }
  }

  override def toString: String = "Command is: DeploymentInsertCommand"
}

case class DeploymentInsertResponse(response: DeploymentByRole) extends CommandResponseWrapper[DeploymentByRole](response)
