package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.{DeploymentByRole, DpwRoles, Host}

import scala.concurrent.Future

trait DeploymentByRoleRepository extends BaseRepository[Int, DeploymentByRole]{

  def getDeploymentsForRoleInHost(dpwRole: DpwRoles, host: Host): Future[Seq[DeploymentByRole]]

  def getAllDeploymentsForRole(dpwRole: DpwRoles): Future[Seq[DeploymentByRole]]

  def getAllDeploymentsInHost(host: Host): Future[Seq[DeploymentByRole]]

  def getActorSystemHosts(actorSystemName: String): Future[Seq[Host]]

  def getActorSystems(): Future[Seq[String]]
}
