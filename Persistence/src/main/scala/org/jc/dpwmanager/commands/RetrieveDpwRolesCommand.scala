package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DpwRoles
import org.jc.dpwmanager.repository.DpwRolesRepository

import scala.concurrent.{ExecutionContext, Future}

case class RetrieveDpwRolesCommand(repository: DpwRolesRepository, entity: DpwRoles)(implicit ec: ExecutionContext) extends Command[String, DpwRoles, RetrieveDpwRolesResponse](repository, entity) {
  override def execute: Future[RetrieveDpwRolesResponse] = {
    repository.getRoles().map(RetrieveDpwRolesResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString = "Command is: RetrieveDpwRolesCommand"
}

case class RetrieveDpwRolesResponse(response: Seq[DpwRoles]) extends CommandResponseWrapper[Seq[DpwRoles]](response = response)
