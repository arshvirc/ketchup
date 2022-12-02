package ketchup.app.controllers

import App
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import ketchup.app.Model
import ketchup.app.components.ContentComponent
import ketchup.app.components.ItemComponent
import kotlinx.coroutines.runBlocking
import ketchup.app.Window
import ketchup.app.WindowSize
import java.io.IOException
import java.net.URI
import java.net.URL
import java.util.*

class MainController : Initializable {
    private lateinit var model: Model

    @FXML lateinit var sidebar: VBox

    @FXML private lateinit var title: Label

    @FXML private lateinit var displayAll: Button

    @FXML private lateinit var displayToday: Button

    @FXML private lateinit var displayUpcoming: Button

    @FXML private lateinit var addItemButton: Button

    @FXML private lateinit var filterButton: ComboBox<String>

    @FXML private lateinit var sortButton: ComboBox<String>

    @FXML private lateinit var clearFilter: Button

    @FXML private lateinit var searchField: TextField

    @FXML private lateinit var displayView: VBox

    @FXML private lateinit var new_item: MenuItem

    @FXML private lateinit var undo: MenuItem

    @FXML private lateinit var redo: MenuItem

    @FXML private lateinit var quitButton: MenuItem

    @FXML private lateinit var defaultThemeButton : MenuItem

    //@FXML private lateinit var darkThemeButton : MenuItem

    @FXML private lateinit var frostThemeButton : MenuItem

    @FXML private lateinit var melonThemeButton : MenuItem

    @FXML private lateinit var bubblegumThemeButton : MenuItem

    @FXML private lateinit var pumpkinThemeButton : MenuItem

    @FXML private lateinit var lilacThemeButton : MenuItem

    @FXML private lateinit var lemonThemeButton : MenuItem

    @FXML private lateinit var about: MenuItem

    @FXML private lateinit var hide: MenuItem

    @FXML private lateinit var unique_container: BorderPane

    override fun initialize(arg0: URL?, arg1: ResourceBundle?) {
        model = Model(displayView.children, this)

        setTheme(model.getTheme())

        filterButton.items.addAll(
            "Filter",
            "No Priority",
            "Low Priority",
            "Medium Priority",
            "High Priority",
            "No Deadline",
            "With Deadline"
        )

        sortButton.items.addAll(
            "Sort",
            "Increasing Priority",
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
            if(!model.onEditableField) {
                model.undo()
            }
        }

        redo.setOnAction { actionEvent ->
            if(!model.onEditableField) {
                model.redo()
            }
        }

        quitButton.setOnAction { actionEvent ->
            val stage: Stage = title.scene.window as Stage
            stage.fireEvent(WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
        }

        hide.setOnAction { _ ->
            val stage: Stage = title.scene.window as Stage
            stage.isIconified = true
        }

        defaultThemeButton.setOnAction { actionEvent ->
            setTheme("default")
        }

//        darkThemeButton.setOnAction { actionEvent ->
//            setTheme("dark")
//        }

        frostThemeButton.setOnAction { actionEvent ->
            setTheme("frost")
        }

        melonThemeButton.setOnAction { actionEvent ->
            setTheme("melon")
        }

        bubblegumThemeButton.setOnAction { actionEvent ->
            setTheme("bubblegum")
        }

        pumpkinThemeButton.setOnAction { actionEvent ->
            setTheme("pumpkin")
        }

        lilacThemeButton.setOnAction { actionEvent ->
            setTheme("lilac")
        }

        lemonThemeButton.setOnAction { actionEvent ->
            setTheme("lemon")
        }

        searchField.textProperty().addListener{ e, o, n ->
            if ( n.isNotEmpty() && !model.isSearchModeOn ) {
                model.isSearchModeOn = true;    // filter on
            }

            if (n.isEmpty() && model.isSearchModeOn) {
                model.isSearchModeOn = false;   // no filter
            }
            model.searchText = n
            model.refreshDisplayedList(false)

        }


        clearFilter.setOnAction { _ ->
            model.refreshDisplayedList()
            filterButton.value = "Filter"
            sortButton.value = "Sort"
            filterButton.promptText = "Filter"
            sortButton.promptText = "Sort"
            searchField.text = ""
        }


        about.setOnAction {
            java.awt.Desktop.getDesktop().browse(URI("https://git.uwaterloo.ca/a23dhing/cs346-project/-/wikis/home"));
        }
    }

    @FXML
    private fun setTheme(name : String) {
        model.setTheme(name)
        val url = "css/${name}/main.css"
        unique_container.stylesheets.clear()
        unique_container.stylesheets.add(url)
//        val scene : Scene = title.scene
//        scene.stylesheets.clear()
//        scene.stylesheets.add(url)
    }

    @FXML
    private fun sideBarButton(e: ActionEvent) {
        val source = e.source as Button
        title.text = source.text
        searchField.text = ""
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
                    "No Priority" -> filterPriority(0)
                    "Low Priority" -> filterPriority(1)
                    "Medium Priority" -> filterPriority(2)
                    "High Priority" -> filterPriority(3)
                    "No Deadline" -> filterDeadline(false)
                    "With Deadline" -> filterDeadline(true)
                    else -> model.refreshDisplayedList()
                }
            }
            "sortButton" -> {
                when(sortButton.value) {
                    "Increasing Priority" -> sortPriority(true)
                    "Decreasing Priority" -> sortPriority(false)
                    "Due Earliest" -> sortDeadline(true)
                    "Due Latest" -> sortDeadline(false)
                    else -> model.refreshDisplayedList()
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
            var window: Window = Window()
            var windowSize : WindowSize = window.getWindowSize()
            inputStage.x = windowSize.x + (windowSize.w - 540)/2
            inputStage.y = windowSize.y + (windowSize.h - 273)/2
            inputStage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun updateSideBar(tag: String) {
        val bar = ButtonBar()
        val tagButton = Button(tag)
        tagButton.font = Font(14.0)
        tagButton.padding = Insets(0.0)
        tagButton.alignment = Pos.CENTER_LEFT
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
                uiItem.content = ContentComponent(uiItem.item, model, false)
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
            println("SORTING BY INCREASING PRIORITY")
            itemComponents.sortedBy { it.item.priority }
        } else {
            println("SORTING BY DECREASING PRIORITY")
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

    private fun filterPriority(priority: Int) {
        model.refreshDisplayedList()
        val itemComponents = model.displayList.map{ it as ItemComponent }
        val filtered = itemComponents.filter { it.item.priority == priority }

        model.displayList.clear()

        model.displayList.addAll(filtered)
    }

    private fun filterDeadline(deadline: Boolean) {
        model.refreshDisplayedList()
        val itemComponents = model.displayList.map{ it as ItemComponent}
        var filtered: List<ItemComponent>

        if(deadline) {
            filtered = itemComponents.filter { it.item.deadline != null }
        } else {
            filtered = itemComponents.filter { it.item.deadline == null}
        }

        model.displayList.clear()
        model.displayList.addAll(filtered)
    }
}
