package org.jc.dpwmanager.models

import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._
/**
  * Created by jorge on 26/5/2017.
  */

case class Agent(port: Int, agentId: Short, host: String, actorName: String, actorSystemName: String, roleId: String) extends BaseModel

class AgentTable(tag: Tag) extends Table[Agent](tag, "agent") {
  val dpwRolesTable = TableQuery[DpwRolesTable]

  def port: Rep[Int] = column[Int]("port")
  def agentId: Rep[Short] = column[Short]("agent_id", O.PrimaryKey, O.AutoInc)
  def host: Rep[String] = column[String]("host")
  def actorName: Rep[String] = column[String]("actor_name")
  def actorSystemName: Rep[String] = column[String]("actor_system_name")
  def roleId: Rep[String] = column[String]("role_id")

  def * = (port, agentId, host, actorName, actorSystemName, roleId) <> (Agent.tupled, Agent.unapply)

  def dpwRolesFk = foreignKey("fk_role", roleId, dpwRolesTable)(_.roleId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
}
