package ketchup.service.controllers

import java.sql.Connection
import java.sql.SQLException

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
}