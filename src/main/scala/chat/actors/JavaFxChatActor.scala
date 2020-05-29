package chat.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, UnreachableMember}
import chat.gui.{AuthorizationWindow, ChatWindow}
import javafx.stage.Stage

//актор, который занимается обменом сообщений с кластером и отображением окон авторизации и основного окна чата
class JavaFxChatActor(val primaryStage: Stage) extends Actor with ActorLogging
  with UserManagement
  with MessageManagement
  with ChatStorage {

  val cluster = Cluster(context.system)
  primaryStage.hide()
  var currentName = "Noname"
  val authWindow = new AuthorizationWindow(primaryStage, this)
  val chatWindow = new ChatWindow(primaryStage, this)

  override def preStart() = {
    cluster.registerOnMemberUp {
      cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
    }
  }

  override def receive = userManagement orElse messageManagement

  override def postStop() = {
    logoutCluster()
    cluster.unsubscribe(self)
  }

  def stopActorSystem() = {
    context.system.terminate()
  }

  def addUser(userName: String, ref: ActorRef) = {
    log.info("add user: {} {}", userName, ref)
    users += (userName -> ref)
    stories += (userName -> " ")
    context.watch(ref)
    chatWindow.addUserNameButton(userName)
  }

  def removeUser(userName: String) = {
    log.info("remove user: {} {}", userName)
    users -= (userName)
    stories -= (userName)
    chatWindow.removeUserNameButton(userName)
  }
}

object JavaFxChatActor {
  def props(primaryStage: Stage) = Props(new JavaFxChatActor(primaryStage: Stage))
}


