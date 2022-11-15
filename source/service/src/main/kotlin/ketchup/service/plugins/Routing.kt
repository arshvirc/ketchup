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
data class GetItemResponse(val listId: Int, val item: TodoItem)


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
                // println(id)
                val controller = ListController(conn)

                val list = controller.getList(id)
                val response = ListResponse(id, list.size, list.list)

                call.respond(HttpStatusCode.Accepted, response)
            }

            delete("{id}") {
                val id: Int = call.parameters["id"]?.toInt() ?: -1
                val controller = ListController(conn)

                val response = controller.deleteList(id)

                if(response) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
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
                val id: Int = call.parameters["id"]?.toInt() ?: -1
                val controller = ItemController(conn)

                val response = controller.getTodoItem(id)
                if(response != null) {
                    call.respond(response)
                } else {
                    call.respondText("Item not found.")
                }
                // return item with specific id
            }

            put("{id?}") {
                // edit item with specific id
                val itemId = call.parameters["id"]?.toInt() ?: -1
                var item = call.receive<TodoItem>()
                val controller = ItemController(conn)
                val success = controller.editItem(item, itemId)
                if (success) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
            }

            delete("{id?}") {
                // delete item with specific id

                val itemId = call.parameters["id"]?.toInt() ?: -1
                val controller = ItemController(conn)

                val response = controller.deleteItem(itemId)

                if(response) {
                    call.respondText("success")
                } else {
                    call.respondText("failure")
                }
            }

            post {
                // create item
                val item = call.receive<AddItemRequest>()
                val controller = ItemController(conn)
                val newId = controller.addItem(item.item, item.listId)

                if (newId != controller.INVALID_ITEM_ID) {
                    call.respond<Int>(newId) // maybe an HTTP status code here
                } else {
                    call.respondText("failure")
                }
            }
        }
    }
}
