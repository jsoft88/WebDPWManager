package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._

case class Host(hostId: Short, address: String) extends BaseModel

class HostTable(tag: Tag) extends Table[Host](tag, "host") {

  def hostId: Rep[Short] = column[Short]("host_id", O.Unique, O.AutoInc, O.PrimaryKey)

  def address: Rep[String] = column[String]("address")

  def * = (hostId, address) <> (Host.tupled, Host.unapply)
}
