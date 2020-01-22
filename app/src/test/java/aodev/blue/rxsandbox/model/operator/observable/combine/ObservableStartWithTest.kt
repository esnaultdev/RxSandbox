package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.inputOf
import org.junit.Assert
import org.junit.Test


class ObservableStartWithTest {

    private fun <T : Any> operator(value: T) = ObservableStartWith(value)

    @Test
    fun startWithNever() {
        // Given
        val input = ObservableT<Int>(emptyList(), ObservableT.Termination.None)

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
    fun startWithEmpty() {
        // Given
        val input = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(0f))

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
    fun startWithError() {
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
    fun startWithOtherEvents() {
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
    fun startWithBeforeFirstEvent() {
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
