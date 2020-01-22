package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.empty
import aodev.blue.rxsandbox.model.inputOf
import aodev.blue.rxsandbox.model.never
import org.junit.Assert
import org.junit.Test


class ObservableContainsTest {

    private fun <T : Any> operator(element: T) = ObservableContains(element)

    @Test
    fun neverSource() {
        // Given
        val input = ObservableT.never<Int>()

        val operator = operator(5)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT<Boolean>(SingleT.Result.None())
        Assert.assertEquals(expected, result)
    }

    @Test
    fun emptySource() {
        // Given
        val input = ObservableT.empty<Int>(completeAt = 8f)

        val operator = operator(5)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(8f, false))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun foundOne() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 4),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(5)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun foundFirstOfMultiple() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(5)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun foundOneBeforeError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 7),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(5)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun notFound() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(7)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(8f, false))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun notFoundAndError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(7)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Error(8f))
        Assert.assertEquals(expected, result)
    }
}
