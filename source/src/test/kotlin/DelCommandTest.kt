import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class DelCommandTest {
    @Test
    fun execute() {
        // Arrange
        val testItem1 = TodoItem("Delete Test 1", "Delete by Title")

        var testList1 = TodoList()
        testList1.add(testItem1)

        var expectedList1 = TodoList()

        val args1 = listOf("del", "0")
        val command1 = CommandFactory.createFromArgs(args1)

        // Act
        command1.execute(testList1)
        testList1.displayList()
        expectedList1.displayList()

        // Assert
        assertTrue( expectedList1.assertEqualList(testList1) )



        // Arrange
        val testItem2 = TodoItem("Delete Test 2", "Delete by Title")

        var testList2 = TodoList()
        testList1.add(testItem2)

        var expectedList2 = TodoList()

        val args2 = listOf("del", "Delete Test 2")
        val command2 = CommandFactory.createFromArgs(args2)

        // Act
        command2.execute(testList2)
        testList2.displayList()
        expectedList2.displayList()

        // Assert
        assertTrue( expectedList2.assertEqualList(testList2) )

        // Arrange
        val firstSharedItem3 = TodoItem("Prepare for meeting", "Finish unit testing", priority = 1)
        val testItem3 = TodoItem("Add details", priority = 2)

        var testList3 = TodoList()
        testList3.add(firstSharedItem3)
        testList3.add(testItem3)


        var expectedList3 = TodoList()
        expectedList3.add(firstSharedItem3)

        val args3 = listOf("del", "1" )
        val command3 = CommandFactory.createFromArgs(args3)

        // Act
        command3.execute(testList3)
        testList3.displayList()
        expectedList3.displayList()

        // Assert
        assertTrue( expectedList3.assertEqualList(testList3) )


        // Arrange
        val firstSharedItem4 = TodoItem("Night Routine", "Go for a walk", priority = 2)
        val secondSharedItem4 = TodoItem("Buy Food", priority = 0)
        val testItem4 =  TodoItem("Finish Week Prep", "Journal, Meal Prep", priority = 5)
        val thirdSharedItem4 =  TodoItem("Abstain from Coffee", "For a week.", priority = 5)

        var testList4 = TodoList()
        testList4.add(firstSharedItem4)
        testList4.add(secondSharedItem4)
        testList4.add(testItem4)
        testList4.add(thirdSharedItem4)

        var expectedList4 = TodoList()
        expectedList4.add(firstSharedItem4)
        expectedList4.add(secondSharedItem4)
        expectedList4.add(testItem4)
        expectedList4.add(thirdSharedItem4)
        expectedList4.removeIf { it.id == 2 }

        val args4 = listOf("del", "2" )
        val command4 = CommandFactory.createFromArgs(args4)

        // Act
        command4.execute(testList4)
        testList4.displayList()
        expectedList4.displayList()

        // Assert
        assertTrue( expectedList4.assertEqualList(testList4) )
    }
}