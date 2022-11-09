import ketchup.console.CommandFactory
import ketchup.console.TodoList
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.exp
import kotlin.test.expect

internal class ListCommandTest {

    @Test
    fun sortTest() {
        // TESTING SORT BY PRIORITY
        // Arrange
        val testList1 = TodoList()
        val expectedList1 = TodoList()


        val args1 = listOf("add", "-t", "item1", "-p", "5")
        val args2 = listOf("add", "-t", "item2", "-p", "10")
        val command1 = CommandFactory.createFromArgs(args1)
        val command2 = CommandFactory.createFromArgs(args2)
        command2.execute(expectedList1)
        command1.execute(expectedList1)

        // Act
        command1.execute(testList1)
        command2.execute(testList1)
        testList1.displayList()
        expectedList1.displayList()

        testList1.sort("p")

        //Assert

        testList1.getById(0)?.let { expectedList1.getById(1)?.let { it1 -> assertEquals(it.priority, it1.priority) } }
        testList1.getById(1)?.let { expectedList1.getById(0)?.let { it1 -> assertEquals(it.priority, it1.priority) } }

        // TESTING SORT BY DEADLINE

        // Arrange
        val testList2 = TodoList()
        val expectedList2 = TodoList()

        val args3 = listOf("add", "-t", "item", "-due", "27/04/2023")
        val args4 = listOf("add", "-t", "item2", "-due", "23/11/2022")
        val command3 = CommandFactory.createFromArgs(args3)
        val command4 = CommandFactory.createFromArgs(args4)

        command4.execute(expectedList2)
        command3.execute(expectedList2)

        // Act

        command3.execute(testList2)
        command4.execute(testList2)
        testList2.displayList()
        expectedList2.displayList()

        testList2.sort("due")

        // Assert
        assertEquals(testList2.getById(0)!!.deadline, expectedList2.getById(1)!!.deadline)
        assertEquals(testList2.getById(1)!!.deadline, expectedList2.getById(0)!!.deadline)
    }

    @Test
    fun filterTest() {
        // TESTING FILTERING BASED ON TAGS
        // Arrange
        val testList3 = TodoList()
        val expectedList3 = TodoList()

        val args1 = listOf("add", "-t", "item1", "-tags", "\"1,2,3\"")
        val args2 = listOf("add", "-t", "item2", "-tags", "\"4,5,2\"")
        val command1 = CommandFactory.createFromArgs(args1)
        val command2 = CommandFactory.createFromArgs(args2)
        command2.execute(testList3)
        command1.execute(testList3)

        command1.execute(expectedList3);

        val tags = listOf<String>("3")
        testList3.filter(tags);

        testList3.displayList()
        expectedList3.displayList()

        assertEquals(testList3.getById(1)?.title ?: 0, expectedList3.getById(0)?.title ?: 0)
    }
}