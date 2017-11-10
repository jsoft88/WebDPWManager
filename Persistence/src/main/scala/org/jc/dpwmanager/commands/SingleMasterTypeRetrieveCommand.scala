package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.MasterType
import org.jc.dpwmanager.repository.MasterTypeRepository

import scala.concurrent.ExecutionContext

case class SingleMasterTypeRetrieveCommand(repository: MasterTypeRepository, entity: MasterType)(implicit ec: ExecutionContext) extends Command[Short, MasterType, SingleMasterTypeRetrieveResponse](repository, entity){
  override def execute = {
    val notFound = MasterType(label = "Not found", masterTypeId = 0)

    repository.get(entity.masterTypeId).map(r => SingleMasterTypeRetrieveResponse(r match { case Some(t) => t case None => notFound})).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: SingleMasterTypeRetrieveCommand"
}

case class SingleMasterTypeRetrieveResponse(response: MasterType) extends CommandResponseWrapper[MasterType](response = response)