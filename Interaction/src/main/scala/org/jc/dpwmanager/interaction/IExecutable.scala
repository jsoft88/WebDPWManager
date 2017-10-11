package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 11/6/2017.
  */
abstract class IExecutable(masterTypeId: Short, masterLabel: String) {

  protected var command: Array[String]

  def commandToString(): String

  def commandAsArray(map: Map[MasterFields, String]): Array[String]
}
