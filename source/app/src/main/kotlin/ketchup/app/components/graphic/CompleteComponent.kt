package ketchup.app.components.graphic

import javafx.collections.FXCollections
import javafx.geometry.Bounds
import ketchup.app.Model
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import ketchup.app.Action
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking

class CompleteComponent: Button {
    var model: Model
    var imageView = ImageView()
    var image: Image

    constructor(item: TodoItem, m: Model) {
        this.alignment = Pos.CENTER
        this.maxHeight = 25.0
        this.maxHeight = 25.0
        this.minHeight = 25.0
        this.minWidth = 25.0
        this.isMnemonicParsing = false
        this.prefHeight = 25.0
        this.prefWidth = 25.0
        this.model = m

        image = if (item.completion) {
            Image("images/completed.png")
        } else {
            Image("images/progress.png")
        }

        imageView.fitHeight = 15.0
        imageView.fitWidth = 40.0
        imageView.isPickOnBounds = true
        imageView.isPreserveRatio = true
        imageView.image = image
        this.graphic = imageView

        this.setOnAction {
            run {
                if (item.completion) {
                    // Setting completion to "false"
                    println("Proceeding to Update ${item.id} as uncompleted")
                    model.editToDoItem(item.id.toString(), Action.EDIT_COMPLETE, false)

                } else {
                    // Setting completion to "true"
                    println("Proceeding to Update ${item.id} as completed")
                    model.editToDoItem(item.id.toString(), Action.EDIT_COMPLETE, true)
                }
            }
        }
    }
}
