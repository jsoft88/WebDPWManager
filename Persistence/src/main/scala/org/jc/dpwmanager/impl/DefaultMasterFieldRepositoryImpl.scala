package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{MasterField, MasterFieldTable, MasterTypeHasFieldsTable}
import org.jc.dpwmanager.repository.MasterFieldRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class DefaultMasterFieldRepositoryImpl(implicit ec: ExecutionContext) extends MasterFieldRepository with Database {
  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val masterFieldTable = TableQuery[MasterFieldTable]

  val masterTypeHasFields = TableQuery[MasterTypeHasFieldsTable]

  db.run(DBIO.seq(masterFieldTable.schema.create)) onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("Failed to create schema for MasterField Table: " + ex.getMessage)
  }

  override def save(entity: MasterField) = {
    Future.successful(entity)
  }

  override def delete(entity: MasterField) = Future.successful(1)

  override def update(entity: MasterField) = Future.successful(1)

  override def get(id: Int) = db.run(masterFieldTable.filter(_.fieldId === id).result headOption)

  override def getFieldWithEnabledFlag(fieldId: Int): Future[Option[(MasterField, Boolean)]] = {
    db.run(
      (for {
        (mf, mhf) <- masterFieldTable filter(_.fieldId === fieldId) join masterTypeHasFields on (_.fieldId === _.fieldId)
      } yield (mf, mhf.fieldEnabled)).result headOption
    )
  }
}
