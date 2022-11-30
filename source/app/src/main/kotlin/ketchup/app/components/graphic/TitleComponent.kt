package ketchup.app.components.graphic
import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.TextField
import ketchup.app.Action
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking

class TitleComponent: TextField {
    private var toDoItemId : String
    private var model: Model
    private var api: Client
    constructor(item: TodoItem, model: Model, archive: Boolean) {
        this.maxHeight = 30.0
        this.maxWidth = 1.7976931348623157E308
        this.minHeight = 30.0
        this.minWidth = Double.NEGATIVE_INFINITY
        this.prefHeight = 30.0
        this.prefWidth = 102.0

        this.text = item.title
        toDoItemId = item.id.toString()
        this.model = model
        this.api = model.api

        if(archive) {
            this.isDisable = true;
        }

        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Title Field to have the following title: ${this.text}")
                    model.editToDoItem(toDoItemId, Action.EDIT_TITLE, this.text)
                }
            }
        }
    }
}