package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.functions.predicateOf
import aodev.blue.rxsandbox.model.inputOf
import org.junit.Assert
import org.junit.Test


class ObservableAnyTest {

    private fun <T : Any> operator(predicate: (T) -> Boolean): ObservableAny<T> {
        val expressiblePredicate = predicateOf("predicate", predicate)
        return ObservableAny(expressiblePredicate)
    }

    private val evenPredicate: (Int) -> Boolean = { it % 2 == 0 }

    @Test
    fun neverSource() {
        // Given
        val input = ObservableT<Int>(emptyList(), ObservableT.Termination.None)

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT<Boolean>(SingleT.Result.None())
        Assert.assertEquals(expected, result)
    }

    @Test
    fun emptySource() {
        // Given
        val input = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(8f))

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(8f, false))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun oneValueMatches() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 4, 7f to 7),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun firstMatchOfMultiple() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 4, 7f to 6),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun oneValueMatchesBeforeError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 4, 7f to 7),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun noValueMatchesAndComplete() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(8f, false))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun noValueMatchesAndNoCompletion() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.None
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.None())
        Assert.assertEquals(expected, result)
    }

    @Test
    fun noValueMatchesAndError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 1, 5f to 5, 7f to 5),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Error(8f))
        Assert.assertEquals(expected, result)
    }
}