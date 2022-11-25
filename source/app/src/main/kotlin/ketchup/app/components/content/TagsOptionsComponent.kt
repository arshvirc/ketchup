package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node
import ketchup.app.components.ItemComponent
import ketchup.app.ktorclient.Client
import ketchup.console.TodoItem
import ketchup.console.TodoList
import kotlinx.coroutines.runBlocking
import org.controlsfx.control.CheckComboBox


class TagsOptionsComponent: CheckComboBox<String> {
    var model: Model
    private var toDoItemId: String
    private val api: Client

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 26.0
        this.prefWidth = 117.0
        this.model = m
        this.api = m.api
        this.toDoItemId = item.id.toString()
        this.items.addAll(m.listOfTags)

        for (tag in this.items) {
            if (item.tags.contains(tag)) {
                this.checkModel.check(tag)
                println("has the following tag $tag")
            }
        }

        this.checkModel.checkedItems.addListener(ListChangeListener<String?> { c ->
            val newValue : ObservableList<String> = this.checkModel.checkedItems
            println("Proceeding to Update Tags to be ${newValue.toString()}")
            val editedItem = editToDoItem(model.dbListOfAllItems, toDoItemId, newValue)
            updateEditedItem(toDoItemId, editedItem)
        })
    }
    private fun editToDoItem(list: TodoList, id: String, tags: ObservableList<String>): TodoItem {
        var item: TodoItem
        var mutableTags = mutableListOf<String>()
        for (item in tags) {
            mutableTags.add(item)
        }
        for (i in 0..model.dbListOfAllItems.list.lastIndex) {
            item = model.dbListOfAllItems.list[i]
            if (item.id == id.toInt()) {
                item.tags = mutableTags
                val editSuccess = runBlocking { api.editTodoItem(id.toInt(), item) }
                if(!editSuccess) {
                    println("Editing tags for item with ID $id failed")
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