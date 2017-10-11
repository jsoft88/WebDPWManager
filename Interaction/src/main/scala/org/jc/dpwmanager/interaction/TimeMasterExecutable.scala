package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 11/6/2017.
  */
case class TimeMasterExecutable(masterTypeId: Short, masterLabel: String) extends IExecutable(masterTypeId, masterLabel) {

  override protected var command: Array[String] = _

  override def commandToString(): String = {
    if (this.command.length == 0) {
      ""
    } else {
      this.command.mkString(",")
    }
  }

  override def commandAsArray(map: Map[MasterFields, String]): Array[String] = {
    val fieldOrder: Array[MasterFields] =
      Array(
        TimeMasterFields.RunAsUser,
        TimeMasterFields.CmdPath,
        TimeMasterFields.JVMParams,
        TimeMasterFields.PathToExecutable,
        TimeMasterFields.TimeMasterId,
        TimeMasterFields.ZooKeeperHost,
        TimeMasterFields.ZooKeeperPort,
        TimeMasterFields.TimeMasterZnode,
        TimeMasterFields.TimeListenersZnode,
        TimeMasterFields.TimeTickInterval,
        TimeMasterFields.NTPServer,
        TimeMasterFields.KillRequestZnode)

    val cmd = new scala.collection.mutable.ArrayBuffer[String]()

    fieldOrder.foreach(ef => map get ef match {
      case Some(value) => value split " " foreach(cmd += _)
      case None => throw new Exception("Key: " + ef + " has no associated value.")
    })

    this.command = cmd.toArray
    this.command
  }
}
