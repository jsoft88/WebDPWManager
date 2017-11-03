package utils

import akka.actor.ActorRef

final case class BusinessMessage(message: String)

final case class BusinessRegisterFlowActor(out: ActorRef)
