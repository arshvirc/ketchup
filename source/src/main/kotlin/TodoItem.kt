import java.util.Date

class TodoItem() {
    var id : Int = 0
        private set
    var title : String = ""
    var description : String = ""
    val timestamp : Date = Date(System.currentTimeMillis())
    var deadline : Date? = null
    var priority : Int = 0
    var tags = mutableSetOf<String>()
    var completion : Boolean = false

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
        println("ID: $id")
        println("Title: $title")
        println("Description: $description")
        println("Timestamp: $timestamp")
        println("Deadline: $deadline")
        println("Priority: $priority")
        println("Completion: $completion")
        print("Tags: ")
//        println(tags.joinToString(prefix = "[\"", postfix = "\"]", separator = "\", \""))
    }

    fun assertEqualItem(otherItem: TodoItem?): Boolean {
        if ( otherItem == null ) return false
        return ( title == otherItem.title &&
                description == otherItem.description &&
                deadline == otherItem.deadline &&
                priority == otherItem.priority &&
                completion == otherItem.completion)
    }
}

