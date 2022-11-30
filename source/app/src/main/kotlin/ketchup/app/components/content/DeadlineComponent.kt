package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ketchup.app.Action
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class DeadlineComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : DatePicker
    var model: Model
    private val api: Client

    constructor(item: TodoItem, m: Model, archive: Boolean) {
        // Styling
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.padding = javafx.geometry.Insets(0.0,0.0,0.0,10.0)
        this.spacing = 10.0
        // Initializing
        this.model = m
        this.api = m.api
        this.toDoItemId = item.id.toString()

        // Components
        this.options = DatePicker()
        this.options.prefHeight = 26.0
        this.options.prefWidth = 120.0
        this.label = LabelComponent("Deadline: ")
        if(item.deadline != null) {
            val date = item.deadline;
            val instant = date?.toInstant()
            val local = instant?.atZone(ZoneId.systemDefault())?.toLocalDate();
            this.options.value = local
        }

        if(archive) {
            this.options.isDisable = true;
        }

        this.children.add(label)
        this.children.add(options)
        this.options.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Deadline to be ${this.options.value}")
                    model.editToDoItem(toDoItemId, Action.EDIT_DEADLINE, this.options.value)
                }
            }
        }
    }
}