import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class App(): Application() {
    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader(App::class.java.getResource("fxml/main.fxml"))
        val scene = Scene(fxmlLoader.load(), 700.0, 550.0)
        primaryStage.scene = scene
        primaryStage.minHeight = 550.0
        primaryStage.minWidth = 700.0
        primaryStage.title = "Ketchup 0.2"
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java)
}
