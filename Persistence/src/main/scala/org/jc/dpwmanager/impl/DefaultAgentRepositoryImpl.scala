package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db
import org.jc.dpwmanager.models.{Agent, AgentTable}
import org.jc.dpwmanager.repository.AgentRepository
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
* Created by jorge on 5/6/2017.
*/
class DefaultAgentRepositoryImpl(implicit ec: ExecutionContext) extends AgentRepository with db.Database{

  val db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val agents = TableQuery[AgentTable]
  db.run(DBIO.seq(agents.schema.create)).onComplete {
    case Failure(ex) => throw new Exception("Schema creation failed", ex)
    case Success(_) =>
  }

  override def getAllAgents: Future[Seq[Agent]] = {
    db.run(agents.result)
  }

  override def getAllAgentsInHost(host: String): Future[Seq[Agent]] = {
    db.run(agents.filter(_.host === host).result)
  }

  override def save(entity: Agent): Future[Agent] = {
    db.run(agents returning agents.map(_.agentId) into ((entity, agentId) => entity.copy(agentId = agentId)) += entity)
  }

  override def delete(entity: Agent): Future[Int] = {
    db.run(agents.filter(_.agentId === entity.agentId).delete)
  }

  override def update(entity: Agent): Future[Int] = {
    val q = for { a <- agents if a.agentId === entity.agentId } yield (a.port, a.host)
    db.run(q.update((entity.port, entity.host)))
  }

  override def get(id: Short): Future[Option[Agent]] = {
    db.run(agents.filter(_.agentId === id).result.headOption)
  }
}
