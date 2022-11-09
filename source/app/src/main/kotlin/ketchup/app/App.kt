import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.fxml.FXMLLoader

fun main(args: Array<String>) {
    Application.launch(App::class.java)
}

class App(): Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(App::class.java.getResource("main_page.fxml"))
        //val pane = StackPane(Label("HelloWorld!"))
        val scene = Scene(fxmlLoader.load(), 700.0, 550.0)
        //val anchorPane =
        stage.scene = scene
        stage.title = "Ketchup version 0.2"
        stage.show()
    }
}
