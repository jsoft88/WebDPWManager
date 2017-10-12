package org.jc.dpwmanager.util

/**
  * Created by jorge on 7/7/2017.
  */

final object ServerRole {
  override def toString: String = "server"
}

final object AgentRole {
  override def toString: String = "agent"
}

final object PersistenceRole {
  override def toString: String = "persistence"
}

final object BusinessRole {
  override def toString: String = "business"
}