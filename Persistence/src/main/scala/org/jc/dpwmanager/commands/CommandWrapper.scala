package org.jc.dpwmanager.commands


import org.jc.dpwmanager.models.BaseModel
import org.jc.dpwmanager.util.PersistenceCommand

/**
  * Created by jorge on 7/7/2017.
  */
case class CommandWrapper(command: Command[A forSome { type A }, B forSome { type B <: BaseModel}, C forSome { type C <: CommandResponseWrapper[_]}]) extends PersistenceCommand
