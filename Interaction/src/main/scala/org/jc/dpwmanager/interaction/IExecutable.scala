package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 11/6/2017.
  */
abstract class IExecutable(masterTypeId: Short, masterLabel: String) {

  protected var commandMap: Map[MasterFields, String]

  protected var command: Array[String]

  def commandToString(): String

  final def commandAsArray(map: Map[MasterFields, String]): Array[String] = {
    this.commandMap = map
    commandMapToArray(map)
  }

  def commandMapToArray(map: Map[MasterFields, String]): Array[String]

  def commandAsMap: Map[MasterFields, String]
}
