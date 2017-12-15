package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{DpwRoleConstraint, DpwRoles}
import org.jc.dpwmanager.repository.DpwRoleConstraintRepository

case class RetrieveDpwRolesExecutableCommand(repository: DpwRoleConstraintRepository, entity: DpwRoleConstraint) extends Command[Short, DpwRoleConstraint, RetrieveDpwRolesExecutableResponse](repository, entity) {
  override def execute = {
    repository.rolesWithExecutionPermission.map(RetrieveDpwRolesExecutableResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". Error is: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: RetrieveDpwRolesExecutableCommand"
}

case class RetrieveDpwRolesExecutableResponse(response: Seq[DpwRoles]) extends CommandResponseWrapper[Seq[DpwRoles]](response)
