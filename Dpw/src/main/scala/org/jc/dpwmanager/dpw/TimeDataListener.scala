package org.jc.dpwmanager.dpw

import org.apache.zookeeper.AsyncCallback.{StatCallback, StringCallback, VoidCallback}
import org.apache.zookeeper.Watcher.Event.{EventType, KeeperState}
import org.apache.zookeeper._
import org.apache.zookeeper.data.Stat

import scala.concurrent.{ExecutionContext, Future}

trait TimeDataListener extends BaseDataListener {

  /**
    * Callback invoked when time masters' keep alive znode changes
    * @param creator master id of creator
    * @param isWatchTrigger boolean indicating if the method is invoked because a trigger
    */
  def timeZnodeCreated(creator: String, isWatchTrigger: Boolean): Unit

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

  /**
    * Callback invoked when a verification for existence of znode is completed, and to signal that it hasn't been created
    * yet.
    */
  def timeZnodeInexistent: Unit
}

sealed trait TimeConstants

class TimeDataMonitor(zk: ZooKeeper, timeZnode: String, timeListenersZnode: String, timeMasterId: String, listener: TimeDataListener)(implicit ec: ExecutionContext) extends Watcher with StringCallback with StatCallback with AsyncCallback.DataCallback with VoidCallback {

  case object REQUESTER_ID extends TimeConstants

  case object CREATE_ZNODE_PAYLOAD extends TimeConstants

  override def process(event: WatchedEvent) = {
    event.getType match {
      case EventType.None => {
        if (event.getState == KeeperState.SyncConnected) {
          this.listener.connected()
        } else if (event.getState == KeeperState.Disconnected || event.getState == KeeperState.Expired) {
          this.listener.disconnected(KeeperException.Code.CONNECTIONLOSS.intValue())
        }
      }

      case EventType.NodeCreated => {

        event.getPath match {
          case this.timeZnode => this.listener.timeZnodeCreated("", true)
          case this.timeListenersZnode => this.listener.timeListenersZnodeCreated
        }
      }

      case EventType.NodeDataChanged => {
        event.getPath match {
          case this.timeZnode => this.listener.timeZnodeChanged
          case this.timeListenersZnode => this.listener.timeListenersZnodeChanged
        }
      }
    }
  }

  def readTimeZnode: Unit = {
    this.zk.getData(this.timeZnode, this, this, Map.empty[String, String])
  }

  def removeTimeZnode: Unit = {
    this.zk.delete(this.timeZnode, -1, this, null)
  }

  def createTimeZnode(withData: String): Unit = {
    var ctx = Map.empty[TimeConstants, String]
    ctx += (REQUESTER_ID -> this.timeMasterId)
    ctx += (CREATE_ZNODE_PAYLOAD -> withData)

    this.zk.create(this.timeZnode, withData.getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, ctx)
  }

  def timeZnodeExists: Unit = {
    this.zk.exists(this.timeZnode, this, this, Map.empty[String, String])
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
            this.listener.timeZnodeCreated(ctx.asInstanceOf[Map[TimeConstants, String]].get(REQUESTER_ID) match { case Some(c) => c case None => "" }, false)
          }
        }
      }
      case KeeperException.Code.AUTHFAILED | KeeperException.Code.OPERATIONTIMEOUT => this.zk.create(path, ctx.asInstanceOf[Map[TimeConstants, String]].get(CREATE_ZNODE_PAYLOAD).get.getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, ctx)
      case KeeperException.Code.NODEEXISTS => if (path.equals(this.timeZnode)) this.listener.timeZnodeCreated("-9999999999", false)
      case KeeperException.Code.SESSIONEXPIRED | KeeperException.Code.CONNECTIONLOSS => this.listener.disconnected(rc)
    }
  }

  /**
    * Callback for checking existence of znode
    * @param rc verification result code
    * @param path path of znode for which we are verifying existence
    * @param ctx some context as map[String, String]
    * @param stat zookeeper stuff
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any, stat: Stat) = {
    KeeperException.Code.get(rc) match {
      case KeeperException.Code.OK => {
        path match {
          case this.timeZnode => this.listener.timeZnodeCreated("-9999999999999999", false)
          case this.timeListenersZnode => this.listener.timeListenersZnodeCreated
        }
      }

      case KeeperException.Code.AUTHFAILED | KeeperException.Code.OPERATIONTIMEOUT => this.zk.exists(path, this, this, Map.empty[String, String])
      case KeeperException.Code.NONODE => this.listener.timeZnodeInexistent
      case KeeperException.Code.SESSIONEXPIRED | KeeperException.Code.CONNECTIONLOSS => this.listener.disconnected(rc)
    }
  }

  /**
    * Callback invoked when reading data from znode
    * @param rc data read result code
    * @param path path of znode from which data is being read
    * @param ctx some context object
    * @param data data read from znode as byte array
    * @param stat zookeeper stuff
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any, data: Array[Byte], stat: Stat) = {

  }

  /**
    * Callback invoked when znode is removed
    * @param rc removal result code
    * @param path path of removed znode
    * @param ctx some context
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any) = {

  }
}
