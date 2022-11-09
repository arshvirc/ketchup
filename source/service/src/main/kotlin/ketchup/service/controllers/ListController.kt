package ketchup.service.controllers

import java.sql.Connection
import java.sql.SQLException

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
}