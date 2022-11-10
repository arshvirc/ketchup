package ketchup.service.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ketchup.service.controllers.ListController
import java.sql.Connection
import ketchup.console.TodoItem
import ketchup.service.controllers.ItemController

data class ListName(val name: String)

fun Application.configureRouting(conn: Connection) {


    routing {
        route("/"){
            get {
                call.respondText("Hello World!");
            }
        }
        route("/api/lists") {
            get {
                // return all lists as JSON
            }

            get("{id?}") {
                // return list with specific id
            }

            delete("{id?}") {
                // delete list with id
            }

            post {
                // create a new list
                val name = call.receive<ListName>().name;
                val controller = ListController(conn)
                val success = controller.createList(name)

                if(success) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
            }
        }

        route("/api/todo") {
            get("{id?}") {
                // return todo item with specific id
            }

            put("{id?}") {
                // edit todo item with specific id
            }

            delete("{id?}") {
                // delete todo item with specific id
            }

            post {
                // create item
                val item = call.receive<TodoItem>()
                val controller = ItemController(conn)
                val success = controller.addItem(item)
//                println(item.completion)


                if(success) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
            }
        }
    }
}
