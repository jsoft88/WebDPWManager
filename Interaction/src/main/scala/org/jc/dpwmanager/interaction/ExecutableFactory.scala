package org.jc.dpwmanager.interaction

/**
  * Created by jorge on 13/6/2017.
  */
object ExecutableFactory {

  final val TIME_MASTER: String = "tm"
  final val MASTER_WATCHER: String = "mw"
  final val CHILD_MASTER_WATCHER: String = "cmw"
  final val DUMMY_EXECUTABLE: String = "dummy"

  @throws(classOf[Exception])
  def getExecutable(masterTypeId: Short, masterTypeLabel: String): Option[IExecutable] = {
    masterTypeLabel match {
      case TIME_MASTER => Some(new TimeMasterExecutable(masterTypeId = masterTypeId, masterLabel = masterTypeLabel))
      case MASTER_WATCHER => None
      case CHILD_MASTER_WATCHER => None
      case DUMMY_EXECUTABLE => Some(DummyExecutable(0, ""))
      case _ => throw new Exception("Unable to create master type.")
    }
  }
}
