package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
/**
  * Created by jorge on 29/5/2017.
  */

case class MasterTypeHasFields(masterTypeHasFieldsId: Int, fieldId: Int, masterTypeId: Short, ordering: Short, fieldEnabled: Boolean) extends BaseModel

class MasterTypeHasFieldsTable(tag: Tag) extends Table[MasterTypeHasFields](tag, "master_type_has_fields"){

  val masterFields = TableQuery[MasterFieldTable]
  val masterTypes = TableQuery[MasterTypeTable]

  def masterTypeHasFieldsId: Rep[Int] = column[Int]("master_type_has_fields_id", O.PrimaryKey, O.Unique, O.AutoInc)
  def fieldId: Rep[Int] = column[Int]("field_id")
  def masterTypeId: Rep[Short] = column[Short]("master_type_id")
  def ordering: Rep[Short] = column[Short]("ordering")
  def fieldEnabled: Rep[Boolean] = column[Boolean]("field_enabled")

  def * = (masterTypeHasFieldsId, fieldId, masterTypeId, ordering, fieldEnabled) <> (MasterTypeHasFields.tupled, MasterTypeHasFields.unapply)

  def masterField = foreignKey("field_id_fk", fieldId, masterFields)(_.fieldId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
  def masterType = foreignKey("master_type_id_fk", masterTypeId, masterTypes)(_.masterTypeId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
}
