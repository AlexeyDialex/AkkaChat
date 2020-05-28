package chat.actors

import akka.actor.{ActorLogging, ActorRef, RootActorPath, Terminated}
import akka.cluster.ClusterEvent.MemberExited
import akka.cluster.MemberStatus
import chat.gui.ChatWindow

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
      val members = cluster.state.members.filter(_.status == MemberStatus.Up)
      members.size match {
        case 0 => Thread.sleep(500) //на случай, если попытка авторизации произошла раньше первого MemberUp
        case 1 => self ! Logged(currentName)
        case _ => {
          if (getAllUserNames().contains(userName)) {
            sender() ! Unlogged("This name already exists")
          } else if (getAllUserRefs().contains(sender())) {
            sender() ! Unlogged("You're already logged")
          } else {
            addUser(userName, sender())
            sender() ! Logged(currentName)
            log.info("Logining user: {}, {}", userName, sender())
          }
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
    case Logged(userName) => {
      if (getAllUserNames().contains(userName)) {
        sender() ! Unlogged("This name already exists")
      } else if (getAllUserRefs().contains(sender())) {
        sender() ! Unlogged("You're already logged")
      } else {
        addUser(userName, sender())
        if (!chatWindow.isShowing) {
          authWindow.hide()
          chatWindow.setTitle("AlexChat: " + currentName)
          chatWindow.show()
          log.info("Logged user {}, {}", userName, sender())
        }
      }
    }
    case Unlogged(error) => {
      log.warning("Unlogged: {} from {}", error, sender())
      authWindow.unsuccessfulAuthorization(error)
    }
  }
  def validateName(name: String): Unit = {
    if (name.matches("""[a-zA-Z0-9]+""") & name.size < 20) {
      currentName = name
      loginCluster(currentName)
    } else {
      authWindow.unsuccessfulAuthorization("only letters or numbers")
    }
  }
  def loginCluster(name: String) = {
    val membersUp = cluster.state.members.filter(_.status == MemberStatus.Up)
    log.info("login to Cluster membersUp: {}", membersUp.toString())
    membersUp.foreach { member =>
      val remoteNode = context.actorSelection(RootActorPath(member.address) / "user" / "javaFxChatActor")
      remoteNode ! Login(name)
    }
  }
  def logoutCluster() = {
    getAllUserRefs().foreach(ref =>
      ref ! Logout
    )
  }
}
