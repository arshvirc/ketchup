import java.util.*

class TodoList() {
    var maxItemID = 0
    private var list = mutableListOf<TodoItem>()
    // Not sure if we want to introduce mappings between IDs and indices

    fun add(item: TodoItem) {
        var newItem = TodoItem(id = maxItemID, title = item.title, description = item.description,
            deadline = item.deadline, priority = item.priority);
        maxItemID++;
        list.add(newItem)
    }

    fun add(title : String, description : String = "", deadline : Date? = null, priority : Int = 0) {
        list.add(TodoItem(id = maxItemID, title = title, description = description,
                deadline = deadline, priority = priority))
        maxItemID++
    }

    // Remove item i from the list iff removeCondition(i) == true
    fun removeIf(removeCondition : (TodoItem) -> Boolean ) {
        for (item in list) {
            if (removeCondition(item)) {
                list.remove(item)
            }
        }
    }

    fun complete(item: TodoItem) {
         list[list.indexOf(item)].completeTask()
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
        list[list.indexOf(oldItem)] = newItem
    }

    fun editIf(editCondition: (TodoItem) -> Boolean, newItem: TodoItem) {
        for (item in list) {
            if (editCondition(item)) {
                list[list.indexOf(item)] = newItem
                break
            }
        }
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
        for(i in list) {
            i.printItem()
            println('\n')
        }
    }

    // fun sort()
    // fun filter()
}