package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._

case class DeploymentByRole(deployId: Int, hostId: Short, actorName: String, actorSystemName: String, roleId: String, componentId: Short, port: Short) extends BaseModel

class DeploymentByRoleTable(tag: Tag) extends Table[DeploymentByRole](tag, "deployment_by_role"){

  val dpwRoles = TableQuery[DpwRolesTable]

  val hosts = TableQuery[HostTable]

  def deployId: Rep[Int] = column[Int]("deploy_id", O.Unique, O.AutoInc, O.PrimaryKey)

  def hostId: Rep[Short] = column[Short]("host_id")

  def actorName: Rep[String] = column[String]("actor_name")

  def actorSystemName: Rep[String] = column[String]("actor_system_name")

  def roleId: Rep[String] = column[String]("role_id")

  def componentId: Rep[Short] = column[Short]("component_id", O.AutoInc)

  def port: Rep[Short] = column[Short]("port")

  def * = (deployId, hostId, actorName, actorSystemName, roleId, componentId, port) <> (DeploymentByRole.tupled, DeploymentByRole.unapply)

  def hostFk = foreignKey("host_fk", hostId, hosts)(_.hostId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)

  def rolesFk = foreignKey("role_fk", roleId, dpwRoles)(_.roleId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
}
