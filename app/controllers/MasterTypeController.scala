package controllers

import javax.inject.{Inject, Singleton}

import akka.pattern.ask
import akka.util.Timeout
import initialization.InitialConfiguration
import org.jc.dpwmanager.commands._
import org.jc.dpwmanager.impl.{DefaultMasterFieldRepositoryImpl, DefaultMasterTypeHasFieldsRepositoryImpl, DefaultMasterTypeImpl}
import org.jc.dpwmanager.models.{MasterField, MasterType, MasterTypeHasFields}
import play.api.libs.json.{JsPath, Json, Writes}
import scala.concurrent.duration._
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.functional.syntax._

@Singleton
class MasterTypeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private def noBusinessActor = "No business actor found to fulfill this request"

  implicit val timeout = Timeout(15 seconds)

  implicit val masterTypeUIWrites: Writes[MasterTypeUIModel] = (
    (JsPath \ "masterTypeId").write[Short] and
      (JsPath \ "masterLabel").write[String]
  )(unlift(MasterTypeUIModel.unapply))

  implicit val masterFieldUIWrites: Writes[MasterFieldUIModel] = (
    (JsPath \ "fieldId").write[Int] and
      (JsPath \ "fieldName").write[String] and
      (JsPath \ "fieldDescription").write[String]
  )(unlift(MasterFieldUIModel.unapply))

  def getMasterTypes() = Action.async { implicit request =>
    val dummyMasterType = MasterType(label = "", masterTypeId = 0)

    InitialConfiguration.businessActor match {
      case Some(ar) => {
        (ar ? CommandWrapper(MasterTypesListRetrieveCommand(new DefaultMasterTypeImpl(), dummyMasterType))).mapTo[MasterTypesListRetrieveResponse]
          .map(s => Ok(Json.toJson(s.response.map(each => MasterTypeUIModel(masterTypeId = each.masterTypeId, masterLabel = each.label))))) recover { case ex => InternalServerError("Could not retrieve Master Types: " + ex.getMessage)}
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getMasterType(masterTypeId: Short) = Action.async { implicit request =>
    val dummyMasterType = MasterType(label = "", masterTypeId = masterTypeId)

    InitialConfiguration.businessActor match {
      case Some(ar) => {
        (ar ? CommandWrapper(SingleMasterTypeRetrieveCommand(new DefaultMasterTypeImpl(), dummyMasterType))).mapTo[SingleMasterTypeRetrieveResponse]
          .map(s => Ok(Json.toJson(MasterTypeUIModel(masterTypeId = s.response.masterTypeId, masterLabel = s.response.label)))) recover { case ex => InternalServerError("An error occurred while retrieving Master Type: " + ex.getMessage)}
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getFieldsForMaster(masterTypeId: Short) = Action.async { implicit request =>
    val dummyEntity = MasterTypeHasFields(masterTypeId = masterTypeId, fieldId = 0, fieldEnabled = false, ordering = 0, masterTypeHasFieldsId = 0)

    InitialConfiguration.businessActor match {
      case Some(ar) => {
        (ar ? CommandWrapper(MasterFieldsRetrieveCommandAsc(new DefaultMasterTypeHasFieldsRepositoryImpl(), dummyEntity))).mapTo[MasterFieldsRetrieveResponse]
          .map(s => Ok(Json.toJson(s.response.map(mf => MasterFieldUIModel(fieldId = mf.fieldId, fieldName = mf.fieldName, fieldDescription = mf.fieldDescription))))) recover { case ex => InternalServerError(ex.getMessage) }
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }

  def getFieldById(fieldId: Int) = Action.async { implicit request =>
    val dummyEntity = MasterField(fieldId = fieldId, fieldName = "", fieldDescription = "", javaTypePattern = "")

    InitialConfiguration.businessActor match {
      case Some(ar) => {
        (ar ? CommandWrapper(SingleFieldRetrieveCommand(new DefaultMasterFieldRepositoryImpl(), dummyEntity))).mapTo[SingleFieldRetrieveResponse]
          .map(s => Ok(Json.toJson(s.response))) recover { case ex => InternalServerError(ex.getMessage) }
      }
      case None => Future.successful(InternalServerError(noBusinessActor))
    }
  }
}

case class MasterTypeUIModel(masterTypeId: Short, masterLabel: String)