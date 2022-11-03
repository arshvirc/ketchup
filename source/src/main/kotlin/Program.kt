import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.*
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Program {
    private var theList : TodoList = TodoList()
    private var saveFilePath : String = "./tasks.json"
    private val dbURL : String = "jdbc:sqlite:data.db"

    val format = Json { encodeDefaults = true }

    fun connect() : Connection? {
        var conn: Connection? = null
        try {
            conn = DriverManager.getConnection(dbURL)
            println("Connection to SQLite has been established.")
        } catch (e: SQLException) {
            println(e.message)
        }
        return conn
    }

    fun load(fileName : String) {
        connect()
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

    fun runCLI() {
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
}