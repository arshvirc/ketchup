package ketchup.service.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.configureRouting() {


    routing {
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
            }
        }
    }
}
