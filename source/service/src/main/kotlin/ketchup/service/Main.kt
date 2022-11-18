package ketchup.service

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ketchup.service.plugins.*
import java.io.File
import java.io.PrintWriter

fun createFile(filename: String) {
    var file = File(filename)
    file.createNewFile()
    val writer = PrintWriter(file)
    writer.print("")
    writer.close()
}

fun deleteFile(filename: String) {
    val file = File(filename);
    val result = file.delete()
    if (!result) {
        println("Error deleting $filename")
    }
}
fun main() {
    /*
    Testing set up to show TA tests for Web Service Client
     */
    val testingClient = false
    if(testingClient) {
        createFile("./test.db")
        val program = Program("jdbc:sqlite:test.db")
        val conn = program.run()

        println("THIS IS A TESTING SERVER WITH A TEST DATABASE (TO TEST THE WEB SERVICE CLIENT)")

        embeddedServer(Netty, port = 4040, host = "127.0.0.1") {
            if (conn != null) {
                configureRouting(conn)
            }
            configureSerialization()
        }.start(wait = true)

        program.close()
        deleteFile("./test.db")
    } else {
        val program = Program()
        val conn = program.run()

        embeddedServer(Netty, port = 3000, host = "127.0.0.1") {
            if (conn != null) {
                configureRouting(conn)
            }
            configureSerialization()
        }.start(wait = true)

        program.close()
    }
}

