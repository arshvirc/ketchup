import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.test.expect

internal class CompleteCommandTest {
    @Test
    fun execute() {
        // Arrange
        val firstSharedItem1 = TodoItem("Complete Task Test 1", "Un-valid ID")

        var testList1 = TodoList()
        testList1.add(firstSharedItem1)

        var expectedList1 = TodoList()
        expectedList1.add(firstSharedItem1)

        val args1 = listOf("c", "2")
        val command1 = CommandFactory.createFromArgs(args1)

        // Act
        command1.execute(testList1)
        testList1.displayList()
        expectedList1.displayList()

        // Assert
        assertTrue( expectedList1.assertEqualList(testList1) )


        // Arrange
        val testItem2 = TodoItem("Complete Task Test 2", "Valid Id")

        var testList2 = TodoList()
        testList2.add(testItem2)

        var expectedList2 = TodoList()
        expectedList2.add(testItem2)
        expectedList2.completeIf { it.id == 0 }

        val args2 = listOf("c", "0")
        val command2 = CommandFactory.createFromArgs(args2)

        // Act
        command2.execute(testList2)
        testList2.displayList()
        expectedList2.displayList()

        // Assert
        assertTrue( expectedList2.assertEqualList(testList2) )
    }
}