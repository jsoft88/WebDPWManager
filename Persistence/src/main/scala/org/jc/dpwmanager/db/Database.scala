package org.jc.dpwmanager.db

/**
  * Created by jorge on 22/6/2017.
  */
trait Database {

  def db: slick.jdbc.JdbcBackend.Database
}
