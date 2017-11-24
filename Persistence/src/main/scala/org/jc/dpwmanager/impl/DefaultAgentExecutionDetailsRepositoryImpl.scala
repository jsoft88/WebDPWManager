package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{AgentExecutionDetails, AgentExecutionDetailsTable}
import org.jc.dpwmanager.repository.AgentExecutionDetailsRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class DefaultAgentExecutionDetailsRepositoryImpl(implicit ec: ExecutionContext) extends AgentExecutionDetailsRepository with Database {
  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val agentExecutionDetailsTable = slick.lifted.TableQuery[AgentExecutionDetailsTable]

  db.run(DBIO.seq(agentExecutionDetailsTable.schema.create)).onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("AgentExecutionDetails schema create error. Error is: " + ex.getMessage)
  }

  override def save(entity: AgentExecutionDetails) = db.run(agentExecutionDetailsTable returning agentExecutionDetailsTable.map(_.executionDetailId) into ((entity, executionDetailId) => entity.copy(executionDetailId = executionDetailId)) += entity)

  override def delete(entity: AgentExecutionDetails) = db.run(agentExecutionDetailsTable.filter(_.executionDetailId === entity.executionDetailId).delete)

  override def update(entity: AgentExecutionDetails) = Future.successful(1)

  override def get(id: Int) = db.run(agentExecutionDetailsTable.filter(_.executionDetailId === id).result headOption)

  override def getExecutionDetails(agentExecId: Int) = db.run(agentExecutionDetailsTable.filter(_.agentExecId === agentExecId).result)
}
