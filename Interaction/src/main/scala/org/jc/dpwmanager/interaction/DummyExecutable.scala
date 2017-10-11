package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 14/7/2017.
  */
case class DummyExecutable(masterTypeId: Short, masterLabel: String) extends IExecutable(masterTypeId, masterLabel) {

  override protected var command: Array[String] = Array("dummy")

  override def commandToString(): String = "dummy"

  override def commandAsArray(map: Map[MasterFields, String]): Array[String] = Array("dummy")
}