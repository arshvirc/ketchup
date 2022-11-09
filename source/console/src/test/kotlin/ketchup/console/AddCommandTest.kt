import ketchup.console.CommandFactory
import ketchup.console.TodoItem
import ketchup.console.TodoList
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class AddCommandTest {
    @Test
    fun execute() {
        // Arrange
        var testList1 = TodoList()

        var expectedList1 = TodoList()

        val args1 = listOf("add", "-t")
        val command1 = CommandFactory.createFromArgs(args1)

        // Act
        command1.execute(testList1)
        testList1.displayList()
        expectedList1.displayList()

        // Assert
        assertTrue( expectedList1.assertEqualList(testList1) )


        // Arrange

        val expectedItem2 = TodoItem("Give Updates", "Talk about issue 51 and issue 43")

        var testList2 = TodoList()

        var expectedList2 = TodoList()
        expectedList2.add(expectedItem2)

        val args2 = listOf("a", "-t", "Give Updates", "-desc", "Talk about issue 51 and issue 43")
        val command2 = CommandFactory.createFromArgs(args2)

        // Act
        command2.execute(testList2)
        testList2.displayList()
        expectedList2.displayList()

        // Assert
        assertTrue( expectedList2.assertEqualList(testList2) )


        // Arrange
        val firstSharedItem3 = TodoItem("Finish Unit Testing", "For AddCommandTest")
        val expectedItem3 = TodoItem("Finish Integration Testing", "For adding and editing")

        var testList3 = TodoList()
        testList3.add(firstSharedItem3)

        var expectedList3 = TodoList()
        expectedList3.add(firstSharedItem3)
        expectedList3.add(expectedItem3)

        val args3 = listOf("a", "-t", "Finish Integration Testing", "-desc", "For adding and editing")
        val command3 = CommandFactory.createFromArgs(args3)

        // Act
        command3.execute(testList3)
        testList3.displayList()
        expectedList3.displayList()

        // Assert
        assertTrue( expectedList3.assertEqualList(testList3) )

        // Arrange
        var testList4 = TodoList();
        var args4 = listOf("a", "-t", "Bad Priority", "-p", "4")
        val command4 = CommandFactory.createFromArgs(args4);
        // Act

        command4.execute(testList4)

        // Assert
        assertEquals(testList4.size, 0)
    }
}