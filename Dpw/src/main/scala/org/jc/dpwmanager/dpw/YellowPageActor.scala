package org.jc.dpwmanager.dpw

import akka.actor.{Actor, ActorRef, Status}
import org.jc.dpwmanager.util.{YPQuery, YPRegister}

class YellowPageActor extends Actor {

  val mediator = akka.cluster.pubsu

  override def receive = {
    case YPRegister(actorName, actorRef) => this.mapping += (actorName -> actorRef)

    case YPQuery(address) => this.mapping.filter(_._2.path.address == address) headOption match { case Some((k, v)) => sender ! v case None => sender ! Status.Failure(new Exception("Agent not registered."))}
  }
}
