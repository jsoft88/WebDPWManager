package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DpwRoles
import org.jc.dpwmanager.repository.DpwRolesRepository

import scala.concurrent.ExecutionContext

case class SingleDpwRoleRetrieveCommand(repository: DpwRolesRepository, entity: DpwRoles)(implicit ec: ExecutionContext) extends Command[String, DpwRoles, SingleDpwRoleRetrieveResponse](repository, entity){
  override def execute = {
    repository.get(entity.roleId).map(SingleDpwRoleRetrieveResponse(_)) recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: SingleDpwRoleRetrieve"
}

case class SingleDpwRoleRetrieveResponse(response: Option[DpwRoles]) extends CommandResponseWrapper[Option[DpwRoles]](response)