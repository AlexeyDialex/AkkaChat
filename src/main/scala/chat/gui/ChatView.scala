package chat.gui

import chat.actors.JavaFxChatActor
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.{Button, Tab, TabPane, TextArea}
import javafx.scene.layout.{Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, Priority, VBox}
import javafx.scene.paint.Color
import javafx.stage.{Modality, Stage}

trait ChatView {this: Stage =>

  val primaryStage: Stage
  val javaFxChatActor: JavaFxChatActor

  this.setTitle("AlexChat")
  val chatRoot = new BorderPane()
  val userTabs = new TabPane()
  val userList = new VBox()
  val storyArea = new TextArea()
  val posting = new HBox()

  val border = new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(3)))

  val defaultTab = new Tab(javaFxChatActor.defaultRoom)
  defaultTab.setClosable(false)
  userTabs.getTabs().add(defaultTab)
  userTabs.setBorder(border)
  chatRoot.setTop(userTabs)

  storyArea.setEditable(false)
  storyArea.setBorder(border)
  chatRoot.setCenter(storyArea)

  userList.setMinWidth(100)
  userList.setMaxWidth(100)
  userList.setBorder(border)
  chatRoot.setRight(userList)

  val messageField = new TextArea()
  messageField.setPromptText("Введите сообщение...")
  val sendButton = new Button("Send")
  sendButton.setMinSize(60, 50)

  posting.getChildren().addAll(messageField, sendButton)
  HBox.setHgrow(messageField, Priority.ALWAYS)
  posting.setAlignment(Pos.CENTER)
  posting.setBorder(border)
  posting.setFillHeight(true)
  chatRoot.setBottom(posting)

  val chatScene = new Scene(chatRoot, 700, 500)
  this.setScene(chatScene)
  this.initModality(Modality.NONE)
  this.initOwner(primaryStage)
  this.hide()

}
