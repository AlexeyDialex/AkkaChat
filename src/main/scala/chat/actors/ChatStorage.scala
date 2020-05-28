package chat.actors

import akka.actor.ActorRef

import scala.collection.mutable

trait ChatStorage {
  var currentName: String

  val defaultRoom = "common"
  val users = mutable.HashMap[String, ActorRef]() //имена и ссылки на пользователей
  val stories = mutable.HashMap[String, String]() //истории переписки пользователей
  var publishStory = "Welcome!"

  def removeStory(userName: String): Unit = {
    stories -= (userName)
  }

  def appendStory(userName: String, message: String): Unit = {
    println(s"appendStory: ${userName}, ${message}")
    if (!getAllUserNames().contains(userName)) {
      stories += (userName -> " ")
    }
    stories(userName) = stories(userName) + "\n" + userName + ": " + message
    println(s"story: ${stories(userName)}")
  }

  def appendStoryToMyself(userName: String, message: String): Unit = {
    stories(userName) = stories(userName) + "\n" + currentName + ": " + message
  }

  def appendPublishStory(userName: String, message: String): Unit = {
    publishStory = publishStory + "\n" + userName + ": " + message
  }

  def getStory(userName: String): String = {
    stories.get(userName).getOrElse("")
  }

  def getPublishStory(): String = {
    publishStory
  }

  def getUserName(ref: ActorRef): Option[String] = {
    users.find(_._2 == ref).map(_._1)
  }

  def getAllUserNames(): List[String] = {
    users.keys.toList
  }

  def getRef(userName: String): Option[ActorRef] = {
    users.get(userName)
  }

  def getAllUserRefs(): List[ActorRef] = {
    users.values.toList
  }
}