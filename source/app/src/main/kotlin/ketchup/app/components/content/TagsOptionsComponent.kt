package ketchup.app.components.content

import ketchup.app.Model
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Node
import ketchup.app.Action
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
            println("${item.tags} hello")
            if (item.tags.contains(tag)) {
                this.checkModel.check(tag)
                println("has the following tag $tag")
            }
        }

        this.checkModel.checkedItems.addListener(ListChangeListener<String?> { c ->
            val newValue : ObservableList<String> = this.checkModel.checkedItems
            println("Proceeding to Update Tags to be ${newValue.toString()}")
            model.editToDoItem(toDoItemId, Action.EDIT_TAGS, newValue)
        })
    }
}