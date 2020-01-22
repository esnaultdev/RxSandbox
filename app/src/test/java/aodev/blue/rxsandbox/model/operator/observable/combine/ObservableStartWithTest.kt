package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.empty
import aodev.blue.rxsandbox.model.inputOf
import aodev.blue.rxsandbox.model.never
import org.junit.Assert
import org.junit.Test


class ObservableStartWithTest {

    private fun <T : Any> operator(value: T) = ObservableStartWith(value)

    @Test
    fun neverSource() {
        // Given
        val input = ObservableT.never<Int>()

        val operator = operator(1)

        // When
        val result = operator.apply(input)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 1),
                termination = ObservableT.Termination.None
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun emptySource() {
        // Given
        val input = ObservableT.empty<Int>()

        val operator = operator(1)

        // When
        val result = operator.apply(input)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 1),
                termination = ObservableT.Termination.Complete(0f)
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun errorSource() {
        // Given
        val input = ObservableT<Int>(emptyList(), ObservableT.Termination.Error(0f))

        val operator = operator(1)

        // When
        val result = operator.apply(input)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 1),
                termination = ObservableT.Termination.Error(0f)
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun otherEvents() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(2f to 4, 7f to 8),
                termination = ObservableT.Termination.None
        )

        val operator = operator(1)

        // When
        val result = operator.apply(input)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 4, 7f to 8),
                termination = ObservableT.Termination.None
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun beforeFirstEvent() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 5),
                termination = ObservableT.Termination.None
        )

        val operator = operator(12)

        // When
        val result = operator.apply(input)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 12, 0f to 5),
                termination = ObservableT.Termination.None
        )
        Assert.assertEquals(expected, result)
    }
}
