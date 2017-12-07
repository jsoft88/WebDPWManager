package org.jc.dpwmanager.dpw

import org.apache.zookeeper.AsyncCallback.StringCallback
import org.apache.zookeeper.Watcher.Event.{EventType, KeeperState}
import org.apache.zookeeper._

import scala.concurrent.{ExecutionContext, Future}

trait TimeDataListener extends BaseDataListener {

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  def timeZnodeCreated(creator: String): Unit

  /**
    * Callback invoked when time masters' keep alive znode is removed
    */
  def timeZnodeRemoved: Unit

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  def timeZnodeChanged: Unit

  /**
    * Callback invoked when content from time masters' keep alive znode is retrieved
    * @return byte array representing the znode's content
    */
  def timeZnodeRead: Seq[Byte]

  /**
    * Callback invoked when time listeners' znode is created
    */
  def timeListenersZnodeCreated: Unit

  /**
    * Callback invoked when time listeners' znode is removed
    */
  def timeListenersZnodeRemoved: Unit

  /**
    * Callback invoked when time listeners' znode changes
    */
  def timeListenersZnodeChanged: Unit

  /**
    * Callback invoked when content from time listeners' znode is retrieved
    * @return byte array representing znode's content
    */
  def timeListenersZnodeRead: Seq[Byte]
}

sealed trait TimeConstants

class TimeDataMonitor(zk: ZooKeeper, timeZnode: String, timeListenersZnode: String, timeMasterId: String, listener: TimeDataListener)(implicit ec: ExecutionContext) extends Watcher with StringCallback {

  case object REQUESTER_ID extends TimeConstants



  override def process(event: WatchedEvent) = {
    event.getType match {
      case EventType.None => {
        if (event.getState == KeeperState.SyncConnected) {
          this.listener.connected()
        } else if (event.getState == KeeperState.Disconnected || event.getState == KeeperState.Expired) {
          this.listener.disconnected(KeeperException.Code.CONNECTIONLOSS.intValue())
        }
      }
    }
  }

  def createTimeZnode: Unit = {
    this.zk.create(this.timeZnode, "0".getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, Map(REQUESTER_ID -> this.timeMasterId))
  }

  /**
    * Callback for znode creation
    * @param rc creation result code
    * @param path path of created znode
    * @param ctx context object containing metadata
    * @param name
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any, name: String) = {
    KeeperException.Code.get(rc) match {
      case KeeperException.Code.OK => {
        if (path.equals(this.timeZnode)) {
          Future {
            this.listener.timeZnodeCreated(ctx.asInstanceOf[Map[TimeConstants, String]].get(REQUESTER_ID) match { case Some(c) => c case None => "" })
          }
        }
      }
      case KeeperException.Code.AUTHFAILED | KeeperException.Code.OPERATIONTIMEOUT =>
      case KeeperException.Code.NODEEXISTS => if (path.equals(this.timeZnode)) this.listener.timeZnodeCreated("-9999999999")
      case KeeperException.Code.SESSIONEXPIRED | KeeperException.Code.CONNECTIONLOSS => this.listener.disconnected(rc)
    }
  }
}
