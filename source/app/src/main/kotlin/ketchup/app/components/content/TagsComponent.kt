package ketchup.app.components.content

import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.control.Button
import ketchup.app.Model
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import ketchup.app.components.ContentComponent
import ketchup.app.components.ItemComponent
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking
import org.controlsfx.control.CheckComboBox

class TagsComponent: HBox {
    var toDoItemId : String
    var label : Label
    var options : CheckComboBox<String>
    var model : Model

    constructor(item: TodoItem, m: Model, archive: Boolean) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.spacing = 10.0
        this.padding = Insets(0.0,0.0,0.0,10.0)

        this.model = m
        this.label = LabelComponent("Tags: ")
        this.options = TagsOptionsComponent(item, m)
        this.toDoItemId = item.id.toString()

        var addTag = Button("Add Tag")
        addTag.setOnAction { e-> newTagOptions(e)}

        this.children.add(label)
        this.children.add(options)

        if(!archive) {
            this.children.add(addTag)
        }
    }
    private fun newTagOptions(event: ActionEvent) {
        val source = event.source as Button
        val id = source.id
        when (source.text) {
            "Add Tag" -> {
                this.children.remove(1,3)
                val field = TextField()
                this.children.add(field)
                val check = Button("Confirm")
                check.setOnAction { e-> newTagOptions(e) }
                val cancel = Button("Cancel")
                cancel.setOnAction { e-> newTagOptions(e) }
                this.children.addAll(check, cancel)
            }
            "Confirm" -> {
                val text = this.children[1] as TextField
                val newTag = text.text
                // MODEL
                updateAllTags(newTag)
                val apiTag = runBlocking { model.api.createNewTag(newTag) }
                this.children.remove(1,4)                   // keep label only
                options.items.add(newTag)                   // update options
                options.checkModel.check(newTag)
                this.children.add(options)                  // add checkcombobox

                val add = Button("Add Tag")           // add add box
                add.setOnAction { e-> newTagOptions(e) }
                this.children.addAll(add)
            }
            "Cancel" -> {
                this.children.remove(1,4)
                this.children.add(options)

                val add = Button("Add Tag")
                add.setOnAction { e-> newTagOptions(e) }
                this.children.addAll(add)
            }
        }
    }
    private fun updateAllTags(tag: String) {
        model.listOfTags.add(tag)
        for ( item in model.uiListOfAllItems) {
            val uiItem = item as ItemComponent
            uiItem.content = ContentComponent(uiItem.item, model, false)
        }
        model.previousController.updateSideBar(tag)
        model.refreshDisplayedList()
    }
}