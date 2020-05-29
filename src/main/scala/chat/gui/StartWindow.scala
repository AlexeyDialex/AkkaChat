package chat.gui

import chat.actors.{ClusterConfig, StartCluster}
import javafx.application.{Application, Platform}
import javafx.event.ActionEvent
import javafx.geometry.{HPos, Insets}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}
import javafx.scene.paint.Color
import javafx.stage.Stage
import scala.collection.immutable

//запускаем окно для ввода параметров кластера
class StartWindow extends Application {

  def start(primaryStage: Stage) = {
    primaryStage.setTitle("Configuration")

    val selfPortLabel = new Label(" your port: ")
    val selfPortText = new TextField("2551")
    selfPortText.setMaxWidth(80)
    val selfPortError = new Label("")
    selfPortError.setTextFill(Color.RED)
    val seedIpLabel = new Label("seed ip: ")
    val seedIpText = new TextField("127.0.0.1")
    seedIpText.setMaxWidth(80)
    val seedIpError = new Label("")
    seedIpError.setTextFill(Color.RED)
    val seedPortLabel = new Label("seed port: ")
    val seedPortText = new TextField("2552")
    seedPortText.setMaxWidth(80)
    val seedPortError = new Label("")
    seedPortError.setTextFill(Color.RED)

    val startClusterError = new Label("")
    startClusterError.setTextFill(Color.RED)
    val startClusterButton = new Button("Start node")
    startClusterButton.setOnAction((e: ActionEvent) => {
      selfPortError.setText("")
      seedIpError.setText("")
      seedPortError.setText("")
      if (!validatePort(selfPortText.getText())) {
        selfPortError.setText("port must be 0 to 65 535")
      } else if (!validatePort(seedPortText.getText())) {
        seedPortError.setText("port must be 0 to 65 535")
      } else if (!validatePort(seedPortText.getText())) {
        seedIpError.setText("ip must be 0 to 255.0 to 255.0 to 255.0 to 255")
      } else {
        val congigData = immutable.HashMap[String, String]("selfPort" -> selfPortText.getText(),
          "seedIp" -> seedIpText.getText(), "seedPort" -> seedPortText.getText())
        try {
          StartCluster(ClusterConfig(congigData), primaryStage)
        } catch {
          case e: Exception => Platform.runLater(() =>
            startClusterError.setText(e.toString)
          )
        }
      }
    })

    val root = new GridPane()
    // строим сетку
    root.getColumnConstraints().add(new ColumnConstraints(70))
    root.getColumnConstraints().add(new ColumnConstraints(80))
    root.getColumnConstraints().add(new ColumnConstraints(90))
    root.getColumnConstraints().add(new ColumnConstraints(60))
    root.getColumnConstraints().add(new ColumnConstraints(100))
    root.getRowConstraints().add(new RowConstraints(40))
    root.getRowConstraints().add(new RowConstraints(40))
    root.getRowConstraints().add(new RowConstraints(40))
    root.getRowConstraints().add(new RowConstraints(40))
    root.getRowConstraints().add(new RowConstraints(40))
    root.getRowConstraints().add(new RowConstraints(80))

    root.setPadding(new Insets(10, 10, 10, 10))
    root.add(selfPortLabel, 0, 2)
    root.add(selfPortText, 1, 2)
    root.add(selfPortError, 0, 3, 2, 1)
    root.add(seedIpLabel, 3, 0)
    root.add(seedIpText, 4, 0)
    root.add(seedIpError, 3, 1, 2, 1)
    root.add(seedPortLabel, 3, 2)
    root.add(seedPortText, 4, 2)
    root.add(seedPortError, 3, 3, 2, 1)
    root.add(startClusterButton, 2, 4)
    root.add(startClusterError, 0, 5, 5, 1)
    GridPane.setHalignment(startClusterButton, HPos.CENTER)
    root.setPadding(new Insets(10, 10, 10, 10))

    val scene = new Scene(root, 500, 300);
    scene.setOnKeyPressed((e: KeyEvent) => {
      if (e.getCode == KeyCode.ENTER) {
        val congigData = immutable.HashMap[String, String]("selfPort" -> selfPortText.getText(),
          "seedIp" -> seedIpText.getText(), "seedPort" -> seedPortText.getText())
        StartCluster.apply(ClusterConfig(congigData), primaryStage)
      }
    })
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  def validatePort(port: String): Boolean = {
    port.matches("""\d+""") & port.toInt < 65536
  }

  def validateIP(ip: String): Boolean = {
    ip.matches(""".*?(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3}).*""")
  }
}

object StartWindow {
  def main(args: Array[String]) {
    Application.launch(classOf[StartWindow], args: _*)
  }
}