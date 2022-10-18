import java.util.Date

class TodoItem() {
    var id : Int = 0
        private set
    var title : String = ""
        private set
    var description : String = ""
        private set
    val timestamp : Date = Date(System.currentTimeMillis())
    var deadline : Date? = null
        private set
    var priority : Int = 0
        private set
    var tags = mutableSetOf<String>()
        private set
    var completion : Boolean = false
        private set

    constructor(title :  String = "", description : String = "", deadline : Date? = null, priority : Int = 0, id : Int = 0) : this() {
        this.id = id
        this.title = title
        this.description = description
        this.deadline = deadline
        this.priority = priority
        //println("TimeStamp: $timestamp")
    }

    fun hasTag(tag : String) = tags.contains(tag)
    fun addTag(tag: String) {
        this.tags.add(tag)
    }

    fun removeTag(tag: String) {
        this.tags.remove(tag)
    }

    fun completeTask() {
        this.completion = true
    }

    fun printItem() {
        println("Title: $title")
        println("Description: $description")
        println("Timestamp: $timestamp")
        println("Deadline: $deadline")
        println("Priority: $priority")
        print("Tags: ")
        println(tags.joinToString(prefix = "[\"", postfix = "\"]", separator = "\", \""))
    }
}

