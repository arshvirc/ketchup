import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import ketchup.app.Window
import ketchup.app.WindowSize


/*
To run the server locally, go into service module and run Main.kt
Then, the service will be running at http://127.0.0.1:8080/
Once this happens, you may start using the client.
Whenever you use the client you may see a prompt that asks you to use the 'suspend' keyword.
Click accept. API requests are asynchronous, so we need this suspend keyword to have normal functionality.
 */

class App : Application() {

    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader(App::class.java.getResource("fxml/main.fxml"))
        val scene = Scene(fxmlLoader.load())
        var window: Window = Window()
        primaryStage.scene = scene
        primaryStage.minHeight = 550.0
        primaryStage.minWidth = 900.0
        val image = Image("images/ketchup_bottle.png")
        var windowSize : WindowSize = window.getWindowSize()
        primaryStage.height = windowSize.h
        primaryStage.width = windowSize.w
        primaryStage.x = windowSize.x
        primaryStage.y = windowSize.y
        primaryStage.isFullScreen = windowSize.max == 1.0
        primaryStage.title = "Ketchup 0.4"
        primaryStage.icons.add(image)
        primaryStage.show()

        primaryStage.xProperty().addListener{ e, o, n ->
            if ( o != n ) {
                window.saveWindowSize(primaryStage.height, primaryStage.width, primaryStage.x, primaryStage.y, windowSize.max)
            }
        }

        primaryStage.yProperty().addListener{ e, o, n ->
            if ( o != n ) {
                window.saveWindowSize(primaryStage.height, primaryStage.width, primaryStage.x, primaryStage.y, windowSize.max)
            }

        }

        primaryStage.heightProperty().addListener{ e, o, n ->
            if ( o != n ) {
                window.saveWindowSize(primaryStage.height, primaryStage.width, primaryStage.x, primaryStage.y, windowSize.max)
            }

        }

        primaryStage.widthProperty().addListener{ e, o, n ->
            if ( o != n ) {
                window.saveWindowSize(primaryStage.height, primaryStage.width, primaryStage.x, primaryStage.y, windowSize.max)
            }

        }

        primaryStage.setOnCloseRequest {
            if (primaryStage.isFullScreen) {
                windowSize.max = 1.0
            } else {
                windowSize.max = 0.0
            }
            window.saveWindowSize(primaryStage.height, primaryStage.width, primaryStage.x, primaryStage.y, windowSize.max)
        }
    }
}
