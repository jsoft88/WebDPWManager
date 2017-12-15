package org.jc.dpwmanager.dpw

import akka.actor.Actor
import org.apache.zookeeper.{WatchedEvent, Watcher}

class WatchMaster extends Actor with Watcher {
  override def receive = ???

  override def process(event: WatchedEvent) = ???
}
