package org.jc.dpwmanager.util

import org.jc.dpwmanager.interaction.IExecutable

/**
  * Created by jorge on 19/6/2017.
  */
final case class DeployMasterStart(msgWrapper: MessageWrapper)

final case class DeployMasterCompleted(msgWrapper: MessageWrapper)

final case class DeployMasterFailed(reason: String)

final case class MessageWrapper(actorName: Option[String], masterTypeId: Short, execArgs: IExecutable)

final case class StopMaster(msgWrapper: MessageWrapper)

final case class MasterStopped(msgWrapper: MessageWrapper)

final case class QueryPersistenceManager(command: PersistenceCommand)

final case class PersistenceActorsInformation(pathsForResolution: Array[String])

final case class ServerActorsInformation(pathForResolution: String)

case object AgentRegistration

case class MessagePayload[A](payload: A)

trait PersistenceCommand

case object ServerComponent {
  override def toString: String = this.getClass.getSimpleName
}

case object AgentComponent {
  override def toString: String = this.getClass.getSimpleName
}

