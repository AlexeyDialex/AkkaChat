package chat.actors

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javafx.stage.Stage

object StartCluster {
  def apply(config: Config, primaryStage: Stage)= {
    val system = ActorSystem("ChatSystem", config)
    //диспетчер, чтобы работать с JavaFX
    val javaFxChatActor = system.actorOf(JavaFxChatActor.props(primaryStage: Stage).
      withDispatcher("akka.actor.javafx-dispatcher"), "javaFxChatActor")
  }
}
