package org.jc.dpwmanager.interaction

object MasterWatcherFields {

  case object RunAsUser extends MasterFields

  case object CmdPath extends MasterFields

  case object JvmParams extends MasterFields

  case object PathToExecutable extends MasterFields

  case object MasterWatcherId extends MasterFields

  case object ZooKeeperHost extends MasterFields

  case object ZooKeeperPort extends MasterFields

  case object TimeListenersZnode extends MasterFields

  case object MasterWatcherKeepAliveZnode extends MasterFields

  case object ProcessObservedZnode extends MasterFields

  case object IsChild extends MasterFields

  case object IsActiveChild extends MasterFields

  case object NumberOfChildren extends MasterFields

  case object IntervalToWaitForUpdate extends MasterFields

  case object ChildUpdateZnodes extends MasterFields

  case object childUpdateZnode extends MasterFields

  case object NtpServer extends MasterFields

  case object IdOfParentMasterWatcher extends MasterFields

  case object MaxForgiveMeMillis extends MasterFields

  case object HardKillScript extends MasterFields

  case object KillRequestZnode extends MasterFields
}
