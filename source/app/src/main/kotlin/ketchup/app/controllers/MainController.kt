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
    private lateinit var title: TextField

    @FXML
    private lateinit var allTasksListButton: Button

    @FXML
    private lateinit var upcomingListButton: Button

    @FXML
    private lateinit var list1Button: Button

    @FXML
    private lateinit var list2Button: Button

    @FXML
    private lateinit var addItemButton: Button

    @FXML
    private lateinit var filterButton: ComboBox<String>

    @FXML
    private lateinit var searchBarField: TextArea

    @FXML
    private lateinit var displayView: VBox

    override fun initialize(arg0:URL?, arg1: ResourceBundle? ) {
        model = Model(displayView.children)
        filterButton.items.addAll("Decreasing Priority", "Increase Priority")
    }

    @FXML
    private fun updateDisplayView( list: ObservableList<Node>) {
        for ( item in list ) {
            displayView.children.removeAll()
            displayView.children.addAll(model.uiListOfAllItems)
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
            loader.getController<AddController>().setModel(model)
            inputStage.initOwner(addItemButton.scene.window)
            inputStage.scene = scene
            inputStage.initStyle(StageStyle.UNDECORATED)
            inputStage.isResizable = false
            inputStage.show()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sortPriority(type: Int) {
        println("SORTING BY INCREASING")
        var priority0 = FXCollections.observableArrayList<Node>()
        var priority1 = FXCollections.observableArrayList<Node>()
        var priority2 = FXCollections.observableArrayList<Node>()
        var priority3 = FXCollections.observableArrayList<Node>()
        var item : ItemComponent
        for (i in 0..model.uiListOfAllItems.lastIndex) {
            item = model.uiListOfAllItems[i] as ItemComponent
            when (item.item.priority) {
                0 -> priority0.add(item)
                1 -> priority1.add(item)
                2 -> priority2.add(item)
                3 -> priority3.add(item)
            }
        }
        if ( type == 0 ) {  // Decreasing
            model.uiListOfAllItems.removeAll(priority0)
            model.uiListOfAllItems.removeAll(priority1)
            model.uiListOfAllItems.removeAll(priority2)
            model.uiListOfAllItems.removeAll(priority3)
            model.uiListOfAllItems.addAll(priority0)
            model.uiListOfAllItems.addAll(priority1)
            model.uiListOfAllItems.addAll(priority2)
            model.uiListOfAllItems.addAll(priority3)
        } else {
            model.uiListOfAllItems.removeAll(priority0)
            model.uiListOfAllItems.removeAll(priority1)
            model.uiListOfAllItems.removeAll(priority2)
            model.uiListOfAllItems.removeAll(priority3)
            model.uiListOfAllItems.addAll(priority3)
            model.uiListOfAllItems.addAll(priority2)
            model.uiListOfAllItems.addAll(priority1)
            model.uiListOfAllItems.addAll(priority0)
        }
    }
}