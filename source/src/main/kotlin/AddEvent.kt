import java.time.LocalDate
import java.util.Date

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

//    constructor(dueDate: LocalDate, title: String) {
//        this.setTitle(title)
//        this.setDueDate(dueDate)
//    }
constructor(title: String) {
    this.setTitle(title)
}
     //@Override
     override fun toString() : String {
         return this.getTitle()
         //return "At: " this.getDueDate() + this.getTitle()
     }




}