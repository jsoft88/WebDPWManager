package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DpwRoles
import org.jc.dpwmanager.repository.DpwRolesRepository

case class SingleDpwRoleRetrieveCommand(repository: DpwRolesRepository, entity: DpwRoles) extends Command[String, DpwRoles, SingleDpwRoleRetrieveResponse](repository, entity){
  override def execute = {
    repository.get(entity.roleId).map(SingleDpwRoleRetrieveResponse(_)) recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: SingleDpwRoleRetrieve"
}

case class SingleDpwRoleRetrieveResponse(response: Option[DpwRoles]) extends CommandResponseWrapper[Option[DpwRoles]](response)