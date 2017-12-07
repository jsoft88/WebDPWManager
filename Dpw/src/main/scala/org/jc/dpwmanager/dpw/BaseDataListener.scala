package org.jc.dpwmanager.dpw

trait BaseDataListener {

  /**
    * Callback invoked to notify that the client connected to ZooKeeper
    */
  def connected(): Unit

  /**
    * Callback invoked to notify that the client disconnected from ZooKeeper
    * @param rc int representing disconnection reason
    */
  def disconnected(rc: Int): Unit
}
