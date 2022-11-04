import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import java.time.LocalDate

class GuiController {
    @FXML
    private lateinit var addTask : Button

    @FXML
    private lateinit var datePicker : DatePicker

    @FXML
    private lateinit var titleText : TextField

    @FXML
    private lateinit var taskList : ListView<AddEvent>

    @FXML
    private fun refresh() {
        datePicker.value = LocalDate.now()
        titleText.text = null
    }

    @FXML
    private fun addEvent() {

        //val anEventDate = datePicker.value //datePicker.setValue(datePicker.value)
        //val formattedDate = anEventDate.format(DateTimeFormatter. ofPattern("dd-MMM-yy"))
        val anEventTitle = titleText.text
        val anAddedEvent = AddEvent(anEventTitle)
        //val anAddedEvent = AddEvent(datePicker.getValue(), titleText.getText())
        taskList.items.add(anAddedEvent)
        refresh()
    }
}