package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.functionOf
import aodev.blue.rxsandbox.model.inputOf
import org.junit.Assert
import org.junit.Test


class ObservableCombineLatestTest {

    private fun <T : Any, R : Any> operator(combiner: (List<T>) -> R): ObservableCombineLatest<T, R> {
        return ObservableCombineLatest(functionOf("combiner", combiner))
    }

    @Test
    fun combineEmptySources() {
        // Given
        val source1 = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, combined)
    }

    @Test
    fun combineNoSources() {
        // Given
        val inputs = emptyList<ObservableT<Int>>()

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, combined)
    }

    @Test
    fun combineOneSource() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val inputs = listOf(source1)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        Assert.assertEquals(source1, combined)
    }

    @Test
    fun combineMultipleSourcesOneEmpty() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(0f))
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        Assert.assertEquals(source2, combined)
    }

    @Test
    fun combineMultipleSourcesOneNever() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.Error(8f))
        Assert.assertEquals(expected, combined)
    }

    @Test
    fun combineCompleteBeforeError() {
        // Given
        val source1 = ObservableT<Int>(emptyList(), ObservableT.Termination.Error(1f))
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(0f))
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        Assert.assertEquals(source2, combined)
    }

    @Test
    fun combineValues() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 4f to 4),
                termination = ObservableT.Termination.Complete(10f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(1f to 2, 5f to 3, 7f to 4),
                termination = ObservableT.Termination.Complete(7f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(1f to 3, 2f to 5, 4f to 6, 5f to 7, 7f to 8),
                termination = ObservableT.Termination.Complete(10f)
        )
        Assert.assertEquals(expected, combined)
    }

    @Test
    fun combineStopAtError() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 4f to 4),
                termination = ObservableT.Termination.Error(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(1f to 2, 6f to 3),
                termination = ObservableT.Termination.Complete(7f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(1f to 3, 4f to 6),
                termination = ObservableT.Termination.Error(5f)
        )
        Assert.assertEquals(expected, combined)
    }

    @Test
    fun combineValuesOnlyOneValueAfterError() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1),
                termination = ObservableT.Termination.Error(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(6f to 2),
                termination = ObservableT.Termination.Complete(7f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val combined = operator.apply(inputs)

        // Then
        val expected = ObservableT(emptyList(), ObservableT.Termination.Error(5f))
        Assert.assertEquals(expected, combined)
    }
}
