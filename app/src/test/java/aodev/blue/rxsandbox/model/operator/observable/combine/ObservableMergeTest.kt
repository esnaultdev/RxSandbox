package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.inputOf
import aodev.blue.rxsandbox.model.never
import org.junit.Assert
import org.junit.Test


class ObservableMergeTest {

    private val operator = ObservableMerge<Int>()

    @Test
    fun neverSources() {
        // Given
        val source1 = ObservableT.never<Int>()
        val source2 = ObservableT.never<Int>()
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun noSources() {
        // Given
        val inputs = emptyList<ObservableT<Int>>()

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun oneSource() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val inputs = listOf(source1)

        // When
        val result = operator.apply(inputs)

        // Then
        Assert.assertEquals(source1, result)
    }

    @Test
    fun stopAtError() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3),
                termination = ObservableT.Termination.Error(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(4f to 4, 6f to 6, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 4f to 4),
                termination = ObservableT.Termination.Error(5f)
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun neverComplete() {
        // Given
        val source1 = ObservableT.inputOf(
                events = emptyList<Pair<Float, Int>>(),
                termination = ObservableT.Termination.Complete(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(7f to 7),
                termination = ObservableT.Termination.None
        )
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(7f to 7),
                termination = ObservableT.Termination.None
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun lastComplete() {
        // Given
        val source1 = ObservableT.inputOf(
                events = emptyList<Pair<Float, Int>>(),
                termination = ObservableT.Termination.Complete(2f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(7f to 7),
                termination = ObservableT.Termination.Complete(9f)
        )
        val source3 = ObservableT.inputOf(
                events = listOf(2f to 3, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2, source3)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(2f to 3, 7f to 7, 8f to 8),
                termination = ObservableT.Termination.Complete(9f)
        )
        Assert.assertEquals(expected, result)
    }
}
