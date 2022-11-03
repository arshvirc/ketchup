import javafx.application.Application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.*
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Program {
    private var theList : TodoList = TodoList()
    private var saveFilePath : String = "./tasks.json"
    private val dbURL : String = "jdbc:sqlite:data.db"
    private var conn: Connection? = null

    val format = Json { encodeDefaults = true }

    private fun connect() {
        try {
            conn = DriverManager.getConnection(dbURL)
            println("Connection to SQLite has been established.")
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    // run a query to determine if the DB is empty
    private fun DBIsEmpty() : Boolean {
        if (conn == null) return false
        val query = conn!!.createStatement()
        val emptyDBTestQuery = "SELECT * FROM sqlite_master WHERE type = 'table'"
        val result = query.executeQuery(emptyDBTestQuery)
        return result.next() == false
    }
    private fun setupDB() {
        if (conn == null) return
        val query = conn!!.createStatement()

        // Create Program table
        // Create TodoLists table
        // Create TodoItems table
        // Create ItemTags table
        // Create Tags table
        val queryStrings = listOf<String>(
            "CREATE TABLE TodoLists(list_id int PRIMARY KEY, maxItemID int);",
            "CREATE TABLE Program(list_id int, FOREIGN KEY(list_id) REFERENCES TodoLists(list_id));",
            "CREATE TABLE TodoItems(" +
                                    "item_id int PRIMARY KEY," +
                                    "title TEXT," +
                                    "description TEXT," +
                                    "timestamp DATETIME," +
                                    "deadline DATETIME," +
                                    "priority int," +
                                    "list_id int," +
                                    "FOREIGN KEY(list_id) REFERENCES TodoLists(list_id)" +
                                    ");",
            "CREATE TABLE Tags(tag_id int PRIMARY KEY, name varchar(255), color int);",
            "CREATE TABLE ItemTags(" +
                                    "item_id int, " +
                                    "tag_id int," +
                                    "FOREIGN KEY(item_id) REFERENCES TodoItems(item_id)" +
                                    "FOREIGN KEY(tag_id) REFERENCES Tags(tag_id)" +
                                    ");"
            )

        for (queryString in queryStrings) {
            try {
                query.execute(queryString)
            } catch (ex : SQLException) {
                println(ex.message)
            }
        }
    }

    fun load(fileName : String) {
        var saveFile = File(fileName)
        val fileCreated : Boolean = saveFile.createNewFile()
        if (fileCreated) {
            println("No save file found. Creating new file.")
        } else {
            val fileContents : String = saveFile.readText()
            try {
                theList = Json.decodeFromString(fileContents)
            } catch (decodingException : Exception) {
                println("Couldn't read from save file. Overwriting.")
            }
        }
    }

    fun save(fileName : String) {
        var saveFile = File(fileName)
        val JsonList = format.encodeToString(this.theList)
        saveFile.writeText(JsonList)
    }

    private fun runCLI() {
        load(saveFilePath)
        var command = ""
        println("Ketchup 0.1 by CS 346 Team 205")
        println("Welcome!")
        println("For help, type \"help\".")
        while (command != "quit" && command != "q") {
            print("(Ketchup) ")
            command = readLine()!!
            val regex = Regex("([^\"]\\S*|\".+?\")\\s*")
            val matches = regex.findAll(command)
            var commands = matches.map { it.groupValues[1] }.toList()
            var newCommand = CommandFactory.createFromArgs(commands)
            newCommand.execute(theList)
        }
        save(saveFilePath)
    }

    fun run(args: Array<String>) {
        connect()
        if (conn == null) {
            println("Unknown Error: could not establish connection to database. Exiting.")
        }

        if (conn != null && DBIsEmpty()) {
            setupDB()
        }

        var command = ""
        while (command != "1" || command != "2") {
            println("Menu\n1. Console Application\n2. Graphical Application\n3. Quit")
            command = readLine()!!
            if (command == "1") {
                runCLI()
                break
            } else if (command == "2") {
                Application.launch(MainApp::class.java)
                break
            } else if (command == "3") {
                System.exit(0)
            } else {
                println("Invalid command. Please Try again.")
            }
        }
        conn?.close()
    }
}