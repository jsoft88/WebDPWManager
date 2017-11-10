package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.DeploymentByRole
import org.jc.dpwmanager.repository.DeploymentByRoleRepository

import scala.concurrent.ExecutionContext

case class ActorSystemsRetrieveCommand(repository: DeploymentByRoleRepository, entity: DeploymentByRole)(implicit ec: ExecutionContext) extends Command[Int, DeploymentByRole, ActorSystemsRetrieveResponse](repository, entity){
  override def execute = {
    repository.getActorSystems().map(ActorSystemsRetrieveResponse(_)) recover {
      case ex => throw new Exception(this.toString + ". Failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: ActorSystemsRetrieve."
}

case class ActorSystemsRetrieveResponse(response: Seq[String]) extends CommandResponseWrapper[Seq[String]](response)
