package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._

case class DpwRoleConstraint(constraintId: Short, roleId: String, canExecute: Boolean) extends BaseModel

class DpwRoleConstraintTable(tag: Tag) extends Table[DpwRoleConstraint](tag, "role_constraint") {

  val dpwRoles = TableQuery[DpwRolesTable]

  def constraintId: Rep[Short] = column[Short]("constraint_id", O.PrimaryKey, O.AutoInc, O.Unique)

  def roleId: Rep[String] = column[String]("role_id")

  def canExecute: Rep[Boolean] = column[Boolean]("can_execute")

  def * = (constraintId, roleId, canExecute) <> (DpwRoleConstraint.tupled, DpwRoleConstraint.unapply)

  def rolesFk = foreignKey("role_fk", roleId, dpwRoles)(_.roleId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
}
