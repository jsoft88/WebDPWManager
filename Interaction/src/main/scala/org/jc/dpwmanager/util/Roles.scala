package org.jc.dpwmanager.util

/**
  * Created by jorge on 7/7/2017.
  */

trait Role

final object ServerRole extends Role {
  override def toString: String = "server"
}

final object AgentRole extends Role {
  override def toString: String = "agent"
}

final object PersistenceRole extends Role {
  override def toString: String = "persistence"
}

final object BusinessRole extends Role {
  override def toString: String = "business"
}

final object RoleTranslator {
  def translateRole(roleId: String): Role = {
    roleId match {
      case ServerRole.toString => ServerRole
      case AgentRole.toString => AgentRole
      case PersistenceRole.toString => PersistenceRole
      case BusinessRole.toString => BusinessRole
    }
  }
}