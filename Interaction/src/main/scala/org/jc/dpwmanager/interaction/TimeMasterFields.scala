package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 13/6/2017.
  */

object TimeMasterFields {

  case object RunAsUser extends MasterFields

  case object CmdPath extends MasterFields

  case object JVMParams extends MasterFields

  case object PathToExecutable extends MasterFields

  case object TimeMasterId extends MasterFields

  case object ZooKeeperHost extends MasterFields

  case object ZooKeeperPort extends MasterFields

  case object TimeMasterZnode extends MasterFields

  case object TimeListenersZnode extends MasterFields

  case object TimeTickInterval extends MasterFields

  case object NTPServer extends MasterFields

  case object KillRequestZnode extends MasterFields

  case object MaxForgiveMeMillis extends MasterFields
}

