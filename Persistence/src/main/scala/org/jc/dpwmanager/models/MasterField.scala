package org.jc.dpwmanager.models

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
/**
  * Created by jorge on 29/5/2017.
  */
case class MasterField(fieldId: Int, fieldName: String, javaTypePattern: String, fieldDescription: String) extends BaseModel

class MasterFieldTable(tag: Tag) extends Table[MasterField](tag, "master_fields") {
  def fieldId: Rep[Int] = column[Int]("field_id", O.PrimaryKey)
  def fieldName: Rep[String] = column[String]("field_name")
  def javaTypePattern: Rep[String] = column[String]("java_type_pattern")
  def fieldDescription: Rep[String] = column[String]("field_description")

  def * = (fieldId, fieldName, javaTypePattern, fieldDescription) <> (MasterField.tupled, MasterField.unapply)
}
