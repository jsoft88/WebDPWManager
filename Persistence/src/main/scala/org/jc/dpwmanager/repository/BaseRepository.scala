package org.jc.dpwmanager.repository

import org.jc.dpwmanager.models.BaseModel

import scala.concurrent.Future

/**
  * Created by jorge on 31/5/2017.
  * @tparam A data type of identifier field.
  * @tparam T data type of entity which extends BaseModel
  * throws Exception if initialization fails.
  */
@throws(classOf[Exception])
trait BaseRepository[A, T <: BaseModel] {

  @throws(classOf[Exception])
  def save(entity: T): Future[T]

  @throws(classOf[Exception])
  def delete(entity: T): Future[Int]

  @throws(classOf[Exception])
  def update(entity: T): Future[Int]

  @throws(classOf[Exception])
  def get(id: A): Future[Option[T]]
}
