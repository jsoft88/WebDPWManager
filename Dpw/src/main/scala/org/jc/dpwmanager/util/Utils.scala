package org.jc.dpwmanager.util

import org.jc.dpwmanager.util.Utils.Constant

object Utils {

  sealed trait Constant

  case object TOPIC_TIME_LISTENER extends Constant {
    override def toString: String = "topic_time_listener"
  }

  case object TOPIC_TIME_MASTER_KEEP_ALIVE extends Constant {
    override def toString: String = "topic_master_keep_alive"
  }

  case object TOPIC_PROCESS_OBSERVED extends Constant {
    override def toString: String = "topic_process_observed"
  }

  case object TOPIC_YP_REGISTER extends Constant {
    override def toString: String = "topic_yp_register"
  }

  case object TOPIC_YP_UNREGISTER extends Constant {
    override def toString: String = "topic_yp_unregister"
  }

  case object TOPIC_ACTIVENESS_COMPETITION extends Constant {
    override def toString: String = "topic_activeness_competition"
  }

  case object TIME_KEEP_ALIVE_ZNODE_PAYLOAD extends Constant {
    override def toString: String = "tkalzp"
  }

  case object TIME_LISTENERS_ZNODE_PAYLOAD extends Constant {
    override def toString: String = "tlzp"
  }
}

sealed trait PayloadKey {
  def index: Int
}

sealed trait PayloadUtils {

  def deserializePayload(payload: String): Unit

  def buildPayload(): String

  def getValueByKey(key: PayloadKey): String
}

case object ACTOR_SYSTEM_NAME extends PayloadKey {
  override def index: Int = 0
}

case object MASTER_ID extends PayloadKey {
  override def index: Int = 1
}

case object TIMESTAMP extends PayloadKey {
  override def index: Int = 2
}

case object TICKET_SEQ extends PayloadKey {
  override def index: Int = 3
}

class TimeKeepAliveZnodePayloadBuilder extends PayloadUtils {

  var container: Array[String] = Array.empty

  val splitChar = ";"

  def addData(key: PayloadKey, data: String): TimeKeepAliveZnodePayloadBuilder = {
    this.container(key.index) = data
    return this
  }

  def buildPayload(): String = this.container.mkString(splitChar)

  override def deserializePayload(payload: String): Unit = {
    this.container = payload.split(splitChar)
  }

  override def getValueByKey(key: PayloadKey): String = {
    if (this.container.isEmpty || this.container.length < key.index) ""
    else this.container(key.index)
  }
}
