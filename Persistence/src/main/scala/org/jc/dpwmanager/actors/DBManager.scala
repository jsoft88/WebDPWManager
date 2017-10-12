package org.jc.dpwmanager.actors

import akka.actor.{Actor, Status}
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.DefaultAgentRepositoryImpl
import org.jc.dpwmanager.models.Agent

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Created by jorge on 30/6/2017.
  */
class DBManager(actorName: String, actorSystemName: String) extends Actor {

  var initializing: Boolean = true

  var stopping: Boolean = false

  var myAgentId: Short = 0

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive = {
    case CommandWrapper(command) => (command execute) onComplete {
      case Success(r: CommandResponseWrapper[_]) => sender ! r
      case Failure(ex) => sender ! Status.Failure(new Exception("Command: " + command.toString + " failed with " + ex.getMessage))
    }

      //This is processed only when registering the agent at the database
    case AgentInsertResponse(response: Agent) if initializing => myAgentId = response.agentId

      //When agent retrieved, remove it from db
    case AgentRetrieveResponse(response: Option[Agent]) if stopping && response.head != None => self ! CommandWrapper(AgentRemoveCommand(new DefaultAgentRepositoryImpl, response.head))
  }


  override def postStop(): Unit = {
    //issue a command to retrieve agent, on response, remove it from db.
    val agent = Agent(agentId = myAgentId, port = 0, host = "", actorName = "", actorSystemName = "")
    stopping = true

    self ! CommandWrapper(AgentRetrieveCommand(new DefaultAgentRepositoryImpl, agent))
  }

  override def preStart(): Unit = {
    val agent = Agent(agentId = 0, host = self.path.address.host.get, port = self.path.address.port.get, actorName = actorName, actorSystemName = actorSystemName)
    self ! CommandWrapper(AgentInsertCommand(new DefaultAgentRepositoryImpl, agent))
  }
}
