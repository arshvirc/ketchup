package ketchup.service.controllers

import ketchup.console.TodoItem
import ketchup.console.TodoList
import java.sql.Connection
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

class ListController(connection: Connection) {
    private val conn: Connection = connection

    fun createList(name: String): Boolean {
        return try {
            val query = conn!!.createStatement()
            val queryString = "INSERT INTO TodoLists(title) VALUES(\"$name\")"
            query.execute(queryString);
            true
        } catch (ex: SQLException) {
            false
        }
    }

    fun getList(id: Int): TodoList {
        val list = TodoList()
        try {
            val query = conn!!.createStatement()
            val queryString = "SELECT * FROM TodoItems WHERE list_id=\"$id\""
            val result = query.executeQuery(queryString)
            while(result.next()) {
                val tags = mutableListOf<String>()
                val id = result.getInt("item_id")
                val tagConn = conn!!.createStatement()
                val tagsQuery = "SELECT tag FROM ItemTags WHERE item_id=\"$id\""
                val tagResult = tagConn.executeQuery(tagsQuery)
                while(tagResult.next()) {
                    tags.add(tagResult.getString("tag"))
                }

                val item = TodoItem(
                    result.getString("title"),
                    result.getString("description"),
                    // TODO: figure out how to parse deadline
                    Date(System.currentTimeMillis()),
                    result.getInt("priority"),
                    result.getInt("item_id"),
                    tags,
                    // TODO: figure out how to parse timestamp (will be same as deadline)
                    Date(System.currentTimeMillis())
                )
                list.addItem(item)
            }

            return list
        } catch (ex: SQLException) {
            println(ex.message)
            return list
        } catch (ex: Exception) {
            println(ex.message)
            return list
        }
    }

    fun getAllLists(): MutableList<TodoList> {
        val list = mutableListOf<TodoList>()
        try {
            val query = conn!!.createStatement()
            val listIdQuery = "SELECT DISTINCT list_id FROM TodoItems"
            val result = query.executeQuery(listIdQuery)
            val listIds = mutableListOf<Int>()
            while(result.next()) {
                listIds.add(result.getInt("list_id"))
            }

            for (i in listIds) {
                list.add(TodoList(i))
            }


            var listQuery = conn!!.createStatement()
            val allListQuery = "SELECT * FROM TodoItems ORDER BY list_id"
            val listResult = listQuery.executeQuery(allListQuery)

            while(listResult.next()) {
                val tags = mutableListOf<String>()
                val id = listResult.getInt("item_id")
                val tagConn = conn!!.createStatement()
                val tagsQuery = "SELECT tag FROM ItemTags WHERE item_id=\"$id\""
                val tagResult = tagConn.executeQuery(tagsQuery)
                while(tagResult.next()) {
                    tags.add(tagResult.getString("tag"))
                }

                val item = TodoItem(
                    listResult.getString("title"),
                    listResult.getString("description"),
                    // TODO: figure out how to parse deadline
                    Date(System.currentTimeMillis()),
                    listResult.getInt("priority"),
                    listResult.getInt("item_id"),
                    tags,
                    // TODO: figure out how to parse timestamp (will be same as deadline)
                    Date(System.currentTimeMillis())
                )
//
                val listId = listResult.getInt("list_id")
                val listIndex = list.indexOfFirst { it.id == listId }
                if(listIndex != -1) {
                    list[listIndex].addItem(item)
                }
            }

            return list
        } catch(ex: SQLException) {
            println(ex.message)
            return list
        } catch(ex: Exception) {
            return list
        }
    }
}