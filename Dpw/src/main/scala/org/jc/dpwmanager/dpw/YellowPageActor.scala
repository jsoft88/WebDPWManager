package org.jc.dpwmanager.dpw

import akka.actor.{Actor, ActorRef, Address, PoisonPill, Props, Status}
import akka.cluster.client.ClusterClient.Publish
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.cluster.pubsub.DistributedPubSub
import org.jc.dpwmanager.util._

object YellowPageActor {
  def props(parentActorRef: ActorRef): Props = Props(classOf[YellowPageActor], parentActorRef.path.name)
}

case class YellowPageFormat(actorName: String, actorSystemName: String, actorRef: ActorRef)

class YellowPageActor(parentActorRef: ActorRef) extends Actor {

  var actorsDir: Map[String, YellowPageFormat] = Map.empty

  val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(Utils.TOPIC_TIME_LISTENER.toString, self)
  mediator ! Subscribe(Utils.TOPIC_TIME_MASTER_KEEP_ALIVE.toString, self)
  mediator ! Subscribe(Utils.TOPIC_PROCESS_OBSERVED.toString, self)
  mediator ! Subscribe(Utils.TOPIC_YP_REGISTER.toString, self)
  mediator ! Subscribe(Utils.TOPIC_YP_UNREGISTER.toString, self)
  mediator ! Subscribe(Utils.TOPIC_ACTIVENESS_COMPETITION.toString, self)

  override def receive = {
    case YPRegister(actorName, actorSystemName, actorRef) => this.actorsDir += (s"$actorSystemName$actorName" -> YellowPageFormat(actorName, actorSystemName, actorRef))

    case YPQuery(address) => this.actorsDir.filter(_._2.actorRef.path.address == address) headOption match { case Some(r) => sender ! YPResult(r._2.actorRef) case None => sender ! Status.Failure(new Exception("No results for query"))}

    case PushTimeTick(topic, payload, actorSystemName) if parentActorRef.path.address.system == actorSystemName => this.mediator ! Publish(topic = topic, msg = ReadTimeTick(payload, actorSystemName))

    case MediatorOwnerLeaving => self ! PoisonPill

    case ReadTimeTick(payload, actorSystemName) if sender != self && actorSystemName == parentActorRef.path.address.system => parentActorRef ! ReadTimeTick(payload, actorSystemName)

    case TimeMasterKeepAliveReport(timeTick, actorSystemName) if actorSystemName == self.path.address.system => this.mediator ! Publish(topic = Utils.TOPIC_TIME_MASTER_KEEP_ALIVE.toString, msg = ReadTimeTick(timeTick, actorSystemName)

    case RequestDeathOfActiveMaster(requesterId, actorSystemName) if actorSystemName == self.path.address.system => this.parentActorRef ! TakePoisonPill(ifActive = true)
  }
}
