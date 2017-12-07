package org.jc.dpwmanager.util

import akka.actor.{ActorRef, Address}
import org.jc.dpwmanager.interaction.IExecutable

case class ExecuteMaster(executable: IExecutable)

case class YPRegister(actorName: String, actorRef: ActorRef)

case class YPQuery(address: Address)

case object TimeMasterTimeTick