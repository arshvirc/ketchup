import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class EditCommandTest {
    @Test
    fun execute() {

        // Arrange
        val testItem1 = TodoItem("Write Tests", "For Add, Edit, Del")
        val expectedItem1 = TodoItem("Write Tests", "For Add, Edit, Del")

        var testList1 = TodoList()
        testList1.add(testItem1)

        var expectedList1 = TodoList()
        expectedList1.add(expectedItem1)

        val args1 = listOf("edit", "2", "-desc", "For Edit, Del" )
        val command1 = CommandFactory.createFromArgs(args1)

        // Act
        command1.execute(testList1)
        testList1.displayList()
        expectedList1.displayList()

        // Assert
        assertTrue( expectedList1.assertEqualList(testList1) )


        // Arrange
        val firstSharedItem2 = TodoItem("Prepare for Demo", "Converse with group members", priority = 4)
        val testItem2 = TodoItem("Finish writing unit testing", priority = 5)
        val expectedItem2 = TodoItem("Finish writing unit testing", "By Midnight", priority = 5)

        var testList2 = TodoList()
        testList2.add(firstSharedItem2)
        testList2.add(testItem2)

        var expectedList2 = TodoList()
        expectedList2.add(firstSharedItem2)
        expectedList2.add(expectedItem2)

        val args2 = listOf("e", "1", "-desc", "By Midnight" )
        val command2 = CommandFactory.createFromArgs(args2)

        // Act
        command2.execute(testList2)
        testList2.displayList()
        expectedList2.displayList()

        // Assert
        assertTrue( expectedList2.assertEqualList(testList2) )


        // Arrange
        val firstSharedItem3 = TodoItem("Morning Chores", "Walk your dog", priority = 5)
        val secondSharedItem3 = TodoItem("Grocery Shopping", priority = 5)
        val testItem3 =  TodoItem("Meal Prep", "Make pasta", priority = 5)
        val expectedItem3 =  TodoItem("Meal Prep", "Make pasta, rice, salads", priority = 10)
        val thirdSharedItem3 =  TodoItem("Buy Coffee Beans", "Make sure its ethical.", priority = 5)

        var testList3 = TodoList()
        testList3.add(firstSharedItem3)
        testList3.add(secondSharedItem3)
        testList3.add(testItem3)
        testList3.add(thirdSharedItem3)

        var expectedList3 = TodoList()
        expectedList3.add(firstSharedItem3)
        expectedList3.add(secondSharedItem3)
        expectedList3.add(expectedItem3)
        expectedList3.add(thirdSharedItem3)

        val args3 = listOf("e", "2", "-desc", "Make pasta, rice, salads", "-p", "10" )
        val command3 = CommandFactory.createFromArgs(args3)

        // Act
        command3.execute(testList3)
        testList3.displayList()
        expectedList3.displayList()

        // Assert
        assertTrue( expectedList3.assertEqualList(testList3) )
    }
}