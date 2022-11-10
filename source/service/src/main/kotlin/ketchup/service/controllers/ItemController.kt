package ketchup.service.controllers

import ketchup.console.TodoItem
import java.sql.Connection
import java.sql.SQLException

class ItemController(connection: Connection) {
    private val conn: Connection = connection

    fun getTodoCount(): Int {
        return try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val getCountQuery = "SELECT MAX(item_id) FROM TodoItems";
                val result = query.executeQuery(getCountQuery);
                result.getInt(1)
            } else {
                -1
            }
        } catch (ex: SQLException) {
            println(ex.message)
            -1
        }
    }

    fun addItem(item: TodoItem): Boolean {
        try {
            if (conn != null) {
                val newId = getTodoCount();
                val itemId = if(newId != -1) newId + 1 else item.id
                val query = conn!!.createStatement()
                // Add the actual item
                val addItemQueryString = "INSERT INTO TodoItems(item_id, title, description, timestamp, deadline, priority, list_id, completion) " +
                        "VALUES (" +
                        "\"${itemId}\", " +
                        "\"${item.title}\", " +
                        "\"${item.description}\", " +
                        "\"${item.timestamp}\"," +
                        "\"${item.deadline.toString()}\", " +
                        "\"${item.priority}\", " +
                        "\"0\"," +
                        "\"${item.completion}\");"
                query.execute(addItemQueryString)
                // Add tags
                for (tag in item.tags) {
                    // Search for the tag in the tags table.
                    // If it exists, just attach the item to the tag.
                    // If it doesn't exist, add it to the tags table.
                    val searchTagQueryString = "SELECT name FROM Tags WHERE name=\"${tag}\";"
                    val result = query.executeQuery(searchTagQueryString)
                    if (result.next() == false) { // if the tag doesn't exist, add it
                        val addTagQueryString = "INSERT INTO Tags(name, color) VALUES (\"${tag}\", \"0\");" // 0 as the "color" for now
                        query.execute(addTagQueryString)
                    }
                    // Add item-tag pairing
                    val addPairQueryString = "INSERT INTO ItemTags(item_id, tag) VALUES (\"${itemId}\", \"${tag}\");"
                    query.execute(addPairQueryString)
                }
            }
            return true
        } catch (ex : SQLException) {
            println(ex.message)
            return false
        }
    }
}