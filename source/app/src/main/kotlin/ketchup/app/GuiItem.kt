import java.time.LocalDate

//@Serializable
class GuiItem() {
    lateinit var title : String
    lateinit var detail : String
    lateinit var tags : String
    var deadline : LocalDate? = null
    var priority : Int = 0
    var timestamp : LocalDate = LocalDate.now()

    constructor(title: String, detail: String = "", tags: String = "", deadline: LocalDate? = null, priority: String = "0"): this() {
        this.title = title
        this.detail = detail
        this.tags = tags
        this.deadline = deadline
        this.priority = priority.toInt()
    }

}