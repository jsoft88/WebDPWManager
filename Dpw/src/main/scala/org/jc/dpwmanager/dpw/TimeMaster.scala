package org.jc.dpwmanager.dpw

import java.net.InetAddress

import akka.actor.{Actor, Cancellable, PoisonPill, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import org.apache.commons.net.ntp.{NTPUDPClient, TimeInfo}
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.jc.dpwmanager.interaction.{TimeMasterExecutable, TimeMasterFields}
import org.jc.dpwmanager.util._

import scala.concurrent.duration._
import scala.util.Random

class TimeMaster extends Actor with Watcher with TimeDataListener {

  protected var zk: Option[ZooKeeper] = None

  protected var tdm: Option[TimeDataMonitor] = None

  protected var schedulerCancellable: Option[Cancellable] = None

  protected var executable: Option[TimeMasterExecutable] = None

  protected val cluster = Cluster(context.system)

  protected val yellowPage = context.actorOf(YellowPageActor.props(self), "yellowpage")

  protected var ticketNumber: Long = 0L

  protected var timeMasterId: String = ""

  protected var active: Boolean = false

  protected var lastTick: Long = 0L

  protected var maxReportSkipForgive: Long = 0L

  protected var endedCausePoisonPill = false

  override def receive = {
    case ExecuteMaster(executable: TimeMasterExecutable, actorSystemName: String) => {
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

          this.timeMasterId = executable.commandAsMap.get(TimeMasterFields.TimeMasterId) match {
            case Some(id: String) => id
          }

          this.zk = Some(new ZooKeeper(s"$host:$port", 10000, this))
          this.tdm = Some(new TimeDataMonitor(zk = this.zk.head, timeZnode = timeZnode, timeListenersZnode = timeListenersZnode, listener = this, timeMasterId = this.timeMasterId))

          val intervalMillis = executable.commandAsMap.get(TimeMasterFields.TimeTickInterval) match {
            case Some(interval) => interval.toLong
          }

          this.executable = Some(executable)

          this.maxReportSkipForgive = executable.commandAsMap.get(TimeMasterFields.MaxForgiveMeMillis) match {
            case Some(value) => value.toLong
            case None => 0L
          }
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

      //push time tick to time listeners
      yellowPage ! PushTimeTick(topic = Utils.TOPIC_TIME_LISTENER.toString, payload = currentNetworkTime, actorSystemName = context.system.name)

      //push a keep alive report to inactive time masters
      yellowPage ! TimeMasterKeepAliveReport(timeTick = currentNetworkTime, actorSystemName = context.system.name)
    }

    case BeginTimeMasterLifeCycle =>
      if (this.active) {
        this.schedulerCancellable = Some(context.system.scheduler.schedule(0 milliseconds, this.executable.get.commandAsMap.get(TimeMasterFields.TimeTickInterval).get.toLong milliseconds, self, TimeMasterTimeTick))
      }

    case ReadTimeTick(payload, _) => {
      if (payload - this.lastTick > this.maxReportSkipForgive) {
        this.yellowPage ! RequestDeathOfActiveMaster(requesterId = this.timeMasterId, actorSystemName = self.path.address.system)
      } else {
        this.lastTick = payload
      }
    }

    case TakePoisonPill(ifActive) if ifActive && this.active => {
      this.endedCausePoisonPill = true
      this.yellowPage ! MediatorOwnerLeaving
      this.tdm.get.removeTimeZnode

      self ! PoisonPill
    }
  }

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent])

    //Register with mediator (yellow page)
    this.yellowPage ! YPRegister(actorName = self.path.name, actorSystemName = context.system.name, actorRef = self)

    this.ticketNumber = Random.nextLong()

    //attempt to create time keep alive znode. If I get to create it, I'll become the active master. First, check existence
    this.tdm.get.timeZnodeExists
  }


  override def postRestart(reason: Throwable): Unit = {
    self ! PoisonPill
  }


  override def postStop(): Unit = {
    if (this.endedCausePoisonPill) { return }

    //If I was removed from cluster, then I have to destroy the mediator (yellow page) I own.
    this.yellowPage ! MediatorOwnerLeaving
    if (this.active) {
      this.tdm.get.removeTimeZnode
    }
  }

  override def process(event: WatchedEvent) = {
    this.tdm match {
      case Some(_) => this.tdm.head.process(event)
    }
  }

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  override def timeZnodeCreated(creatorId: String, isWatchTrigger: Boolean): Unit = {
    //if this is triggered by the creation, just ignore it
    if (!isWatchTrigger) {
      this.active = creatorId.equals(this.timeMasterId)
      context.system.scheduler.scheduleOnce(delay =  20 seconds, self, message = BeginTimeMasterLifeCycle)
    }
  }

  /**
    * Callback invoked when time masters' keep alive znode is removed
    */
  override def timeZnodeRemoved: Unit = {
    if (this.active) {
      this.tdm.get.createTimeZnode(
        new TimeKeepAliveZnodePayloadBuilder()
          .addData(key = ACTOR_SYSTEM_NAME, data = self.path.address.system)
          .addData(key = TIMESTAMP, data = "0")
          .addData(key = MASTER_ID, data = this.timeMasterId)
          .addData(key = TICKET_SEQ, data = this.ticketNumber.toString).buildPayload()
      )
    }
  }

  /**
    * Callback invoked when time masters' keep alive znode changes
    */
  override def timeZnodeChanged: Unit = {

  }

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
override def connected(): Unit = {
  this.tdm.get.timeZnodeExists
}

  /**
    * Callback invoked to notify that the client disconnected from ZooKeeper
    *
    * @param rc int representing disconnection reason
    */
  override def disconnected(rc: Int): Unit = {
    this.yellowPage ! MediatorOwnerLeaving
    self ! PoisonPill
  }

  /**
    * Callback invoked when a verification for existence of znode is completed, and to signal that it hasn't been created
    * yet.
    */
  override def timeZnodeInexistent: Unit = {
    val timeZnodePayloadBuilder = new TimeKeepAliveZnodePayloadBuilder()
    timeZnodePayloadBuilder
      .addData(key = ACTOR_SYSTEM_NAME, data = self.path.address.system)
      .addData(key = MASTER_ID, data = this.timeMasterId)
      .addData(key = TIMESTAMP, data = "0")
      .addData(TICKET_SEQ, this.ticketNumber.toString)

    this.tdm.get.createTimeZnode(timeZnodePayloadBuilder.buildPayload())
  }
}
