package chat.actors

import com.typesafe.config.ConfigFactory

import scala.collection.immutable

object ClusterConfig {
  val selfIP = "127.0.0.1"
  def apply(chatConfig: immutable.HashMap[String, String]) = ConfigFactory.parseString(
    s"""
    akka {
      remote {
        artery {
          canonical.hostname = ${selfIP}
          canonical.port = ${chatConfig.get("selfPort").getOrElse("2555").toInt}
      }
    }
    cluster {
      seed-nodes = [
        "akka://ChatSystem@${chatConfig.get("selfIP").getOrElse("127.0.0.1")}:${chatConfig.get("selfPort").getOrElse("2555").toInt}",
        "akka://ChatSystem@${chatConfig.get("seedIP").getOrElse("127.0.0.1")}:${chatConfig.get("seedPort").getOrElse("2551").toInt}"]
    }
  }
  """)
}
