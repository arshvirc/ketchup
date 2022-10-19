import java.util.Date
import javax.lang.model.type.NullType

class ToDoItem() {
    private var title : String = ""
    private var description : String = ""
    private val timestamp : Date = Date(System.currentTimeMillis())
    private var deadline : Date? = null
    private var priority : Int = 0
    private var tags = mutableSetOf<String>()
    private var completion : Boolean = false;

    constructor(title :  String, description : String, deadline : Date? = null, priority : Int=0) : this() {
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

