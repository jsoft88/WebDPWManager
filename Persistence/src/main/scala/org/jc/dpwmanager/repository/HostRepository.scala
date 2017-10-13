package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.Host

import scala.concurrent.Future

trait HostRepository extends BaseRepository[Short, Host]{

  def getHosts(): Future[Seq[Host]]
}
