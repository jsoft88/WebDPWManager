package org.jc.dpwmanager.interaction

class MasterWatcherExecutable(masterTypeId: Short, masterLabel: String) extends IExecutable(masterTypeId, masterLabel) {
  override protected var commandMap = Array.empty
  override protected var command = Array.empty

  override def commandToString() = ???

  override def commandMapToArray(map: Map[MasterFields, String]) = {
    val fieldOrder: Array[MasterFields] = Array(

    )
  }

  override def commandAsMap = super.commandAsMap
}
