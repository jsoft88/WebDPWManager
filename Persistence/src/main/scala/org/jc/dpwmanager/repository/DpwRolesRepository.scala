package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.DpwRoles

import scala.concurrent.Future

trait DpwRolesRepository extends BaseRepository[String, DpwRoles]{
  def getRoles(): Future[Seq[DpwRoles]]
}
