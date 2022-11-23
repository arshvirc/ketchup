import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ketchup.app.ktorclient.Client


/*
To run the server locally, go into service module and run Main.kt
Then, the service will be running at http://127.0.0.1:8080/
Once this happens, you may start using the client.
Whenever you use the client you may see a prompt that asks you to use the 'suspend' keyword.
Click accept. API requests are asynchronous so we need this suspend keyword to have normal functionality.
 */

class App(): Application() {
    override fun start(primaryStage: Stage) {
        val menu: MenuBar = MenuBar()
        val file: Menu = Menu("File")
        val edit: Menu = Menu("Edit")

        val newItem = MenuItem("New Item")

        val undo = MenuItem("Undo")
        val redo = MenuItem("redo")

        file.items.addAll(newItem)
        edit.items.addAll(undo, redo)

        menu.menus.addAll(file, edit)
        menu.isUseSystemMenuBar = true

        val fxmlLoader = FXMLLoader(App::class.java.getResource("fxml/main.fxml"))

        val vbox = VBox(menu, fxmlLoader.load());

        val scene = Scene(vbox, 900.0, 550.0)
        primaryStage.scene = scene
        primaryStage.minHeight = 550.0
        primaryStage.minWidth = 900.0
        primaryStage.title = "Ketchup 0.2"
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java)
}
