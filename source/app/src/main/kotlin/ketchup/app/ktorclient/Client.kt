package ketchup.app.ktorclient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import ketchup.console.TodoItem
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TodoItemResponse(val id: Int, val title: String, val description: String, val timestamp: String, val deadline: String?,
                            val priority: Int, val tags: MutableList<String>?, val completion: Boolean)
@Serializable
data class GetItemResponse(val listId: Int, val item: TodoItemResponse)

@Serializable
data class ListsResponse(val list: List<TodoItemResponse>, val size: Int, val id: Int, val name: String)

@Serializable
data class GetListsResponse(val lists: List<ListsResponse>)

@Serializable
data class SingleListResponse(val listId: Int, val size: Int, val list: List<TodoItemResponse>, val name: String)

@Serializable
data class NewListRequest(val name: String)

@Serializable
data class AddItemRequest(val listId: Int, val item: TodoItem)

@Serializable
data class TagName(val name: String)

// Instantiate this by doing val api: Client = Client(url) <- url is the url of the api
class Client(url: String) {
    private val host = url
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /*
    Returns an item with a specific ID
    Will return an Object called GetItemResponse or NULL if the item wasn't found
    GetItemResponse has two fields:
    - listId: the id of the list that the item is in
    - item: TodoItemResponse wrapper. Use this to build the todoItem. Check the dataclass for the fields
     */
    suspend fun getTodoItem(id: Int): GetItemResponse? {
        return try {
            val url = "$host/api/todo/$id"
            val response: HttpResponse = client.get(url)
            var body = response.body<GetItemResponse>()

            body
        } catch (ex: Exception) {
            println(ex.cause)
            println(ex.stackTrace)
            println(ex.message)
            null
        }
    }

    /*
    Gets all the lists, or returns null
    Will return an object called GetListsResponse, which has the following structure:
    lists: [
        list: List of TodoItems,
        id: Int (id of the list),
        size: The length of the list
    ]
     */
    suspend fun getAllLists(): GetListsResponse? {
        try {
            val url = "$host/api/lists"
            val response: HttpResponse = client.get(url)

            return response.body<GetListsResponse>()
        } catch (ex: Exception) {
            println(ex.message)
            return null
        }
    }

    /*
    Gets a list based on ID
    Will return a list based on the ID given or NULL
    Response Object:
    {
        listId: Int (id of the list)
        size: Int (size)
        list: List<TodoItem> (list of TodoItems)
        name: String (name of the list)
    }
     */
    suspend fun getListById(id: Int): SingleListResponse? {
        return try {
            val url = "$host/api/lists/$id"
            val response: HttpResponse = client.get(url)

            response.body<SingleListResponse>()
        } catch(ex: Exception) {
            println(ex.message)
            null
        }
    }

    /*
    Deletes list based on ID
    Pass the ID, and it will delete the list AND all the items in the list
    Will return a Boolean (true for success, false otherwise).
     */
    suspend fun deleteListById(id: Int): Boolean {
        return try {
            val url = "$host/api/lists/$id"
            val response: String = client.delete(url).body()

            response == "success"
        } catch (ex: Exception) {
            println(ex.message)
            false
        }
    }

    /*
    Creates a list with the name passed as argument
    Will return a Boolean (true for success, false otherwise)
     */
    suspend fun createList(name: String): Int {
        try {
            val url = "$host/api/lists"
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(NewListRequest(name))
            }

            return response.body()
        } catch (ex: Exception) {
            println(ex.message)
            return -1
        }
    }

    /*
    This function edits the todoItem with the given ID. Pass in the new todoItem to essentially replace the item with
    the given ID. Will return a Boolean (true for success, false otherwise).
     */
    suspend fun editTodoItem(id: Int, item: TodoItem): Boolean {
        try {
            val url = "$host/api/todo/$id"
            val response: HttpResponse = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(item)
            }

            val res = response.body<String>()
            return res == "success"
        } catch (ex: Exception) {
            println(ex.message)
            return false
        }
    }

    /*
    This function deletes a todoItem with a specific ID
    Return a boolean (true for success, false otherwise).
    */
    suspend fun deleteTodoItem(id: Int): Boolean {
        try {
            val url = "$host/api/todo/$id"
            val response: HttpResponse = client.delete(url)

            val res: String = response.body<String>()
            return res == "success"
        } catch (ex: Exception) {
            println(ex.message)
            return false
        }
    }

    /*
    This function creates a new TodoItem. Pass the listId that you would like to create the item in
    (pass 0 if we only have one list), and the new TodoItem.
    Will return an Int (the new item's ID if successful, or -1 otherwise)
     */
    suspend fun createTodoItem(listId: Int, item: TodoItem):Int {
        try {
            val url = "$host/api/todo"
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(AddItemRequest(listId, item))
            }

            return response.body()
        } catch (ex: Exception) {
            return -1
        }
    }

    suspend fun getAllTags(): List<String> {
        try {
            val url = "$host/api/tags"
            val response = client.get(url);
            return response.body()
        } catch(ex: Exception) {
            return mutableListOf()
        }
    }

    suspend fun createNewTag(name: String): List<String> {
        try {
            val url = "$host/api/tags"
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(TagName(name))
            }

            return response.body()

        } catch(ex: Exception) {
            println(ex)
            return mutableListOf()
        }
    }

    fun close() {
        client.close()
    }
}