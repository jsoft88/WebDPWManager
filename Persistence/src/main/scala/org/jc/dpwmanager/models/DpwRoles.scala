package org.jc.dpwmanager.models

import slick.lifted.{Rep, Tag}
import slick.jdbc.PostgresProfile.api._

case class DpwRoles(roleId: String, roleLabel: String, roleDescription: String) extends BaseModel

class DpwRolesTable(tag: Tag) extends Table[DpwRoles](tag, "dpw_roles") {

  def roleId: Rep[String] = column[String]("role_id", O.PrimaryKey)
  def roleLabel: Rep[String] = column[String]("role_label")
  def roleDescription: Rep[String] = column[String]("role_description")

  def * = (roleId, roleLabel, roleDescription) <> (DpwRoles.tupled, DpwRoles.unapply)
}
