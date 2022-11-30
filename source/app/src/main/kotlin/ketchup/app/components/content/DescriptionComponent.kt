package ketchup.app.components.content
import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.TextField
import ketchup.app.Action
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking

class DescriptionComponent: TextField {
    private var toDoItemId : String
    private var model: Model
    private val api: Client

    constructor(item: TodoItem, m : Model) {
        this.prefHeight = 20.0
        this.prefWidth = 331.0
        this.padding = Insets(5.0)
        this.style += "-fx-padding:10;" + "-fx-border-insets: 5;" + "-fx-background-insets: 5px;"
        if (item.description == "") {
            this.promptText = "Enter Details"
        } else {
            this.text = item.description
        }

        this.toDoItemId = item.id.toString()
        this.model = m
        this.api = m.api
        this.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Description to be ${this.text}")
                    model.editToDoItem(toDoItemId, Action.EDIT_DESC, this.text)
                    model.onEditableField = false;
                } else {
                    model.onEditableField = true;
                }
            }
        }
    }
}