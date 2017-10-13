package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{Host, HostTable}
import org.jc.dpwmanager.repository.HostRepository
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DefaultHostRepositoryImpl(implicit ec: ExecutionContext) extends HostRepository with Database {

  val hosts = TableQuery[HostTable]

  db.run(DBIO.seq(hosts.schema.create)) onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("Schema creation for Host table failed: " + ex.getMessage)
  }

  override def getHosts() = db.run(hosts.result)

  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  override def save(entity: Host) = db.run(hosts returning hosts.map(_.hostId) into ((entity, hostId) => entity.copy(hostId = hostId)) += entity)

  override def delete(entity: Host) = db.run(hosts.filter(_.hostId === entity.hostId).delete)

  override def update(entity: Host) = {
    val q = for { h <- hosts if h.hostId === entity.hostId } yield (h.hostId)
    db.run(q.update((entity.hostId)))
  }

  override def get(id: Short) = db.run(hosts.filter(_.hostId === id).result headOption)
}
