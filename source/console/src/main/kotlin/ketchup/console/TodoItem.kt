package ketchup.console

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
class TodoItem() {
    var id : Int = 0
    var title : String = ""
    var description : String = ""
    @Serializable(with = DateSerializer::class)
    var timestamp : Date = Date(System.currentTimeMillis())
    @Serializable(with = DateSerializer::class)
    var deadline : Date? = null
    var priority : Int = 0
    var tags : MutableList<String> = mutableListOf()
    var completion : Boolean = false

    constructor(title:  String = "", description: String = "", deadline: Date? = null, priority: Int = 0, id: Int = 0, tags: MutableList<String> = mutableListOf(), timestamp: Date = Date(System.currentTimeMillis())) : this() {
        this.id = id
        this.title = title
        this.description = description
        this.deadline = deadline
        this.priority = priority
        for (tag in tags) {
            this.addTag(tag.trim())
        }
        this.timestamp = timestamp
    }

    constructor(title:  String = "", description: String = "", deadline: Date? = null, priority: Int = 0, id: Int = 0, tags: MutableList<String> = mutableListOf(), timestamp: Date = Date(System.currentTimeMillis()), completion: Boolean = false) : this() {
        this.id = id
        this.title = title
        this.description = description
        this.deadline = deadline
        this.priority = priority
        for (tag in tags) {
            this.addTag(tag.trim())
        }
        this.timestamp = timestamp
        this.completion = completion
    }
    fun addTag(tag: String) {
        this.tags.add(tag)
    }

    fun removeTag(tag: String) {
        this.tags.remove(tag)
    }

    fun hasTag(tag : String) = tags.contains(tag)

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
        println(tags)
    }

    fun assertEqualItem(otherItem: TodoItem?): Boolean {
        if ( otherItem == null ) return false
        return ( title == otherItem.title &&
                description == otherItem.description &&
                deadline == otherItem.deadline &&
                priority == otherItem.priority &&
                completion == otherItem.completion)
    }

    fun copy(): TodoItem {
        val copy_tags = mutableListOf<String>()
        for (tag in tags) {
            copy_tags.add(tag)
        }
        return TodoItem(title, description, deadline, priority, id, copy_tags, timestamp, completion)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date> {
    private val df: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(df.format(value))
    }

    override fun deserialize(decoder : Decoder): Date {
        return df.parse(decoder.decodeString())
    }

    override val descriptor : SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
}