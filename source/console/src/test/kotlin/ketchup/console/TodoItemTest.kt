import ketchup.console.TodoItem
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class TodoItemTest {

    @Test
    fun addTag() {
        // test when tags functionality is created for the list
        // Arrange
        var testItem = TodoItem("Add Tag")
        val testTag = "I should be there"

        // Act
        testItem.addTag(testTag)

        // Assert
        assertTrue( testItem.tags.contains(testTag) )
    }

    @Test
    fun removeTag() {
        var testItem = TodoItem("Remove Tag")
        val testTag = "I should be gone there"
        testItem.tags.add(testTag)

        // Act
        testItem.removeTag(testTag)

        // Assert
        assertTrue(!testItem.tags.contains(testTag))
    }

    fun hasTag() {
        var testItem = TodoItem("Add Tag")
        val testTag = "I should be there"
        testItem.tags.add(testTag)

        // Act
        val result = testItem.hasTag(testTag)

        // Assert
        assertTrue( result )
    }

    @Test
    fun completeTask() {
        // Arrange
        var testItem1 = TodoItem("Hi")
        // Act
        testItem1.completeTask()
        // Assert
        assertTrue(testItem1.completion)
    }

    @Test
    fun assertEqualItem() {
        // Arrange
        var expected1 = TodoItem("hi", "hello", null, 1, 1)
        var actual1 = TodoItem("hi", "hello", null, 1, 1)

        // Act
        val result1 = expected1.assertEqualItem(actual1)

        // Assert
        assertTrue(result1)

        // Arrange
        var expected2 = TodoItem("hi", "hello", null, 1, 1)
        var actual2 = TodoItem("hello", "hi", null, 1, 1)

        // Act
        val result2 = expected2.assertEqualItem(actual2)

        // Assert
        assertTrue(!result2)
    }
}