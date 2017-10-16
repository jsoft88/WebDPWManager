package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetailsTable, AgentExecutionTable}
import org.jc.dpwmanager.repository.AgentExecutionRepository
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}

/**
  * Created by jorge on 11/7/2017.
  */
class DefaultAgentExecutionRepositoryImpl(implicit ec: ExecutionContext) extends AgentExecutionRepository with Database{

  val db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  val agentExecutionTable = TableQuery[AgentExecutionTable]

  val agentExecutionDetailsTable = TableQuery[AgentExecutionDetailsTable]

  db.run(DBIO.seq(agentExecutionTable.schema.create)).onComplete({
    case Success(_) =>
    case Failure(ex) => throw new Exception("Schema creation failed", ex)
  })

  override def getUnstoppedMasterForAgent(agentId: Short): Future[Seq[AgentExecution]] = {
    val q = agentExecutionTable.filter((aet => !aet.cleanStop && aet.agentId === agentId))
    db.run(q.result)
  }

  override def save(entity: AgentExecution): Future[AgentExecution] = db.run(agentExecutionTable returning agentExecutionTable.map(_.agentExecId) into ((entity, agentExecId) => entity.copy(agentExecId = agentExecId)) += entity)

  override def delete(entity: AgentExecution): Future[Int] = {
    Future.successful(0)
  }

  override def update(entity: AgentExecution): Future[Int] = {
    val q = for { ae <- agentExecutionTable if ae.agentExecId === entity.agentExecId } yield (ae.cleanStop)
    db.run(q.update(entity.cleanStop))
  }

  override def get(id: Int): Future[Option[AgentExecution]] = db.run(agentExecutionTable.filter(_.agentExecId === id).result headOption)

  override def getAllMastersForAgent(agentId: Short): Future[Seq[AgentExecution]] = db.run(agentExecutionTable.filter(_.agentId === agentId).result)

  override def getAllMastersOnDeployment(deployId: Int) = db.run()
}
