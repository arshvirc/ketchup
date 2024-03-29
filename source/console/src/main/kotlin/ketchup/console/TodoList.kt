package ketchup.console

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class TodoList() {
    var maxItemID = 0
    var list = mutableListOf<TodoItem>()
    var size = 0
    var id = 0
    var name: String = ""
    // Not sure if we want to introduce mappings between IDs and indices

    constructor(list: TodoList) : this() {
        var maxIdSeen = 0
        for(i in list.list) {
            if(i.id > maxIdSeen) {
                maxIdSeen = i.id
            }
            this.list.add(i)
        }
        maxItemID = maxIdSeen + 1
    }

    constructor(id: Int) : this() {
        this.id = id
    }

    fun add(item: TodoItem) {
        var newItem = TodoItem(
            id = maxItemID, title = item.title, description = item.description,
            deadline = item.deadline, priority = item.priority, tags = item.tags)
        maxItemID++
        size++
        list.add(newItem)
    }

    fun addItem(item: TodoItem) {
        list.add(item)
        size++
    }

    fun add(title : String, description : String = "", deadline : Date? = null, priority : Int = 0, tags: MutableList<String> = mutableListOf()) {
        list.add(
            TodoItem(
            id = maxItemID, title = title, description = description,
                deadline = deadline, priority = priority, tags = tags)
        )
        maxItemID++
        size++
    }

    // Remove item i from the list iff removeCondition(i) == true
    fun removeIf(removeCondition : (TodoItem) -> Boolean ) {
        for (item in list) {
            if (removeCondition(item)) {
                list.remove(item)
                size--
            }
        }
    }

    // Complete item i in the list iff completeCondition(i) == true
    fun completeIf(completeCondition : (TodoItem) -> Boolean ) {
        for (item in list) {
            if (completeCondition(item)) {
                item.completeTask()
            }
        }
    }

    fun edit(oldItem: TodoItem, newItem: TodoItem) {
        //list[list.indexOf(oldItem)] = newItem
        val oldId = oldItem.id;
        val index = list.indexOfFirst { it.id == oldId }
        list[index] = newItem


    }

    fun getById(id: Int): TodoItem? {
        for(item in list) {
            if(item.id == id) {
                return item
            }
        }

        return null
    }

    fun displayList() {
        if (list.isEmpty()) {
            println("No tasks to display.")
        }
        for (i in list) {
            i.printItem()
            println('\n')
        }
    }

    private fun sortByDeadline() {
        list.sortWith(compareBy(nullsLast()) { it.deadline })
    }

    private fun sortByPriority() {
        list.sortWith(compareByDescending { it.priority })
    }

     fun sort(flag: String) {
        when(flag) {
            "p", "priority" -> sortByPriority()
            "due", "duedate", "deadline" -> sortByDeadline()
        }
     }

    fun filter(arg: List<String>) {
//        print(arg)
//        for(i in list) {
//            println(arg.toSet().intersect(i.tags.toSet()).isNotEmpty())
//        }
        list = list.filter { it.tags.toSet().intersect(arg.toSet()).isNotEmpty() } as MutableList<TodoItem>
    }


    fun assertEqualList(otherList: TodoList): Boolean {
        if ( size != otherList.size ) {
            return false
        } else if ( size == 0 ) {
            return true;
        }
        for ( i in 0 until size ) {
            var item = list[i]
            var itemID = item.id
            var otherItem = otherList.getById(itemID)
            if ( ! item.assertEqualItem(otherItem) ) {
                return false
            }
        }
        return true
    }


    // fun sort()
    // fun filter()
}