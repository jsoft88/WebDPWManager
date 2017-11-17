package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.{MasterField, MasterTypeHasFields}

import scala.concurrent.Future

/**
  * Created by jorge on 22/6/2017.
  */
trait MasterTypeHasFieldsRepository extends BaseRepository[Int, MasterTypeHasFields] {

  @throws(classOf[Exception])
  def getFieldsForMaster(masterTypeId: Short, asc: Boolean): Future[Seq[(MasterField, Boolean)]]

  @throws(classOf[Exception])
  def updateOrderingOfFields(masterTypeFields: Seq[MasterTypeHasFields]): Future[Boolean]
}
