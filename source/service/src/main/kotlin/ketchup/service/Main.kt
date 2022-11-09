package ketchup.service

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ketchup.service.plugins.*
fun main() {
    val program = Program()
    val conn = program.run()

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)

    program.close()
}

