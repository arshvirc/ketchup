import java.time.LocalDate

class AddEvent {

    private lateinit var title : String
    private lateinit var dueDate : LocalDate

    fun getTitle() : String {
        return title
    }

    fun setTitle(newTitle : String) {
        this.title = newTitle
    }

    fun getDueDate() : LocalDate {
        return dueDate
    }

    fun setDueDate(newDueDate : LocalDate) {
        this.dueDate = newDueDate
    }

    constructor(title: String, date: LocalDate) {
        this.setTitle(title)
        this.setDueDate(date)
    }
    //@Override
    override fun toString() : String {
        return "At: " + this.getDueDate() + " " + this.getTitle()
    }
}