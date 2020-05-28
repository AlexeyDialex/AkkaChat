package chat.gui

import chat.actors.JavaFxChatActor
import javafx.event.{ActionEvent, Event}
import javafx.scene.control.{Button, Tab}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.stage.{Stage, WindowEvent}

class ChatWindow(val primaryStage: Stage, val javaFxChatActor: JavaFxChatActor) extends Stage with ChatView {

  defaultTab.setId(javaFxChatActor.defaultRoom)

  //при выборе вкладки обновляем историю переписки в центре
  defaultTab.setOnSelectionChanged((e: Event) => {
    storyArea.setText(javaFxChatActor.getPublishStory())
  })
  sendButton.setOnAction((e: ActionEvent) => {
    send()
  })
  messageField.setOnKeyPressed((e: KeyEvent) => {
    if (e.getCode == KeyCode.ENTER) {
      send()
    }
  })

  def send() = {
    val selectedTabText = userTabs.getSelectionModel.getSelectedItem.getText
    if (selectedTabText == javaFxChatActor.defaultRoom) {
      javaFxChatActor.sendPublishMessage(messageField.getText)
    } else {
      if (selectedTabText != javaFxChatActor.currentName & javaFxChatActor.getAllUserNames().contains(selectedTabText)) {
        javaFxChatActor.sendPrivateMessage(selectedTabText, messageField.getText)
      }
      javaFxChatActor.appendStoryToMyself(selectedTabText, messageField.getText)
      storyArea.setText(javaFxChatActor.getStory(selectedTabText))
    }
    messageField.setText("")
  }

  def addUserNameButton(userName: String): Unit = {
    val userButton = new Button(userName)
    userButton.setMaxWidth(Double.MaxValue)
    userButton.setOnAction((e: ActionEvent) => {
      val tab = searchTab(userName)
      if (tab == null) {
        addTab(userName)
        println("addTab")
      }
      userTabs.getSelectionModel().select(tab)
      storyArea.setText(javaFxChatActor.getStory(userName))
    })
    userList.getChildren().add(userButton)
  }

  def removeUserNameButton(userName: String) = {
    userList.getChildren().forEach(node => {
      node match {
        case button: Button => {
          if (button.getText() == userName) {
            userList.getChildren().remove(button)
          }
        }
      }
    })
  }

  def searchTab(userName: String): Tab = {
    var foundTab: Tab = null
    userTabs.getTabs().forEach(tab => {
      if (tab.getText == userName & tab.getId != javaFxChatActor.defaultRoom) {
        foundTab = tab
      }
    })
    foundTab
  }

  def addTab(userName: String): Unit = {
    val tab = new Tab(userName)
    tab.setOnSelectionChanged((e: Event) => {
      storyArea.setText(javaFxChatActor.getStory(userName))
      println("setOnSelectionChanged to " + userName)
    })
    userTabs.getTabs().add(tab)
  }


  def publishPost(publishStory: String) = {
    println("publishPost")
    if (userTabs.getSelectionModel.getSelectedItem.getId == javaFxChatActor.defaultRoom) {
      storyArea.setText(publishStory)
    }
  }

  def privatePost(userName: String, story: String) = {
    val selectedTabText = userTabs.getSelectionModel.getSelectedItem.getText
    if (searchTab(userName) == null) {
      addTab(userName)
    }
    if (selectedTabText == userName) {
      storyArea.setText(story)
    }
  }

  this.setOnCloseRequest((e: WindowEvent) => {
    primaryStage.close()
    javaFxChatActor.stopActorSystem()
  })
}