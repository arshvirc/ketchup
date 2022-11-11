package ketchup.service.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ketchup.service.controllers.ListController
import java.sql.Connection
import ketchup.console.TodoItem
import ketchup.console.TodoList
import ketchup.service.controllers.ItemController

data class ListName(val name: String)
data class ListResponse(val listId: Int, val size: Int, val list: MutableList<TodoItem>)
data class ListsResponse(val lists: MutableList<TodoList>)
data class AddItemRequest(val listId: Int, val item: TodoItem)


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
                val controller = ListController(conn)
                val lists = controller.getAllLists()

                val response = ListsResponse(lists)

                call.respond(HttpStatusCode.Accepted, response)

            }

            get("{id}") {
                val id: Int = call.parameters["id"]?.toInt() ?: 0
                println(id)
                val controller = ListController(conn)

                val list = controller.getList(id)
                val response = ListResponse(id, list.size, list.list)

                call.respond(HttpStatusCode.Accepted, response)
            }

            delete("{id}") {
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
                val item = call.receive<AddItemRequest>()
                val controller = ItemController(conn)
                val success = controller.addItem(item.item, item.listId)

                if(success) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
            }
        }
    }
}
