package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.HBox
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

    constructor(item: TodoItem, m: Model) {
        // Styling
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.padding = javafx.geometry.Insets(0.0,0.0,0.0,10.0)

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

        this.children.add(label)
        this.children.add(options)
        this.options.focusedProperty().addListener{ _, _, new ->
            run {
                if (!new) {
                    println("Proceeding to Update Deadline to be ${this.options.value}")
                    val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, this.options.value)
                    updateEditedItem(toDoItemId, editedItem)

                }
            }
        }
    }

    private fun editToDoItem(list: TodoList, id: String, deadline: LocalDate?): TodoItem {
        var item: TodoItem
        for (i in 0..model.dbListOfAllItems.list.lastIndex) {
            item = model.dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                if(deadline != null) {
                    val instant = Instant.from(deadline.atStartOfDay(ZoneId.systemDefault()))
                    val date = Date.from(instant)
                    item.deadline = date   /* Update this to include  */
                    val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
                } else {
                    item.deadline = null;
                    val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item)};
                }

                return item
            }
        }
        item = TodoItem()
        return item
    }

    private fun updateEditedItem( completedID: String, dbItem: TodoItem) {
        val newItem = ItemComponent(dbItem, model)
        var newList = FXCollections.observableArrayList<Node>()
        newList.addAll(newItem)
        var FOUND = false
        var beforeList = FXCollections.observableArrayList<Node>()
        var oldList = FXCollections.observableArrayList<Node>()
        var afterList = FXCollections.observableArrayList<Node>()
        for (item in model.uiListOfAllItems) {
            if ( item.id == completedID ) {
                oldList.add(item)
                FOUND = true
            } else if ( FOUND ) {
                afterList.add(item)
            } else {
                beforeList.add(item)
            }
        }
        model.uiListOfAllItems.removeAll(oldList)
        model.uiListOfAllItems.removeAll(afterList)
        model.uiListOfAllItems.addAll(newList)
        model.uiListOfAllItems.addAll(afterList)
    }
}