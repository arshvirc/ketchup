package ketchup.service.controllers

import kotlinx.serialization.Serializable
import java.lang.Exception
import java.sql.Connection
import java.sql.SQLException

@Serializable
data class DeleteTagResponse(val success: Boolean, val tags: List<String>)

class TagController(connection: Connection) {
    private val conn = connection;

    fun getAllTags(): List<String> {
        try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val getTagsQuery = "SELECT name FROM Tags";
                val result = query.executeQuery(getTagsQuery);

                val tags: MutableList<String> = mutableListOf();

                while(result.next()) {
                    tags.add(result.getString("name"));
                }

                return tags
            } else {
                return mutableListOf()
            }
        } catch (ex: SQLException) {
            println(ex.message)
            return mutableListOf()
        }
    }

    fun createNewTag(tag: String): List<String> {
        try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val createTagQuery = "INSERT INTO Tags(name, color) " +
                "VALUES (" + "\"${tag}\","+ "\"0\");"
                query.executeUpdate(createTagQuery);

                val newTags = getAllTags()

                return newTags
            } else {
                return mutableListOf();
            }
        } catch (ex: SQLException) {
            println(ex.message);
            return mutableListOf()
        }
    }

    fun deleteTag(name: String): DeleteTagResponse {
        try {
            if(conn != null) {
                val query = conn!!.createStatement()
                val deleteQuery = "DELETE FROM TAGS WHERE name=\"$name\""

                query.executeUpdate(deleteQuery)

                val deleteItemTags = conn!!.createStatement()
                val deleteItemQuery = "DELETE FROM ItemTags WHERE tag=\"$name\""

                deleteItemTags.executeUpdate(deleteItemQuery);

                val newTags = getAllTags()

                return DeleteTagResponse(true, newTags)
            } else {
                return DeleteTagResponse(false, listOf())
            }
        } catch (ex: Exception) {
            println(ex.message)
            return DeleteTagResponse(false, listOf())
        }
    }
}