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
    private lateinit var dueDatePicker : DatePicker

    @FXML
    private lateinit var titleText : TextField

    @FXML
    private lateinit var taskList : ListView<AddEvent>

    @FXML
    private fun refresh() {
        dueDatePicker.value = LocalDate.now()
        titleText.text = null
    }

    @FXML
    private fun addEvent() {
        val eventDate = dueDatePicker.value
        print(eventDate)
        val eventTitle = titleText.text
        val anAddedEvent = AddEvent(eventTitle, eventDate)
        taskList.items.add(anAddedEvent)
        refresh()
    }
}