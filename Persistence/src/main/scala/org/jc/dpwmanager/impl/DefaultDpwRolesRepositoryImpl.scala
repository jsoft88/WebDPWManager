package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{DpwRoles, DpwRolesTable}
import org.jc.dpwmanager.repository.DpwRolesRepository
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}

class DefaultDpwRolesRepositoryImpl(implicit ec: ExecutionContext) extends DpwRolesRepository with Database {

  val db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val dpwRolesTable = TableQuery[DpwRolesTable]

  db.run(DBIO.seq(dpwRolesTable.schema.create)).onComplete({
    case Success(_) =>
    case Failure(ex) => throw new Exception("Schema creation for DpwRoles failed. Error is: " + ex.getMessage, ex)
  })

  override def getRoles() = db.run(dpwRolesTable.result)

  override def save(entity: DpwRoles) = Future.successful(entity)

  override def delete(entity: DpwRoles) = Future.successful(1)

  override def update(entity: DpwRoles) = Future.successful(1)

  override def get(id: String) = db.run(dpwRolesTable.filter(_.roleId === id).result headOption)
}
