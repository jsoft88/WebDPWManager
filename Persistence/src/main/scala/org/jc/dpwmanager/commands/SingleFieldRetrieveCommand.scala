package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.MasterField
import org.jc.dpwmanager.repository.MasterFieldRepository

import scala.concurrent.ExecutionContext

case class SingleFieldRetrieveCommand(repository: MasterFieldRepository, entity: MasterField)(implicit ec: ExecutionContext) extends Command[Int, MasterField, SingleFieldRetrieveResponse](repository, entity){
  override def execute = {
    val dummyField = MasterField(fieldId = 0, fieldName = "Not Found", javaTypePattern = "", fieldDescription = "Master field not found")
    repository.getFieldWithEnabledFlag(entity.fieldId).map(r => SingleFieldRetrieveResponse(r match { case Some(f) => (f._1, f._2) case None => (dummyField, false)})).recover {
      case ex => throw new Exception(this.toString +  ". It failed with: " + ex.getMessage)
    }
  }

  override def toString: String = "Command is: SingleFieldRetrieveCommand"
}

case class SingleFieldRetrieveResponse(response: (MasterField, Boolean)) extends CommandResponseWrapper[(MasterField, Boolean)](response)