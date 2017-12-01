package filters

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import play.api.libs.json.Json
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import utils.{LackOfRoleException, LackOfRolesConstants, LackOfRolesResponse, ReachPersistenceAgentWith}
import akka.pattern.ask
import org.jc.dpwmanager.util.QueryOnePersistenceRoleAtLeast
import play.api.routing.Router.Attrs

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class PersistenceRoleFilter @Inject()(implicit override val mat: Materializer, exec: ExecutionContext, @Named("businessActor") businessActor: ActorRef, actorSystem: ActorSystem) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {

    if (businessActor == null) {
      Future {Results.InternalServerError(Json.parse(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_BUSINESS, errorDescription = "No business actor is available")._toJson(None)))}
    } else {
      if (requestHeader.path.equals("/api/hosts/deployments/add")) {
        nextFilter(requestHeader).map(result => result)
      } else {
        (businessActor ? QueryOnePersistenceRoleAtLeast) (60 seconds).flatMap(_ => nextFilter(requestHeader).map(result => result)).recover {
          case ex: LackOfRoleException => Results.InternalServerError(Json.parse(LackOfRolesResponse(errorCode = LackOfRolesConstants.NO_PERSISTENCE, errorDescription = ex.getMessage)._toJson(None)))
        }
      }
    }
  }
}
