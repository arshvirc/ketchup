import java.util.*

class TodoList() {
    var maxItemID = 0
    private var list = mutableListOf<TodoItem>()
    var size = 0
    // Not sure if we want to introduce mappings between IDs and indices

    constructor(list: TodoList) : this() {
        var maxIdSeen = 0;
        for(i in list.list) {
            if(i.id > maxIdSeen) {
                maxIdSeen = i.id;
            }
            this.list.add(i)
        }
        maxItemID = maxIdSeen + 1;
    }

    fun add(item: TodoItem) {
        var newItem = TodoItem(id = maxItemID, title = item.title, description = item.description,
            deadline = item.deadline, priority = item.priority);
        maxItemID++;
        size++;
        list.add(newItem)
    }

    fun add(title : String, description : String = "", deadline : Date? = null, priority : Int = 0) {
        list.add(TodoItem(id = maxItemID, title = title, description = description,
                deadline = deadline, priority = priority))
        maxItemID++
        size++;
    }

    // Remove item i from the list iff removeCondition(i) == true
    fun removeIf(removeCondition : (TodoItem) -> Boolean ) {
        for (item in list) {
            if (removeCondition(item)) {
                list.remove(item)
                size--;
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