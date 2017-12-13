package org.jc.dpwmanager.util

import akka.actor.{ActorRef, Address}
import org.jc.dpwmanager.interaction.IExecutable

case class ExecuteMaster(executable: IExecutable, actorSystemName: String)

case class YPRegister(actorName: String, actorSystemName: String, actorRef: ActorRef)

case class YPQuery(address: Address)

case class YPResult(actorRef: ActorRef)

case class PushTimeTick(topic: String, payload: Long, actorSystemName: String)

case class ReadTimeTick(payload: Long, actorSystemName: String)

case class TimeMasterKeepAliveReport(timeTick: Long, actorSystemName: String)

object BeginTimeMasterLifeCycle

object MediatorOwnerLeaving

case object TimeMasterTimeTick