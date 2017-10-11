package org.jc.dpwmanager.actors

import akka.actor.{ActorSystem, Props}
import org.jc.dpwmanager.commands.{AgentInsertCommand, CommandWrapper}
import org.jc.dpwmanager.impl.DefaultAgentRepositoryImpl
import org.jc.dpwmanager.models.Agent

/**
  * Created by jorge on 3/7/2017.
  */
object ActorStart extends App {

  override def main(args: Array[String]): Unit = {
    val Array(host, port, actorName, actorSystemName) = args
    val system = ActorSystem(actorSystemName)

    val persistenceActor = system.actorOf(Props(new DBManager(actorName = actorName, actorSystemName = actorSystemName)), name = actorName)
  }
}
