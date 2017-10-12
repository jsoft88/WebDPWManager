package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DpwRoles
import org.jc.dpwmanager.repository.DpwRolesRepository

import scala.concurrent.{ExecutionContext, Future}

case class DpwRoleRetrieveCommand(repository: DpwRolesRepository, entity: DpwRoles)(implicit ec: ExecutionContext) extends Command[String, DpwRoles, DpwRoleRetrieveResponse](repository, entity) {
  override def execute: Future[DpwRoleRetrieveResponse] = {
    repository.get(entity.roleId).map(DpwRoleRetrieveResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: DpwRoleRetrieveCommand"
}


case class DpwRoleRetrieveResponse(response: Option[DpwRoles]) extends CommandResponseWrapper[Option[DpwRoles]](response = response)