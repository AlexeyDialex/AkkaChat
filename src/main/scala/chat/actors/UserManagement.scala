package chat.actors

import akka.actor.{ActorLogging, ActorRef, RootActorPath, Terminated}
import akka.cluster.ClusterEvent.{MemberExited, MemberUp}
import akka.cluster.MemberStatus

import scala.collection.mutable

sealed trait Event
case class Login(userName: String) extends Event
case class Logged(userName: String) extends Event
case class Unlogged(error: String) extends Event
case object Logout extends Event

trait UserManagement {this: JavaFxChatActor =>

  val users: mutable.HashMap[String, ActorRef]
  var currentName: String

  protected def getUserName(ref: ActorRef): Option[String]
  protected def getAllUserNames(): List[String]
  protected def getRef(userName: String): Option[ActorRef]
  protected def getAllUserRefs(): List[ActorRef]

  protected def userManagement: Receive = {
    case Login(userName) => {
      if (getAllUserNames().contains(userName)) {
        sender() ! Unlogged("This name already exists")
      } else if (getAllUserRefs().contains(sender())) {
        sender() ! Unlogged("You're already logged")
      } else {
        addUser(userName, sender())
        sender() ! Logged(currentName)
        log.info("Login user: {}, {}", userName, sender())
        if (authWindow.isShowing) {
          authWindow.hide()
          chatWindow.setTitle("AlexChat: " + currentName)
          chatWindow.show()
        }
      }
    }

    case Logged(userName) => {
      if (getAllUserNames().contains(userName)) {
        sender() ! Unlogged("This name already exists")
      } else if (getAllUserRefs().contains(sender())) {
        sender() ! Unlogged("You're already logged")
      } else {
        addUser(userName, sender())
        log.info("Logged user {}, {}", userName, sender())
        if (authWindow.isShowing) {
          authWindow.hide()
          chatWindow.setTitle("AlexChat: " + currentName)
          chatWindow.show()
        }
      }
    }
    case Logout => {
      getUserName(sender()) match {
        case Some(userName) =>
          removeUser(userName)
        case None =>
          log.error("Logout from not authorized user {}", sender())
      }
    }
    case Terminated(actorRef) => {
      getUserName(actorRef) match {
        case Some(userName) =>
          removeUser(userName)
        case None =>
          log.warning("Terminated not authorized user {}", sender())
      }
    }
    case Unlogged(error) => {
      log.warning("Unlogged: {} from {}", error, sender())
      authWindow.errorRaport.setText(error)
    }
  }

  def loginCluster(name: String) = {
    val members = cluster.state.members.filter(_.status == MemberStatus.Up)
    log.info("login to Cluster members: {}", members.toString())
    members.size match {
      case 0 => {     //когда попытка авторизации произошла раньше первого MemberUp
        Thread.sleep(500)
        authWindow.errorRaport.setText("Please try again")
      }
      case 1 => self ! Logged(currentName) //если ты один в кластере, сразу регистрация
      case _ => {
        members.foreach { member =>
          val remoteNode = context.actorSelection(RootActorPath(member.address) / "user" / "javaFxChatActor")
          remoteNode ! Login(name)
          log.info("send Login to {}", remoteNode)
        }
      }
    }
  }
  def logoutCluster() = {
    getAllUserRefs().foreach(ref =>
      ref ! Logout
    )
  }
}
