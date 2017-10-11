package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.{Agent}

import scala.concurrent.Future

/**
  * Created by jorge on 31/5/2017.
  */
trait AgentRepository extends BaseRepository[Short, Agent] {
  def getAllAgents: Future[Seq[Agent]]
  def getAllAgentsInHost(host: String): Future[Seq[Agent]]
}
