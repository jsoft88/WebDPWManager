package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.MasterType

import scala.concurrent.Future

/**
  * Created by jorge on 7/6/2017.
  */
trait MasterTypeRepository extends BaseRepository[Short, MasterType] {

  @throws(classOf[Exception])
  def getAllMasterTypes: Future[Seq[MasterType]]
}
