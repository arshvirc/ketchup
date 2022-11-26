package ketchup.app.controllers

import ketchup.app.Model
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.stage.Stage
import ketchup.app.components.ContentComponent
import ketchup.app.components.ItemComponent
import ketchup.console.TodoItem
import kotlinx.coroutines.runBlocking
import org.controlsfx.control.CheckComboBox
import java.time.Instant
import java.time.ZoneId
import java.util.*

class AddController {
    private lateinit var model: Model
    private lateinit var previousController: MainController

    @FXML
    private lateinit var inputTitle: TextArea

    @FXML
    private lateinit var inputDesc: TextArea

    @FXML
    private lateinit var inputTags: CheckComboBox<String>

    @FXML
    private lateinit var tagContainer: HBox

    @FXML
    private lateinit var addTag: Button

    @FXML
    private lateinit var inputDeadline: DatePicker

    @FXML
    private lateinit var inputPriority: ComboBox<String>

    @FXML
    private lateinit var cancelButton: Button

    @FXML
    private lateinit var createButton: Button

    fun setModel(m: Model, c: MainController) {
        this.model = m
        this.previousController = c
        inputTags.items.addAll(m.listOfTags)
        inputPriority.items.addAll(m.listOfPriorities)

    }

    @FXML
    private fun newTagOptions(event: ActionEvent) {
        val source = event.source as Button
        val id = source.id
        when (source.text) {
            "Add Tag" -> {
                tagContainer.children.remove(1,3)
                val field = TextField()
                tagContainer.children.add(field)
                val check = Button("Confirm")
                check.setOnAction { e-> newTagOptions(e) }
                val cancel = Button("Cancel")
                cancel.setOnAction { e-> newTagOptions(e) }
                tagContainer.children.addAll(check, cancel)
            }
            "Confirm" -> {
                val text = tagContainer.children[1] as TextField
                val newTag = text.text
                model.listOfTags.add(newTag)
                val apiTag = runBlocking { model.api.createNewTag(newTag) }
                tagContainer.children.remove(1,4)
                inputTags.items.add(newTag)
                inputTags.checkModel.check(newTag)
                tagContainer.children.add(inputTags)
                updateAllTags(newTag)

                val add = Button("Add Tag")
                add.setOnAction { e-> newTagOptions(e) }
                tagContainer.children.addAll(add)
            }
            "Cancel" -> {
                tagContainer.children.remove(1,4)
                tagContainer.children.add(inputTags)

                val add = Button("Add Tag")
                add.setOnAction { e-> newTagOptions(e) }
                tagContainer.children.addAll(add)
            }
        }
    }
    @FXML
    private fun updateAllTags(tag: String) {
        for ( item in model.uiListOfAllItems) {
            val uiItem = item as ItemComponent
            uiItem.content = ContentComponent(uiItem.item, model)
        }
        previousController.updateSideBar(tag)
        model.displayListByType(model.displayState)
    }

    @FXML
    private fun onButtonClicked(event: ActionEvent) {
        val source = event.source as Node
        val id = source.id
        if (id == "createButton") {

            if (inputTitle.text == null || inputTitle.text == "" || inputTitle.text == " " ) {
                println("You must have a title!")
                return
            }
            var date: Date? = null;

            if(inputDeadline.value != null) {
                val local = inputDeadline.value
                val instant = Instant.from(local.atStartOfDay(ZoneId.systemDefault()))
                date = Date.from(instant)
            }

            var tagsList = mutableListOf<String>()
            val observableTags : ObservableList<String> = inputTags.checkModel.checkedItems

            println(observableTags)

            for (item in observableTags) tagsList.add(item)
            if (inputDesc.text == null || inputDesc.text == "") inputDesc.text = " ";
            if (inputPriority.value == null) { inputPriority.value = "0"}

            // TODO: Convert deadline from LocalDate to Date

            val item = TodoItem(
                title = inputTitle.text,
                description = inputDesc.text,
                priority = convertPriorityToNum(inputPriority.value),
                completion = false,
                timestamp = Date(System.currentTimeMillis()),
                deadline = date,
                tags = tagsList
            )

            model.addItemToList(item)
        }
        var stage = createButton.scene.window as Stage
        stage.close()
    }

    private fun convertPriorityToNum(priority: String): Int {
        return when(priority) {
            "None" -> 0
            "Low" -> 1
            "Medium" -> 2
            "High" -> 3
            else -> 0
        }
    }
}