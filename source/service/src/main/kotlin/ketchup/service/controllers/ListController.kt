package ketchup.service.controllers

import ketchup.console.TodoItem
import ketchup.console.TodoList
import java.sql.Connection
import java.sql.SQLException
import java.text.SimpleDateFormat

class ListController(connection: Connection) {
    private val conn: Connection = connection
    val df = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy")

    fun createList(name: String): Int {
        return try {
            val query = conn!!.createStatement()
            val queryString = "INSERT INTO TodoLists(title) VALUES(\"$name\")"
            query.executeUpdate(queryString);
            val getIdQuery = conn!!.createStatement()
            val getIdString = "SELECT * FROM TodoLists WHERE title=\"$name\""
            val result = getIdQuery.executeQuery(getIdString)

            return result.getInt("list_id")
        } catch (ex: SQLException) {
            println(ex.message)
            return -1
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

                val deadlineStr = result.getString("deadline")
                val timestampStr = result.getString("timestamp")

                val item = TodoItem(
                    result.getString("title"),
                    result.getString("description"),
                    if (deadlineStr != "NULL") df.parse(deadlineStr) else null,
                    result.getInt("priority"),
                    result.getInt("item_id"),
                    tags,
                    df.parse(timestampStr),
                    result.getString("completion").toBooleanStrict()
                )
                list.addItem(item)
            }

            val newQuery = conn!!.createStatement()
            val nameQuery = "SELECT * FROM TodoLists WHERE list_id=\"$id\""
            val nameResult = newQuery.executeQuery(nameQuery)

            val listName = nameResult.getString("title")
            list.name = listName

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
            while (result.next()) {
                listIds.add(result.getInt("list_id"))
            }

            for (i in listIds) {
                list.add(TodoList(i))
            }

            var listQuery = conn!!.createStatement()
            val allListQuery = "SELECT * FROM TodoItems ORDER BY list_id"
            val listResult = listQuery.executeQuery(allListQuery)

            while (listResult.next()) {
                val tags = mutableListOf<String>()
                val id = listResult.getInt("item_id")
                val tagConn = conn!!.createStatement()
                val tagsQuery = "SELECT tag FROM ItemTags WHERE item_id=\"$id\""
                val tagResult = tagConn.executeQuery(tagsQuery)
                while(tagResult.next()) {
                    tags.add(tagResult.getString("tag"))
                }

                val deadlineStr = listResult.getString("deadline")
                val timestampStr = listResult.getString("timestamp")

                val item = TodoItem(
                    listResult.getString("title"),
                    listResult.getString("description"),
                    if (deadlineStr != "NULL") df.parse(deadlineStr) else null,
                    listResult.getInt("priority"),
                    listResult.getInt("item_id"),
                    tags,
                    df.parse(timestampStr),
                    listResult.getString("completion").toBooleanStrict()
                )
//
                val listId = listResult.getInt("list_id")
                val listIndex = list.indexOfFirst { it.id == listId }
                if(listIndex != -1) {
                    list[listIndex].addItem(item)
                }
            }

            for(i in 0 until list.size) {
                val id = list[i].id

                val newQuery = conn!!.createStatement()
                val nameQuery = "SELECT * FROM TodoLists WHERE list_id=\"$id\""
                val nameResult = newQuery.executeQuery(nameQuery)

                val listName = nameResult.getString("title")

                println(listName)

                list[i].name = listName
            }

            return list
        } catch(ex: SQLException) {
            println(ex.message)
            return list
        } catch(ex: Exception) {
            return list
        }
    }

    fun deleteList(listId: Int): Boolean {
        try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val deleteItemsQuery = "DELETE FROM TodoItems WHERE list_id=\"$listId\""
                query.executeUpdate(deleteItemsQuery)

                val listQuery = conn!!.createStatement()
                val deleteListQuery = "DELETE FROM TodoLists WHERE list_id=\"$listId\""
                listQuery.executeUpdate(deleteListQuery)

                return true
            } else {
                throw Exception("sql connection is undefined or null.")
                return false
            }
        } catch (ex: SQLException) {
            println(ex.message)
            return false
        } catch (ex: Exception) {
            println(ex.message)
            return false
        }

    }
}