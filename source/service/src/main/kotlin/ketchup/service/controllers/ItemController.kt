package ketchup.service.controllers

import ketchup.console.TodoItem
import java.sql.Connection
import java.sql.SQLException

class ItemController(connection: Connection) {
    val INVALID_ITEM_ID = -1
    private val conn: Connection = connection

    fun getTodoCount(): Int {
        return try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val getCountQuery = "SELECT MAX(item_id) FROM TodoItems";
                val result = query.executeQuery(getCountQuery);
                result.getInt(1)
            } else {
                INVALID_ITEM_ID
            }
        } catch (ex: SQLException) {
            println(ex.message)
            INVALID_ITEM_ID
        }
    }

    fun deleteItem(itemId: Int): Boolean {
        if(itemId == -1) {
            return false
        }

        try {
            return if(conn != null) {
                val query = conn!!.createStatement()
                val deleteItemQuery = "DELETE FROM TodoItems WHERE item_id=\"$itemId\""
                query.executeUpdate(deleteItemQuery)

                val tagQuery = conn!!.createStatement()
                val deleteTagsQuery = "DELETE FROM ItemTags WHERE item_id=\"$itemId\""
                tagQuery.executeUpdate(deleteTagsQuery)

                true

            } else {
                false
            }
        } catch (ex: SQLException) {
            println(ex.message)
            return false
        } catch (ex: Exception) {
            return false
        }
    }

    fun addItem(item: TodoItem, listId: Int): Int {

        try {
            var itemId = item.id
            if (conn != null) {
                val newId = getTodoCount()
                if (newId != -1) {
                    itemId = newId + 1
                }

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
                query.executeUpdate(addItemQueryString)
                // Add tags
                for (tag in item.tags) {
                    // Search for the tag in the tags table.
                    // If it exists, just attach the item to the tag.
                    // If it doesn't exist, add it to the tags table.
                    val searchTagQueryString = "SELECT name FROM Tags WHERE name=\"${tag}\";"
                    val result = query.executeQuery(searchTagQueryString)
                    if (result.next() == false) { // if the tag doesn't exist, add it
                        val addTagQueryString = "INSERT INTO Tags(name, color) VALUES (\"${tag}\", \"0\");" // 0 as the "color" for now
                        query.executeUpdate(addTagQueryString)
                    }
                    // Add item-tag pairing
                    val addPairQueryString = "INSERT INTO ItemTags(item_id, tag) VALUES (\"${itemId}\", \"${tag}\");"
                    query.executeUpdate(addPairQueryString)
                }
            }
            return itemId
        } catch (ex : SQLException) {
            println(ex.message)
            return INVALID_ITEM_ID
        }
    }

    fun editItem(item: TodoItem, id : Int) : Boolean {
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
                        "description = \"${item.description}\", " +
                        "timestamp = \"${item.timestamp}\", " +
                        "deadline = \"${item.deadline.toString()}\", " +
                        "priority = \"${item.priority}\", " +
                        "completion = \"${item.completion}\" " +
                        "WHERE item_id = ${id};"
                query.executeUpdate(editItemQueryString)

                // Remove old item-tag associations
                val removeOldTagsQueryString = "DELETE FROM ItemTags WHERE item_id = ${id}"
                query.executeUpdate(removeOldTagsQueryString)

                // Add tags
                for (tag in item.tags) {
                    // Search for the tag in the tags table.
                    // If it exists, just attach the item to the tag.
                    // If it doesn't exist, add it to the tags table.
                    val searchTagQueryString = "SELECT name FROM Tags WHERE name=\"${tag}\";"
                    val result = query.executeQuery(searchTagQueryString)
                    if (result.next() == false) { // if the tag doesn't exist, add it
                        val addTagQueryString = "INSERT INTO Tags(name, color) VALUES (\"${tag}\", \"0\");" // 0 as the "color" for now
                        query.executeUpdate(addTagQueryString)
                    }
                    // Add item-tag pairing
                    val addPairQueryString = "INSERT INTO ItemTags(item_id, tag) VALUES (\"${id}\", \"${tag}\");"
                    query.executeUpdate(addPairQueryString)
                }
            }
            return true
        } catch (ex : SQLException) {
            println(ex.message)
            return false
        }
    }
    fun editItem(item: TodoItem) : Boolean {
        return editItem(item, item.id)
    }
}