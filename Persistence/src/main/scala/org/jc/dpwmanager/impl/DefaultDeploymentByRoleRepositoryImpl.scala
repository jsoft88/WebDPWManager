package org.jc.dpwmanager.impl

import org.jc.dpwmanager.db.Database
import org.jc.dpwmanager.models._
import org.jc.dpwmanager.repository.DeploymentByRoleRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DefaultDeploymentByRoleRepositoryImpl(implicit ec: ExecutionContext) extends DeploymentByRoleRepository with Database {

  val deploymentsByRoles = TableQuery[DeploymentByRoleTable]

  db.run(DBIO.seq(deploymentsByRoles.schema.create)) onComplete {
    case Success(_) =>
    case Failure(ex) => throw new Exception("DeploymentByRole schema creation failed: " + ex.getMessage)
  }

  override def db = slick.jdbc.JdbcBackend.Database.forConfig("database")

  override def save(entity: DeploymentByRole) = db.run(deploymentsByRoles returning deploymentsByRoles.map(_.deployId) into ((entity, deployId) => entity.copy(deployId = deployId)) += entity)

  override def delete(entity: DeploymentByRole) = db.run(deploymentsByRoles.filter(_.deployId === entity.deployId).delete)

  override def update(entity: DeploymentByRole) = {
    val q = for { d <- deploymentsByRoles if d.deployId === entity.deployId } yield (d.hostId, d.actorSystemName, d.actorName, d.port, d.roleId)
    db.run(q.update(entity.hostId, entity.actorSystemName, entity.actorName, entity.port, entity.roleId))
  }

  override def get(id: Int) = db.run(deploymentsByRoles.filter(_.deployId === id).result headOption)

  override def getDeploymentsForRoleInHost(dpwRole: DpwRoles, host: Host) = db.run(deploymentsByRoles.filter(_.roleId === dpwRole.roleId).filter(_.hostId === host.hostId).result)

  override def getAllDeploymentsForRole(dpwRole: DpwRoles) = db.run(deploymentsByRoles.filter(_.roleId === dpwRole.roleId).result)

  override def getAllDeploymentsInHost(host: Host) = db.run(deploymentsByRoles.filter(_.hostId === host.hostId).result)

  override def getActorSystemHosts(actorSystemName: String) = {
    val hosts = TableQuery[HostTable]
    val query =
      for {
        (dbr, h) <- deploymentsByRoles join hosts on (_.hostId === _.hostId)
      } yield h

    db.run(query)
  }
}
