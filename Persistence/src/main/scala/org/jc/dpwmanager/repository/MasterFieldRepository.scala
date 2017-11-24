package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.MasterField

import scala.concurrent.Future

trait MasterFieldRepository extends BaseRepository[Int, MasterField] {

  def getFieldWithEnabledFlag(fieldId: Int): Future[Option[(MasterField, Boolean)]]
}
