package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{MasterFieldTable, MasterTypeHasFields, MasterTypeHasFieldsTable}
import org.jc.dpwmanager.repository.MasterTypeHasFieldsRepository
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class DefaultMasterTypeHasFieldsRepositoryImpl(implicit ec: ExecutionContext) extends MasterTypeHasFieldsRepository with Database {

  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val masterTypeHasFieldsTable = TableQuery[MasterTypeHasFieldsTable]

  val masterFieldTable = TableQuery[MasterFieldTable]

  db.run(DBIO.seq(masterTypeHasFieldsTable.schema.create)) onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("MasterTypeHasFields schema create error. Error is: " + ex.getMessage)
  }

  override def getFieldsForMaster(masterTypeId: Short, asc: Boolean) = {
    db.run(
      (for {
        (mhf, mf) <- masterTypeHasFieldsTable filter (_.masterTypeId === masterTypeId) join masterFieldTable on (_.fieldId === _.fieldId) sortBy(t => if (asc) t._1.ordering.asc else t._1.ordering.desc)
      } yield (mf, mhf.fieldEnabled)).result
    )
  }

  override def updateOrderingOfFields(masterTypeFields: Seq[MasterTypeHasFields]) = Future.successful(true)

  override def save(entity: MasterTypeHasFields) = Future.successful(entity)

  override def delete(entity: MasterTypeHasFields) = Future.successful(1)

  override def update(entity: MasterTypeHasFields) = Future.successful(1)

  override def get(id: Int) = db.run(masterTypeHasFieldsTable.filter(_.fieldId === id).result headOption)
}
