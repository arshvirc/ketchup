package ketchup.app.controllers

import App
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle
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
import javafx.stage.WindowEvent
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

    @FXML private lateinit var new_item: MenuItem

    @FXML private lateinit var undo: MenuItem

    @FXML private lateinit var redo: MenuItem

    @FXML private lateinit var quitButton: MenuItem

    override fun initialize(arg0: URL?, arg1: ResourceBundle?) {
        model = Model(displayView.children, this)
        filterButton.items.addAll(
                "Increase Priority",
                "Decreasing Priority",
                "Due Earliest",
                "Due Latest"
        )
        for (tag in model.listOfTags) {
            updateSideBar(tag)
        }

        new_item.setOnAction { actionEvent ->
            showDialog("addItemUI")
        }

        undo.setOnAction { actionEvent ->
            model.undo()
        }

        redo.setOnAction { actionEvent ->
            model.redo()
        }

        quitButton.setOnAction { actionEvent ->
            val stage: Stage = title.scene.window as Stage
            stage.fireEvent(WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
        }
    }


    @FXML
    private fun sideBarButton(e: ActionEvent) {
        val source = e.source as Button
        title.text = source.text
        model.displayState = source.text.trim()
        model.refreshDisplayedList()
    }

    @FXML
    private fun onButtonClicked(e: ActionEvent) {
        val source = e.source as Node
        when (source.id) {
            "addItemButton" -> showDialog("addItemUI")
            "filterButton" -> {
                when (filterButton.value) {
                    "Increase Priority" -> sortPriority(true)
                    "Decreasing Priority" -> sortPriority(false)
                    "Due Earliest" -> sortDeadline(true)
                    "Due Latest" -> sortDeadline(false)
                    else -> return error("FilterButton has some other function")
                }
            }
            else -> showDialog("addItemUI")
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
            model.displayState = tagButton.text
            model.refreshDisplayedList()
        }
        val delButton = Button("X")
        delButton.setOnAction {
            model.listOfTags.remove(tagButton.text)
            sidebar.children.remove(bar)
            val apiTag = runBlocking { model.api.deleteTag(tagButton.text) }
            for ( item in model.uiListOfAllItems) {
                val uiItem = item as ItemComponent
                uiItem.content = ContentComponent(uiItem.item, model)
            }
            title.text = "All Tasks"
            model.displayState = "All Tasks"
            model.refreshDisplayedList()
        }
        bar.buttons.addAll(tagButton, delButton)
        sidebar.children.add(bar)
    }

    private fun sortPriority(increasing: Boolean) {
        var itemComponents = model.displayList.map { it as ItemComponent }

        itemComponents = if (increasing) {
            println("SORTING BY INCREASING DEADLINE")
            itemComponents.sortedBy { it.item.priority }
        } else {
            println("SORTING BY DECREASING DEADLINE")
            itemComponents.sortedByDescending { it.item.priority }
        }
        model.displayList.clear()
        model.displayList.addAll(itemComponents)
    }

    private fun sortDeadline(increasing: Boolean) {
        println("SORTING BY DEADLINE")
        val itemComponents = model.displayList.map { it as ItemComponent }
        val nullItems = itemComponents.filter { it.item.deadline == null }
        var notNullItems = itemComponents.filter { it.item.deadline != null }

        notNullItems = if (increasing) { notNullItems.sortedBy { it.item.deadline }
        } else {
            notNullItems.sortedByDescending { it.item.deadline }
        }

        model.displayList.clear()

        model.displayList.addAll(notNullItems)
        model.displayList.addAll(nullItems)
    }
}
