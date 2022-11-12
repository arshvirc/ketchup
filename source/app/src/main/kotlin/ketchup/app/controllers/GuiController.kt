import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.ResourceBundle
import java.io.IOException

class GuiController: Initializable {

    private lateinit var model: Model

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
    private lateinit var filterButton: Button

    @FXML
    private lateinit var searchBarField: TextArea

    @FXML
    private lateinit var displayView: VBox

    override fun initialize(arg0:URL?, arg1: ResourceBundle? ) {
        model = Model(displayView.children)
        //updateDisplayView(model.getList())
    }

    @FXML
    private fun updateDisplayView( list: ObservableList<Node>) {
        for ( item in list ) {
            displayView.children.add(item)
        }
    }

    @FXML
    private fun onButtonClicked(e:ActionEvent) {
        val source = e.source as Node
        val id = source.id
        if (id == "addItemButton") {
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




}