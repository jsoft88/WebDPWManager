package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.MasterType
import org.jc.dpwmanager.repository.MasterTypeRepository

import scala.concurrent.ExecutionContext

case class MasterTypesListRetrieveCommand(repository: MasterTypeRepository, entity: MasterType)(implicit ec: ExecutionContext) extends Command[Short, MasterType, MasterTypesListRetrieveResponse](repository, entity){
  override def execute = {
    repository.getAllMasterTypes.map(MasterTypesListRetrieveResponse(_)).recover {
      case ex => throw new Exception(this.toString + ". It failed with " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: MasterTypeListRetrieveCommand"
}

case class MasterTypesListRetrieveResponse(response: Seq[MasterType]) extends CommandResponseWrapper[Seq[MasterType]](response)