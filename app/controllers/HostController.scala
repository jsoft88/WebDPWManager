package controllers

import javax.inject.{Inject, Named, Singleton}

import actors.SocketClientActor
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import initialization.InitialConfiguration
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import utils._
import akka.pattern.{AskTimeoutException, ask}
import akka.stream.Materializer
import akka.util.Timeout
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl._
import org.jc.dpwmanager.models._
import org.jc.dpwmanager.util.{DeployRoleWrapper, PersistenceRole, RoleTranslator, StartRoleInHost}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.streams.ActorFlow

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object WebSocketActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case _ => out ! ("There are no persistence roles available in cluster, most operations through web manager will fail.")
  }
}

@Singleton
class HostController @Inject()(@Named("businessActor") businessActor: ActorRef, cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) with ReadsAndWrites {

  implicit val timeout = Timeout(15 seconds)

  InitialConfiguration.businessActor_(Some(businessActor))

  implicit val ec:ExecutionContext = system.dispatcher

/*  implicit val masterFieldReads: Reads[MasterFieldUIModel] = ReadsAndWrites.masterFieldReads

  implicit val executionDetailsReads: Reads[AgentExecutionDetailsUIModel] = ReadsAndWrites.executionDetailsReads

  implicit val dpwRolesReads: Reads[DpwRolesUIModel] = ReadsAndWrites.dpwRolesReads

  implicit val deploymentByRoleReads: Reads[DeploymentByRoleUIModel] = ReadsAndWrites.deploymentByRoleReads

  implicit val masterTypeReads: Reads[MasterTypeUIModel] = ReadsAndWrites.masterTypeReads

  implicit val agentExecutionReads: Reads[AgentExecutionUIModel] = ReadsAndWrites.agentExecutionReads

  implicit val hostReads: Reads[HostUIModel] = ReadsAndWrites.hostReads

  implicit val dpwRolesWrites: Writes[DpwRolesUIModel] = ReadsAndWrites.dpwRolesWrites

  implicit val deploymentByRoleWrites: Writes[DeploymentByRoleUIModel] = ReadsAndWrites.deploymentByRoleWrites

  implicit val hostWrites: Writes[HostUIModel] = ReadsAndWrites.hostWrites

  implicit val lackResponseReads: Reads[LackOfRolesResponse] = ReadsAndWrites.lackOfRolesReads

  implicit val lackResponseWrites: Writes[LackOfRolesResponse] = ReadsAndWrites.lackOfRolesWrites*/

  implicit val addFormWrites: Writes[AddHostResponse] = (
    (JsPath \ "host").write[HostUIModel] and
      (JsPath \ "errors").write[Seq[String]]
  )(unlift(AddHostResponse.unapply))

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      Props(new SocketClientActor(out))
    }
  }

  def getAllRoles = Action.async { implicit request =>
    val dpwRole = DpwRoles(roleId = "none", roleLabel = "", roleDescription = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(RetrieveDpwRolesCommand(new DefaultDpwRolesRepositoryImpl(), dpwRole)))).mapTo[RetrieveDpwRolesResponse]
        .map(s => Ok(Json.toJson(s.response.map(r => DpwRolesUIModel(roleId = r.roleId, roleLabel = r.roleLabel, roleDescription = r.roleDescription))))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex: Exception => InternalServerError("An error occurred while retrieving DPW roles: " + ex.getMessage)}
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  @Deprecated
  def getAgentRole(agentId: Int) = Action.async { implicit request =>
    val agent = Agent(port = 0, agentId = agentId.toShort, host = "", actorName = "", actorSystemName = "", roleId = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(AgentRetrieveCommand(new DefaultAgentRepositoryImpl(), agent)))).mapTo[AgentRetrieveResponse].flatMap(s =>
        s.response match {
          case Some(ag) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DpwRoleRetrieveCommand(new DefaultDpwRolesRepositoryImpl(), DpwRoles(roleId = ag.roleId, roleLabel = "", roleDescription = ""))))).mapTo[DpwRoleRetrieveResponse].map(r => r.response headOption match {
            case Some(role) => Ok(Json.toJson(DpwRolesUIModel(roleId = role.roleId, roleLabel = role.roleLabel, roleDescription = role.roleDescription)))
            case None => Ok(Json.toJson(DpwRolesUIModel(roleId = "-1", roleLabel = "No roles assigned", roleDescription = "Assign a role first.")))
          })
          case None => Future.successful(InternalServerError("There isn't an agent for the given ID. Please retry."))
        }
      )
      case None => Future.successful(InternalServerError("No business actor available to fulfill this request"))
    }
  }

  private def noBusinessActor = "No business actor available to fulfill this request"

  def getAllHosts() = Action.async { implicit request =>
    val dummyHost = Host(hostId = 0, address = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(HostListRetrieveCommand(new DefaultHostRepositoryImpl(), dummyHost)))).mapTo[HostListRetrieveResponse]
        .map(s => Ok(Json.toJson(s.response.map(h => HostUIModel(hostId = h.hostId, address = h.address, deployments = List.empty, executions = List.empty))))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getDeploymentsForHost(hostId: Int) = Action.async { implicit request =>
    val dummyDeploymentByRole = DeploymentByRole(deployId = 0, hostId = hostId.toShort, actorName = "", actorSystemName = "", componentId = 0, port = 0, roleId = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DeploymentsForHostListCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeploymentByRole)))).mapTo[DeploymentsForHostListResponse]
        .map(s => Ok(Json.toJson(s.response.map(dp => DeploymentByRoleUIModel(deployId = dp.deployId, actorName = dp.actorName, actorSystemName = dp.actorSystemName, port = dp.port, componentId = dp.componentId, role = DpwRolesUIModel(roleId = dp.roleId, roleLabel = "", roleDescription = "")))))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex =>  InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getAllHostsWhereRoleDeployed(roleId: String) = Action.async { implicit request =>
    val dummyRoleDeploy = DeploymentByRole(deployId = 0, roleId = roleId, hostId = 0, actorName = "", actorSystemName = "", componentId = 0, port = 0)

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DeploymentsForRoleHostListCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyRoleDeploy)))).mapTo[DeploymentsForRoleHostListResponse]
        .map(s => Ok(Json.toJson(s.response.map(dp => DeploymentByRoleUIModel(deployId = dp.deployId, actorName = dp.actorName, actorSystemName = dp.actorSystemName, port = dp.port, componentId = dp.componentId, role = DpwRolesUIModel(roleId = dp.roleId, roleLabel = "", roleDescription = "")))))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getSingleDeploymentForRoleById(deployId: Int) = Action.async { implicit request =>
    val dummyDeploymentByRole = DeploymentByRole(deployId = deployId, actorName = "", actorSystemName = "", port = 0, hostId = 0, componentId = 0, roleId = "")
    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(SingleDeploymentByRoleRetrieveCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeploymentByRole)))).mapTo[SingleDeploymentByRoleRetrieveResponse]
        .map(s => s.response match { case Some(dp) => Ok(Json.toJson(DeploymentByRoleUIModel(deployId = dp.deployId, actorName = dp.actorName, actorSystemName = dp.actorSystemName, port = dp.port, componentId = dp.componentId, role = DpwRolesUIModel(roleId = dp.roleId, roleLabel = "", roleDescription = "")))) case None => InternalServerError("A deployment for the given ID does not exist.")})
          .recover {
            case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
            case ex => InternalServerError(ex.getMessage)
          }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getRoleById(roleId: String) = Action.async { implicit request =>
    val dummyDpwRole = DpwRoles(roleId = roleId, roleLabel = "", roleDescription = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(SingleDpwRoleRetrieveCommand(new DefaultDpwRolesRepositoryImpl(), dummyDpwRole)))).mapTo[SingleDpwRoleRetrieveResponse]
        .map(s => Ok(Json.toJson(s.response match { case Some(r) => DpwRolesUIModel(roleId = roleId, roleLabel = r.roleLabel, roleDescription = r.roleDescription) case None => DpwRolesUIModel(roleId = "", roleDescription = "Not found", roleLabel = "Not found") }))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex => InternalServerError(ex.getMessage)

      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getRolesWithExecutionPermission = Action.async { implicit request =>
    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(RetrieveDpwRolesExecutableCommand(repository = new DefaultDpwRoleConstraintRepositoryImpl(), entity = DpwRoleConstraint(constraintId = 0, roleId = "", canExecute = false)))))
          .mapTo[RetrieveDpwRolesExecutableResponse].map(s => Ok(Json.toJson(s.response.map(r => DpwRolesUIModel(roleId = r.roleId, roleDescription = r.roleDescription, roleLabel = r.roleLabel))))).recover {
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getDeploymentsForRoleInActorSystem(roleId: String, actorSystemName: String) = Action.async { implicit request =>
    val dummyDeploymentByRole = DeploymentByRole(deployId = 0, actorName = "", actorSystemName = actorSystemName, hostId = 0, roleId = roleId, componentId = 0, port = 0)
    InitialConfiguration.businessActor match {
      case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(RetrieveDeploymentsForRoleBySystemCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeploymentByRole))))
          .mapTo[RetrieveDeploymentsForRoleBySystemResponse].map(s => Ok(Json.toJson(s.response.map(dbr => DeploymentByRoleUIModel(deployId = dbr.deployId, actorSystemName = dbr.actorSystemName, role = DpwRolesUIModel(ro))))))
    }
  }

  def validateHostJson[H: Reads] = parse.json.validate(
    _.validate[H].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def addHost() = Action(parse.json).async { request =>
    val hostResult = request.body.validate[HostUIModel]
    hostResult.fold(
      errors => {
        val dummyHost = HostUIModel(hostId = 0, deployments = List.empty, address = "", executions = List.empty)
        Future.successful(InternalServerError(Json.toJson(AddHostResponse(host = dummyHost, errors = Seq(JsError.toJson(errors).toString()))).toString()))
      },
      host => {
        val hostToSave = Host(hostId = 0, address = host.address)
        val deploymentByRole = DeploymentByRole(
          deployId = 0,
          hostId = 0,
          actorName = host.deployments.head.actorName,
          actorSystemName = host.deployments.head.actorSystemName,
          roleId = host.deployments.head.role.roleId,
          componentId = 0,
          port = host.deployments.head.port)


        InitialConfiguration.businessActor match {
          case Some(ar) => {
            (ar ? ReachPersistenceAgentWith(CommandWrapper(HostInsertCommand(new DefaultHostRepositoryImpl(), hostToSave))))
              .mapTo[HostInsertResponse]
                .flatMap(s => (ar ? CommandWrapper(DeploymentInsertCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentByRole.copy(hostId = s.response.hostId))))
                  .mapTo[DeploymentInsertResponse]
                  .map(dins => {
                    (ar ? StartRoleInHost(
                      DeployRoleWrapper(
                        actorName = dins.response.actorName,
                        actorSystemName = dins.response.actorSystemName,
                        address = s.response.address,
                        port = dins.response.port,
                        role = RoleTranslator.translateRole(dins.response.roleId))))
                    Ok(Json.toJson(AddHostResponse(host = host.copy(hostId = s.response.hostId, deployments = List(host.deployments.head.copy(deployId = dins.response.deployId, componentId = dins.response.componentId))), errors = Seq())))
                  })
                  .recover {
                    case dpInsertEx => InternalServerError(Json.toJson(AddHostResponse(host = host.copy(hostId = s.response.hostId), errors = Seq(dpInsertEx.getMessage))))
                  }).recover {
              case ex => InternalServerError(Json.toJson(AddHostResponse(host, errors = Seq("Failed to add host to cluster. Error is: " + ex.getMessage))))
            }
          }
          case None => Future.successful(InternalServerError(noBusinessActor))
        }
      }
    )
  }

  def getAllHostsInActorSystemCluster(actorSystemName: String) = Action.async { request =>
    val dummyDeployment = DeploymentByRole(deployId = 0, actorName = "", actorSystemName = actorSystemName, hostId = 0, port = 0, componentId = 0, roleId = "")

    InitialConfiguration.businessActor match {
      case Some(r) => (r ? ReachPersistenceAgentWith(CommandWrapper(HostsPerClusterRetrieveCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeployment))))
          .mapTo[HostsPerClusterRetrieveResponse].map(s => Ok(Json.toJson(s.response.map(h => HostUIModel(hostId = h.hostId, address = h.address, deployments = List.empty, executions = List.empty))))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex => InternalServerError("Could not retrieve hosts in selected cluster. Error is: " + ex.getMessage)
      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def getAllActorSystems = Action.async { request =>
    val dummyDeployment = DeploymentByRole(deployId = 0, actorName = "", actorSystemName = "", hostId = 0, port = 0, componentId = 0, roleId = "")

    InitialConfiguration.businessActor match {
      case Some(r) => (r ? ReachPersistenceAgentWith(CommandWrapper(ActorSystemsRetrieveCommand(new DefaultDeploymentByRoleRepositoryImpl(), dummyDeployment)))).mapTo[ActorSystemsRetrieveResponse].map(s => Ok(Json.toJson(s.response))) recover {
        case ex:LackOfRoleException => InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = ex.getErrorCode, errorDescription = ex.getMessage)))
        case ex => InternalServerError(ex.getMessage)
      }
      case None => Future.successful(InternalServerError(Json.toJson(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = noBusinessActor))))
    }
  }

  def deployNewRole() = Action(parse.json).async { request =>
    val deploymentResult = request.body.validate[HostUIModel]
    deploymentResult.fold(
      errors => {
        val dummyHost = HostUIModel(hostId = 0, deployments = List.empty, address = "", executions = List.empty)
        Future.successful(InternalServerError(Json.toJson(AddHostResponse(host = dummyHost, errors = Seq(JsError.toJson(errors).toString())))))
    },
      host => {
      val deploymentToAdd =
        DeploymentByRole(
          deployId = 0,
          actorName = host.deployments.head.actorName,
          actorSystemName = host.deployments.head.actorSystemName,
          componentId = 0,
          roleId = host.deployments.head.role.roleId,
          port = host.deployments.head.port,
          hostId = host.hostId
        )

        InitialConfiguration.businessActor match {
          case Some(ar) => (ar ? ReachPersistenceAgentWith(CommandWrapper(DeploymentInsertCommand(new DefaultDeploymentByRoleRepositoryImpl(), deploymentToAdd))))
              .mapTo[DeploymentInsertResponse].map(s => Ok(Json.toJson(AddHostResponse(host.copy(deployments = List(host.deployments.head.copy(deployId = s.response.deployId, componentId = s.response.componentId))), Seq()))))
              .recover {
                case ex: LackOfRoleException => {
                  if (RoleTranslator.translateRole(deploymentToAdd.roleId).equals(PersistenceRole.toString)) {
                    ar ! StartRoleInHost(DeployRoleWrapper(actorName = deploymentToAdd.actorName, actorSystemName = deploymentToAdd.actorSystemName, address = host.address, port = deploymentToAdd.port, role = PersistenceRole))
                    Ok(Json.toJson(AddHostResponse(host.copy(deployments = List(host.deployments.head.copy(deployId = 0, componentId = 0))), Seq())))
                  } else {
                    InternalServerError(Json.toJson(AddHostResponse(host = host, errors = Seq("Error while deploying role: " + ex.getMessage))))
                  }
                }
                case ex => InternalServerError(Json.toJson(AddHostResponse(host = host, errors = Seq("Error while deploying role: " + ex.getMessage))))
              }
          case None => Future.successful(InternalServerError(Json.toJson(AddHostResponse(host = host, errors = Seq(noBusinessActor)))))
        }
    })
  }
}

case class AddHostResponse(host: HostUIModel, errors: Seq[String])

case class HostUIModel(hostId: Short, address: String, deployments: List[DeploymentByRoleUIModel], executions: List[AgentExecutionUIModel])

case class DpwRolesUIModel(roleId: String, roleLabel: String, roleDescription: String)

case class DeploymentByRoleUIModel(deployId: Int, actorName: String, actorSystemName: String, port: Short, role: DpwRolesUIModel, componentId: Short)