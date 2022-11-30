package ketchup.service
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import ketchup.console.TodoItem

class Program(private var dbURL: String = "jdbc:sqlite:data.db") {
    private var conn: Connection? = null

    val format = Json { encodeDefaults = true }

    private fun connect() {
        try {
            conn = DriverManager.getConnection(dbURL)
            println("Connection to SQLITE established.")
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    private fun DBIsEmpty() : Boolean {
        if (conn == null) return false
        val query = conn!!.createStatement()
        val emptyDBTestQuery = "SELECT * FROM sqlite_master WHERE type = 'table'"
        val result = query.executeQuery(emptyDBTestQuery)
        return result.next() == false
    }

    fun setupDB() {
        connect()
        if (conn == null) {
            println("Unknown Error: could not establish connection to database. Exiting.")
            return
        }
        if (!DBIsEmpty()) return
        val query = conn!!.createStatement()

        // Create Program table
        // Create TodoLists table
        // Create TodoItems table
        // Create ItemTags table
        // Create Tags table
        // Initialize TodoLists and Program table
        val queryStrings = listOf<String>(
            "CREATE TABLE TodoLists(list_id INTEGER PRIMARY KEY AUTOINCREMENT, title varchar(255));",
            "CREATE TABLE Program(list_id int, FOREIGN KEY(list_id) REFERENCES TodoLists(list_id));",
            "CREATE TABLE TodoItems(" +
                    "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "description TEXT," +
                    "timestamp TEXT," +
                    "deadline TEXT," +
                    "priority int," +
                    "list_id int," +
                    "completion BIT," +
                    "FOREIGN KEY(list_id) REFERENCES TodoLists(list_id)" +
                    ");",
            "CREATE TABLE Tags(name varchar(255) PRIMARY KEY, color int);",
            "CREATE TABLE ItemTags(" +
                    "item_id int, " +
                    "tag varchar(255)," +
                    "FOREIGN KEY(item_id) REFERENCES TodoItems(item_id)" +
                    "FOREIGN KEY(tag) REFERENCES Tags(name)" +
                    ");",
            "CREATE TABLE Archive(" +
                    "item_id int" +
                    ");",
            "INSERT INTO TodoLists(list_id, title) VALUES (\"0\", \"Main List\")",
            "INSERT INTO Program(list_id) VALUES (\"0\")"
        )

        for (queryString in queryStrings) {
            try {
                query.execute(queryString)
            } catch (ex : SQLException) {
                println(ex.message)
            }
        }
    }

    fun run(): Connection? {
        setupDB()
        return conn;
    }

    fun close() {
        conn?.close()
    }
}