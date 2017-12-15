package org.jc.dpwmanager.dpw

import org.apache.zookeeper.AsyncCallback.{StatCallback, StringCallback}
import org.apache.zookeeper.Watcher.Event.{EventType, KeeperState}
import org.apache.zookeeper._
import org.apache.zookeeper.data.Stat

import scala.concurrent.{ExecutionContext, Future}

trait WatcherDataListener extends BaseDataListener {

  /**
    * Callback invoked when watchers' keep alive znode is created or when notifying that it already exist
    * @param creatorId master identifier of the instance of created it.
    */
  def watcherZnodeCreated(creatorId: String): Unit

  def watcherZnodeInexistent: Unit
}

sealed trait WatcherConstants

class WatcherDataMonitor(zk: ZooKeeper, watcherZnode: String, masterId: String, listener: WatcherDataListener)(implicit ec: ExecutionContext) extends Watcher with StringCallback with StatCallback {

  object REQUESTER_ID extends WatcherConstants

  object CREATE_ZNODE_PAYLOAD extends WatcherConstants

  override def process(event: WatchedEvent) = {
    event.getType match {
      case EventType.None => {
        event.getState match {
          case KeeperState.SyncConnected => this.listener.connected()
          case KeeperState.Expired | KeeperState.Disconnected => this.listener.disconnected(KeeperException.Code.CONNECTIONLOSS.intValue())
        }
      }
    }
  }

  def createWatcherKeepAliveZnode(withData: String): Unit = {
    var ctx = Map.empty[WatcherConstants, String]
    ctx += (REQUESTER_ID -> this.masterId)
    ctx += (CREATE_ZNODE_PAYLOAD -> withData)

    this.zk.create(this.watcherZnode, withData.getBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, ctx)
  }

  def watcherKeepAliveZnodeExists(): Unit = {
    this.zk.exists(this.watcherZnode, this, this, Map.empty[WatcherConstants, String])
  }

  /**
    * Callback invoked for processing creation result
    * @param rc operation result code
    * @param path path of the znode being created
    * @param ctx some context as Map[WatcherConstans, String]
    * @param name zookeeper callback stuff
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any, name: String) = {
    KeeperException.Code.get(rc) match {
      case KeeperException.Code.OK => {
        path match {
          case this.watcherZnode => Future { this.listener.watcherZnodeCreated(ctx.asInstanceOf[Map[WatcherConstants, String]].get(REQUESTER_ID) match {
            case Some(id) => id
            case None => "-999999999"
          }) }
        }
      }
      case KeeperException.Code.AUTHFAILED | KeeperException.Code.OPERATIONTIMEOUT =>
      case KeeperException.Code.CONNECTIONLOSS | KeeperException.Code.SESSIONEXPIRED => this.listener.disconnected(rc)
      case KeeperException.Code.NODEEXISTS => this.listener.watcherZnodeCreated("-9999999999")
    }
  }

  /**
    * Callback invoked for processing result from a exists call
    * @param rc operation result code
    * @param path path of the znode we're checking for existence
    * @param ctx some context as Map[WatchConstants, String]
    * @param stat zookeeper stuff
    */
  override def processResult(rc: Int, path: String, ctx: scala.Any, stat: Stat) = {
    KeeperException.Code.get(rc) match {
      case KeeperException.Code.OK => this.listener.watcherZnodeCreated("-99999999")
      case KeeperException.Code.NONODE => this.listener.watcherZnodeInexistent
      case KeeperException.Code.CONNECTIONLOSS | KeeperException.Code.SESSIONEXPIRED => this.listener.disconnected(rc)
      case KeeperException.Code.AUTHFAILED | KeeperException.Code.OPERATIONTIMEOUT => this.watcherKeepAliveZnodeExists()
    }
  }
}
