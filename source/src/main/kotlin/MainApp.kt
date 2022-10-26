import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class MainApp: Application() {
    override fun start(stage: Stage?) {
        val pane = StackPane(Label("HelloWorld!"))
        val scene = Scene(pane, 250.0, 150.0)
        stage?.scene = scene
        stage?.title = "HelloWorld"
        stage?.show()
    }
}
