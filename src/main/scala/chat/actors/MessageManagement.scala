package chat.actors

import akka.actor.{ActorLogging, ActorRef}

sealed trait Message
case class PublishMessage(message: String) extends Message
case class PrivateMessage(message: String) extends Message
case class SendPublish(message: String) extends Message
case class SendPrivate(remoteName: String, message: String) extends Message

trait MessageManagement {this: JavaFxChatActor =>

  protected def messageManagement: Receive = {
    case PublishMessage(message) => {
      getUserName(sender()) match {
        case Some(userName) => {
          appendPublishStory(userName, message)
          chatWindow.publishPost(getPublishStory())
          log.info("PublishMessage: {} from {}", message, sender())
        }
        case None =>
          log.error("Not authorized user {} send publish message {}", sender(), message)
      }
    }
    case PrivateMessage(message) => {
      getUserName(sender()) match {
        case Some(userName) =>
          appendStory(userName, message)
          chatWindow.privatePost(userName, getStory(userName))
          log.info("PrivateMessage: {} from {}", message, sender())
        case None =>
          log.error("Not authorized user {} say private message{}", sender(), message)
      }
    }
  }

  def sendPublishMessage(message: String) =  {
    getAllUserRefs.foreach(userRef => {
      userRef ! PublishMessage(message)
    })
  }
  def sendPrivateMessage(userName: String, message: String) =  {
    getRef(userName) match {
      case Some(ref) =>
        ref ! PrivateMessage(message)
      case None =>
        log.error("Private message {} to not authorized user", message)
    }
  }
}
