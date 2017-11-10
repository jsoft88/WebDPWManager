package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DeploymentByRole
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

import scala.concurrent.ExecutionContext

case class DeploymentByRoleRemoveCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole)(implicit ec: ExecutionContext) extends Command[Int, DeploymentByRole, DeploymentByRoleRemoveResponse](repository, entity) {
  override def execute = {
    repository.delete(entity) map(DeploymentByRoleRemoveResponse(_)) recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: DeploymentByRoleRemove"
}

case class DeploymentByRoleRemoveResponse(response: Int) extends CommandResponseWrapper[Int](response)