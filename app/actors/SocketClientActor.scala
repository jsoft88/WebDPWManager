package actors

import akka.actor.{Actor, ActorRef}
import initialization.InitialConfiguration
import utils.{BusinessMessage, BusinessRegisterFlowActor}

class SocketClientActor(out: ActorRef) extends Actor {

  override def receive: Receive = {
    case _ =>
  }

  override def preStart(): Unit = InitialConfiguration.businessActor match { case Some(r) => r ! BusinessRegisterFlowActor(out) }
}
