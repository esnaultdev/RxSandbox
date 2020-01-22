package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.functions.predicateOf
import aodev.blue.rxsandbox.model.inputOf
import org.junit.Assert
import org.junit.Test


class ObservableAllTest {

    private fun <T : Any> operator(predicate: (T) -> Boolean): ObservableAll<T> {
        val expressiblePredicate = predicateOf("predicate", predicate)
        return ObservableAll(expressiblePredicate)
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
        val expected = SingleT(SingleT.Result.Success(8f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun allValuesMatchAndComplete() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 2, 5f to 12, 7f to 4),
                termination = ObservableT.Termination.Complete(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(8f, true))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun allValuesMatchAndNoTermination() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 2, 5f to 12, 7f to 4),
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
    fun allValuesMatchAndError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 2, 5f to 12, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Error(8f))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun atLeastOneValueDoesNotMatch() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 2, 5f to 11, 7f to 4),
                termination = ObservableT.Termination.None
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, false))
        Assert.assertEquals(expected, result)
    }

    @Test
    fun atLeastOneValueDoesNotMatchBeforeError() {
        // Given
        val input = ObservableT.inputOf(
                events = listOf(0f to 2, 5f to 11, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )

        val operator = operator(evenPredicate)

        // When
        val result = operator.apply(input)

        // Then
        val expected = SingleT(SingleT.Result.Success(5f, false))
        Assert.assertEquals(expected, result)
    }
}