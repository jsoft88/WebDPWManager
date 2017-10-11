package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._

/**
  * Created by jorge on 29/5/2017.
  */

case class MasterType(label: String, masterTypeId: Short) extends BaseModel

class MasterTypeTable(tag: Tag) extends Table[MasterType](tag, "master_types") {
  def label: Rep[String] = column[String]("label")
  def masterTypeId: Rep[Short] = column[Short]("master_type_id", O.PrimaryKey, O.AutoInc)

  def * = (label, masterTypeId) <> (MasterType.tupled, MasterType.unapply)
}
