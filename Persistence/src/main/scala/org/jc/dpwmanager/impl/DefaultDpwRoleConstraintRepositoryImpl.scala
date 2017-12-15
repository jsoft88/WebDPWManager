package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{DpwRoleConstraint, DpwRoleConstraintTable, DpwRoles, DpwRolesTable}
import org.jc.dpwmanager.repository.DpwRoleConstraintRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class DefaultDpwRoleConstraintRepositoryImpl(implicit ec: ExecutionContext) extends DpwRoleConstraintRepository with Database {

  val dpwRoleConstraints = TableQuery[DpwRoleConstraintTable]

  val dpwRoles = TableQuery[DpwRolesTable]

  db.run(DBIO.seq(dpwRoleConstraints.schema.create)) onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("DpwRoleConstraint schema creation failed: " + ex.getMessage)
  }

  override def canRoleExecuteProcess(dpwRoleConstraint: DpwRoleConstraint) = db.run(dpwRoleConstraints.filter(_.roleId === dpwRoleConstraint.roleId).map(_.canExecute).result head)

  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  override def save(entity: DpwRoleConstraint) = Future.successful(entity)

  override def delete(entity: DpwRoleConstraint) = Future.successful(1)

  override def update(entity: DpwRoleConstraint) = Future.successful(1)

  override def get(id: Short) = db.run(dpwRoleConstraints.filter(_.constraintId === id).result headOption)

  override def rolesWithExecutionPermission = {
    db.run(
      (for {
        (rc, dpw) <- dpwRoleConstraints filter(_.canExecute === true) join dpwRoles on (_.roleId === _.roleId)
      } yield dpw).result)
  }
}
