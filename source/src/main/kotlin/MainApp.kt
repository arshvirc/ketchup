import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.fxml.FXMLLoader

class MainApp: Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(MainApp::class.java.getResource("main_page.fxml"))
        //val pane = StackPane(Label("HelloWorld!"))
        val scene = Scene(fxmlLoader.load(), 700.0, 550.0)
        //val anchorPane =
        stage.scene = scene
        stage.title = "Ketchup version 0.2"
        stage.show()
    }
}
