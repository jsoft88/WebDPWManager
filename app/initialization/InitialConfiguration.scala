package initialization

import akka.actor.ActorRef

trait BusinessLayerInit {
  def setupSharedConfiguration: Unit
}

object InitialConfiguration {
  @volatile private var _persistenceActorName = ""
  @volatile private var _actorSystemName = ""
  @volatile private var _persistenceActorsHosts = IndexedSeq.empty[String]
  @volatile private var _persistenceActorsPorts = IndexedSeq.empty[Int]
  @volatile private var _businessActorName = ""
  @volatile private var _businessActor: Option[ActorRef] = None

  def persistenceActorName_(name: String): Unit = _persistenceActorName = name
  def actorSystemName_(actorSystemName: String): Unit = _actorSystemName = actorSystemName
  def persistenceActorsHosts_(persistenceActorsHosts: Seq[String]): Unit = _persistenceActorsHosts = persistenceActorsHosts.toIndexedSeq
  def persistenceActorsPorts_(persistenceActorsPorts: Seq[Int]): Unit = _persistenceActorsPorts = persistenceActorsPorts.toIndexedSeq
  def businessActorName_(businessActorName: String): Unit = _businessActorName = businessActorName
  def businessActor_(businessActor: Option[ActorRef]): Unit = _businessActor = businessActor

  def persistenceActorName = _persistenceActorName
  def actorSystemName = _actorSystemName
  def persistenceActorsHosts = _persistenceActorsHosts
  def persistenceActorsPorts = _persistenceActorsPorts
  def businessActorName = _businessActorName
  def businessActor = _businessActor
}
