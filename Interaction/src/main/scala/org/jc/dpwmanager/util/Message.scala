package org.jc.dpwmanager.util

import akka.actor.Address
import org.jc.dpwmanager.interaction.IExecutable

/**
  * Created by jorge on 19/6/2017.
  */
final case class StartRoleInHost(deployWrapper: DeployRoleWrapper)

final case class StartRoleSuccess(deployRoleWrapper: DeployRoleWrapper)

final case class StartRoleFailed(reason: String, role: String)

final case class DeployMasterStart(msgWrapper: MessageWrapper)

final case class DeployMasterCompleted(msgWrapper: MessageWrapper)

final case class DeployMasterFailed(reason: String)

final case class DeployRoleWrapper(actorName: String, actorSystemName: String, address: String, port: Short, role: Role)

final case class MessageWrapper(deployId: Int, actorName: Option[String], masterTypeId: Short, execArgs: IExecutable, address: String, port: Short)

final case class StopMaster(msgWrapper: MessageWrapper)

final case class MasterStopped(msgWrapper: MessageWrapper)

final case class QueryPersistenceManager(command: PersistenceCommand)

final case class PersistenceActorsInformation(pathsForResolution: Map[String, Address])

final case class ServerActorsInformation(pathForResolution: Map[String, Address])

final object QueryOnePersistenceRoleAtLeast

case object AgentRegistration

case class MessagePayload[A](payload: A)

trait PersistenceCommand

case object ServerComponent {
  override def toString: String = this.getClass.getSimpleName
}

case object AgentComponent {
  override def toString: String = this.getClass.getSimpleName
}

