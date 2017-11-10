package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.{MasterField, MasterTypeHasFields}
import org.jc.dpwmanager.repository.MasterTypeHasFieldsRepository

import scala.concurrent.ExecutionContext

case class MasterFieldsRetrieveCommandAsc(repository: MasterTypeHasFieldsRepository, entity: MasterTypeHasFields)(implicit ec: ExecutionContext) extends Command[Int, MasterTypeHasFields, MasterFieldsRetrieveResponse](repository, entity){
  override def execute = {
    repository.getFieldsForMaster(entity.masterTypeId, true).map(s => MasterFieldsRetrieveResponse(s)) recover {
      case ex => throw new Exception(ex.getMessage)
    }
  }
}

case class MasterFieldsRetrieveCommandDesc(repository: MasterTypeHasFieldsRepository, entity: MasterTypeHasFields)(implicit ec: ExecutionContext) extends Command[Int, MasterTypeHasFields, MasterFieldsRetrieveResponse](repository, entity){
  override def execute = {
    repository.getFieldsForMaster(entity.masterTypeId, false).map(s => MasterFieldsRetrieveResponse(s)) recover {
      case ex => throw new Exception(ex.getMessage)
    }
  }
}

case class MasterFieldsRetrieveResponse(response: Seq[MasterField]) extends CommandResponseWrapper[Seq[MasterField]](response = response)