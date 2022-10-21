import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ListCommandTest {

    @Test
    fun execute() {
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
}