package ketchup.app.controllers

import App
import ketchup.app.Model
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
import ketchup.app.components.ItemComponent
import java.net.URL
import java.util.ResourceBundle
import java.io.IOException

class MainController: Initializable {
    private lateinit var model: Model

    @FXML
    lateinit var sidebar: VBox

    @FXML
    private lateinit var title: Label

    @FXML
    private lateinit var displayAll: Button

    @FXML
    private lateinit var displayToday: Button

    @FXML
    private lateinit var displayUpcoming: Button

    @FXML
    private lateinit var addItemButton: Button

    @FXML
    private lateinit var filterButton: ComboBox<String>

    @FXML
    private lateinit var displayView: VBox

    override fun initialize(arg0:URL?, arg1: ResourceBundle? ) {
        model = Model(displayView.children)
        filterButton.items.addAll("Decreasing Priority", "Increase Priority")
        for (item in model.listOfTags) {
            updateSideBar(item)
        }
    }

    @FXML
    private fun onButtonClicked(e:ActionEvent) {
        val source = e.source as Node
        val id = source.id
        if (id == "addItemButton") {
            showDialog("addItemUI")
        } else if (id == "filterButton") {
            if (filterButton.value == "Decreasing Priority") {
                sortPriority(0)
            } else {
                sortPriority(1)
            }
        } else {
            showDialog("addItemUI")
        }
    }

    @FXML
    private fun showDialog(fxml: String) {
        try {
            val loader = FXMLLoader(App::class.java.getResource("fxml/"+ fxml + ".fxml"))
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
        val button = Button(tag)
        var filteredList: ObservableList<Node>
        button.setOnAction {
            title.text = button.text
            model.displayListByType(tag)
        }
        sidebar.children.add(button)
    }

    private fun sortPriority(type: Int) {
        var priority0 = FXCollections.observableArrayList<Node>()
        var priority1 = FXCollections.observableArrayList<Node>()
        var priority2 = FXCollections.observableArrayList<Node>()
        var priority3 = FXCollections.observableArrayList<Node>()
        var item : ItemComponent
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
        if ( type == 0 ) {  // Decreasing
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
}