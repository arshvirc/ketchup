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

    fun addItem(item: TodoItem, listId: Int): Boolean {
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
                        "\"$listId\"," +
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

    fun editItem(item: TodoItem) : Boolean {
        try {
            if (conn != null) {
                // Procedure:
                // 1) Update the item itself
                // 2) Remove all tags associated with item (clean up any old tags)
                // 3) Add all tags associated with item

                val query = conn!!.createStatement()
                // Update the item itself

                val editItemQueryString = "UPDATE TodoItems SET " +
                        "title = \"${item.title}\", " +
                        "description = \"{item.description}\", " +
                        "timestamp = \"${item.timestamp}\", " +
                        "deadline = \"${item.deadline.toString()}\", " +
                        "priority = \"${item.priority}\", " +
                        "completion = \"${item.completion}\" " +
                        "WHERE item_id = ${item.id};"
                query.executeQuery(editItemQueryString)

                // Remove old item-tag associations
                val removeOldTagsQueryString = "DELETE FROM ItemTags WHERE item_id = ${item.id}"
                query.executeQuery(removeOldTagsQueryString)

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
                    val addPairQueryString = "INSERT INTO ItemTags(item_id, tag) VALUES (\"${item.id}\", \"${tag}\");"
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