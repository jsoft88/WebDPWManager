package org.jc.dpwmanager.dpw

import java.net.InetAddress

import akka.actor.{Actor, Cancellable, PoisonPill, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp, UnreachableMember}
import org.apache.commons.net.ntp.{NTPUDPClient, TimeInfo}
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.jc.dpwmanager.interaction.{TimeMasterExecutable, TimeMasterFields}
import org.jc.dpwmanager.util.{ExecuteMaster, TimeMasterTimeTick}

import scala.concurrent.duration._

class TimeMaster extends Actor with Watcher with TimeDataListener {

  protected var zk: Option[ZooKeeper] = None

  protected var tdm: Option[TimeDataMonitor] = None

  protected var schedulerCancellable: Option[Cancellable] = None

  protected var executable: Option[TimeMasterExecutable] = None

  protected val cluster = Cluster(context.system)

  override def receive = {
    case ExecuteMaster(executable: TimeMasterExecutable) => {
      zk match {
        case None => {
          val port = executable.commandAsMap.get(TimeMasterFields.ZooKeeperPort) match {
            case Some(_) => _
            case None => self ! PoisonPill
          }

          val host = executable.commandAsMap.get(TimeMasterFields.ZooKeeperHost) match {
            case Some(_) => _
          }

          val timeZnode = executable.commandAsMap.get(TimeMasterFields.TimeMasterZnode) match {
            case Some(tz: String) => tz
          }

          val timeListenersZnode = executable.commandAsMap.get(TimeMasterFields.TimeListenersZnode) match {
            case Some(tlz: String) => tlz
          }

          val timeMasterId = executable.commandAsMap.get(TimeMasterFields.TimeMasterId) match {
            case Some(id: String) => id
          }

          this.zk = Some(new ZooKeeper(s"""$host:$port""", 10000, this))
          this.tdm = Some(new TimeDataMonitor(zk = this.zk.head, timeZnode = timeZnode, timeListenersZnode = timeListenersZnode, listener = this, timeMasterId = timeMasterId))

          val intervalMillis = executable.commandAsMap.get(TimeMasterFields.TimeTickInterval) match {
            case Some(interval) => interval.toLong
          }

          this.executable = Some(executable)

          this.schedulerCancellable = Some(context.system.scheduler.schedule(0 milliseconds, intervalMillis milliseconds, self, TimeMasterTimeTick))
        }
      }
    }
    case TimeMasterTimeTick if this.executable.headOption != None => {
      //retrieve time
      val client = new NTPUDPClient()
      val hostAddr = InetAddress.getByName(this.executable.head.commandAsMap.get(TimeMasterFields.NTPServer) match {
        case Some(servers: String) => servers.split(",").head
        case None => "localhost"
      })
      val timeInfo: TimeInfo = client.getTime(hostAddr)
      timeInfo.computeDetails()
      val currentNetworkTime: Long = timeInfo.getReturnTime + timeInfo.getOffset


    }

    case MemberUp(member) if (member.address != self.path.address) => {

        context.actorSelection(RootActorPath(member.address)/"user"/member.address)
    }
  }

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])

  }

  override def process(event: WatchedEvent) = {
    this.tdm match {
      case Some(_) => this.tdm.head.process(event)
    }
  }

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  override def timeZnodeCreated(creatorId: String): Unit = ???

  /**
    * Callback invoked when time masters' keep alive znode is removed
    */
  override def timeZnodeRemoved: Unit = ???

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  override def timeZnodeChanged: Unit = ???

  /**
    * Callback invoked when content from time masters' keep alive znode is retrieved
    *
    * @return byte array representing the znode's content
    */
  override def timeZnodeRead: Seq[Byte] = ???

  /**
    * Callback invoked when time listeners' znode is created
    */
override def timeListenersZnodeCreated: Unit = ???

  /**
    * Callback invoked when time listeners' znode is removed
    */
override def timeListenersZnodeRemoved: Unit = ???

  /**
    * Callback invoked when time listeners' znode changes
    */
override def timeListenersZnodeChanged: Unit = ???

  /**
    * Callback invoked when content from time listeners' znode is retrieved
    *
    * @return byte array representing znode's content
    */
override def timeListenersZnodeRead: Seq[Byte] = ???

  /**
    * Callback invoked to notify that the client connected to ZooKeeper
    */
override def connected(): Unit = ???

  /**
    * Callback invoked to notify that the client disconnected from ZooKeeper
    *
    * @param rc int representing disconnection reason
    */
  override def disconnected(rc: Int): Unit = ???
}
