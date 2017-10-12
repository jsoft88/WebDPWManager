package org.jc.dpwmanager.commands

import org.jc.dpwmanager.models.BaseModel
import org.jc.dpwmanager.repository.BaseRepository

import scala.concurrent.Future

/**
  * Created by jorge on 3/7/2017.
  */

/**
  *
  * @param repository Repository implementation which contains methods that could be invoked by this command.
  * @param entity Instance of the model.
  * @tparam A data type of id field of the entity
  * @tparam B Class of the model extending BaseModel
  * @tparam T Data type of the command's response wrapper.
  */
abstract class Command[+A, +B <: BaseModel, +T <: CommandResponseWrapper[X] forSome { type X }](repository: BaseRepository[A, B], entity: B) {

  @throws(classOf[Exception])
  def execute: Future[T]
}
