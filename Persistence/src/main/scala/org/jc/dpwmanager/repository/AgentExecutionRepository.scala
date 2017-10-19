package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.{AgentExecution, AgentExecutionDetails}

import scala.concurrent.Future

/**
  * Created by jorge on 11/7/2017.
  */
trait AgentExecutionRepository extends BaseRepository[Int, AgentExecution]{

  @Deprecated
  def getUnstoppedMasterForAgent(agentId: Short): Future[Seq[AgentExecution]]

  @Deprecated
  def getAllMastersForAgent(agentId: Short): Future[Seq[AgentExecution]]

  def getAllMastersOnDeployment(deployId: Int): Future[Seq[AgentExecution]]

  def getUnstoppedMasterForAgent(deployId: Int): Future[Seq[AgentExecution]]
}
