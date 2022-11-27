package ketchup.app.components.content

import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.control.Button
import ketchup.app.Model
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.stage.Stage
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

    constructor(item: TodoItem, m: Model) {
        this.prefHeight = 100.0
        this.prefWidth = 200.0
        this.model = m
        this.label = LabelComponent("Tags: ")
        this.padding = javafx.geometry.Insets(0.0,0.0,0.0,10.0)
        this.options = TagsOptionsComponent(item, m)
        this.options.checkModelProperty().addListener{ e, o, n ->
            println("checked something off")
        }

        this.spacing = 10.0

        toDoItemId = item.id.toString()
        this.children.add(label)
        this.children.add(options)

        var addTag = Button("Add Tag")
        addTag.setOnAction { e-> newTagOptions(e)}
        this.children.add(addTag)
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
                model.listOfTags.add(newTag)
                val apiTag = runBlocking { model.api.createNewTag(newTag) }
                this.children.remove(1,4)
                options.items.add(newTag)
                options.checkModel.check(newTag)
                this.children.add(options)
                updateAllTags(newTag)

                val add = Button("Add Tag")
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
        for ( item in model.uiListOfAllItems) {
            val uiItem = item as ItemComponent
            uiItem.content = ContentComponent(uiItem.item, model)
        }
        model.previousController.updateSideBar(tag)
        model.displayListByType(model.displayState)
    }
}