package ketchup.app.controllers

import App
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import ketchup.app.Model
import ketchup.app.components.ContentComponent
import ketchup.app.components.ItemComponent
import kotlinx.coroutines.runBlocking

class MainController : Initializable {
    private lateinit var model: Model

    @FXML lateinit var sidebar: VBox

    @FXML private lateinit var title: Label

    @FXML private lateinit var displayAll: Button

    @FXML private lateinit var displayToday: Button

    @FXML private lateinit var displayUpcoming: Button

    @FXML private lateinit var addItemButton: Button

    @FXML private lateinit var filterButton: ComboBox<String>

    @FXML private lateinit var displayView: VBox

    override fun initialize(arg0: URL?, arg1: ResourceBundle?) {
        model = Model(displayView.children, this)
        filterButton.items.addAll(
                "Decreasing Priority",
                "Increase Priority",
                "Due Earliest",
                "Due Latest"
        )
        for (tag in model.listOfTags) {
            updateSideBar(tag)
        }
    }

    @FXML
    private fun onButtonClicked(e: ActionEvent) {
        val source = e.source as Node
        val id = source.id
        if (id == "addItemButton") {
            showDialog("addItemUI")
        } else if (id == "filterButton") {
            if (filterButton.value == "Decreasing Priority") {
                sortPriority(0)
            } else if (filterButton.value == "Increase Priority") {
                sortPriority(1)
            } else if (filterButton.value == "Due Earliest") {
                sortDeadline(true)
            } else if (filterButton.value == "Due Latest") {
                sortDeadline(false)
            } else {
                // what happens here?
                println("Idk what to do here lolz")
            }
        } else {
            showDialog("addItemUI")
        }
    }

    @FXML
    private fun showDialog(fxml: String) {
        try {
            val loader = FXMLLoader(App::class.java.getResource("fxml/" + fxml + ".fxml"))
            val inputStage = Stage()
            val scene = Scene(loader.load())
            loader.getController<AddController>().setModel(model, this)
            inputStage.initOwner(addItemButton.scene.window)
            inputStage.scene = scene
            inputStage.initStyle(StageStyle.UNDECORATED)
            inputStage.isResizable = false
            inputStage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun updateSideBar(tag: String) {
        val bar = ButtonBar()
        val tagButton = Button(tag)
        tagButton.setOnAction {
            title.text = tagButton.text
            model.displayListByType(tag)
        }
        val delButton = Button("X")
        delButton.setOnAction {
            model.listOfTags.remove(tagButton.text)
            sidebar.children.remove(bar)
            // TODO ADD API CALL
            val apiTag = runBlocking { model.api.deleteTag(tagButton.text) }
            for ( item in model.uiListOfAllItems) {
                val uiItem = item as ItemComponent
                uiItem.content = ContentComponent(uiItem.item, model)
            }
        }
        bar.buttons.addAll(tagButton, delButton)
        sidebar.children.add(bar)
    }

    private fun sortPriority(type: Int) {
        var priority0 = FXCollections.observableArrayList<Node>()
        var priority1 = FXCollections.observableArrayList<Node>()
        var priority2 = FXCollections.observableArrayList<Node>()
        var priority3 = FXCollections.observableArrayList<Node>()
        var item: ItemComponent
        for (i in 0..model.displayList.lastIndex) {
            item = model.displayList[i] as ItemComponent
            when (item.item.priority) {
                0 -> priority0.add(item)
                1 -> priority1.add(item)
                2 -> priority2.add(item)
                3 -> priority3.add(item)
            }
        }
        model.displayList.removeAll(priority0)
        model.displayList.removeAll(priority1)
        model.displayList.removeAll(priority2)
        model.displayList.removeAll(priority3)
        if (type == 0) { // Decreasing
            println("SORTING BY INCREASING")
            model.displayList.addAll(priority0)
            model.displayList.addAll(priority1)
            model.displayList.addAll(priority2)
            model.displayList.addAll(priority3)
        } else {
            println("SORTING BY DECREASING")
            model.displayList.addAll(priority3)
            model.displayList.addAll(priority2)
            model.displayList.addAll(priority1)
            model.displayList.addAll(priority0)
        }
    }

    private fun sortDeadline(increasing: Boolean) {
        println("SORTING BY DEADLINE")
        val itemComponents = model.uiListOfAllItems.map { it as ItemComponent }
        val nullItems = itemComponents.filter { it.item.deadline == null }
        var notNullItems = itemComponents.filter { it.item.deadline != null }

        if (increasing) {
            notNullItems = notNullItems.sortedBy { it.item.deadline }
        } else {
            notNullItems = notNullItems.sortedByDescending { it.item.deadline }
        }

        model.uiListOfAllItems.clear()

        model.uiListOfAllItems.addAll(notNullItems)
        model.uiListOfAllItems.addAll(nullItems)
    }
}
