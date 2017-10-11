package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db
import org.jc.dpwmanager.models.{MasterType, MasterTypeTable}
import org.jc.dpwmanager.repository.MasterTypeRepository
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by jorge on 7/6/2017.
  */
@throws(classOf[Exception])
class DefaultMasterTypeImpl(implicit ec: ExecutionContext) extends MasterTypeRepository with db.Database{

  val db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val masterTypes = TableQuery[MasterTypeTable]
  db.run(masterTypes.schema.create).onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("Error while initializing repository", ex)
  }

  override def getAllMasterTypes: Future[Seq[MasterType]] = {
    db.run(masterTypes.result)
  }

  override def save(entity: MasterType): Future[MasterType] = {
    db.run(masterTypes returning masterTypes.map(_.masterTypeId) into ((entity, masterTypeId) => entity.copy(masterTypeId = masterTypeId)) += entity)
  }

  override def delete(entity: MasterType): Future[Int] = {
    db.run(masterTypes.filter(_.masterTypeId === entity.masterTypeId).delete)
  }

  override def update(entity: MasterType): Future[Int] = {
    val q = for { mt <- masterTypes if mt.masterTypeId === entity.masterTypeId } yield (entity.label)
    db.run(q.update((entity.label)))
  }

  override def get(id: Short): Future[Option[MasterType]] = {
    db.run(masterTypes.filter(_.masterTypeId === id).result.headOption)
  }
}
