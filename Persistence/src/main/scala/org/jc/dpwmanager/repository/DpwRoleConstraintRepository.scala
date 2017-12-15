package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.{DpwRoleConstraint, DpwRoles}

import scala.concurrent.Future

trait DpwRoleConstraintRepository extends BaseRepository[Short, DpwRoleConstraint]{
  def canRoleExecuteProcess(dpwRoleConstraint: DpwRoleConstraint): Future[Boolean]

  def rolesWithExecutionPermission:Future[Seq[DpwRoles]]
}
