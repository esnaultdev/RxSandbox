package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.inputOf
import aodev.blue.rxsandbox.model.never
import org.junit.Assert
import org.junit.Test


class ObservableMergeTest {

    private val operator = ObservableAmb<Int>()

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
    fun oneSourceWithValuesOneNever() {
        // Given
        val source1 = ObservableT.never<Int>()
        val source2 = ObservableT.inputOf(
                events = listOf(4f to 4, 6f to 6, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = source2
        Assert.assertEquals(expected, result)
    }

    @Test
    fun twoSourcesWithValues() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(5f to 5, 7f to 7),
                termination = ObservableT.Termination.None
        )
        val source2 = ObservableT.inputOf(
                events = listOf(2f to 4, 6f to 6, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = source2
        Assert.assertEquals(expected, result)
    }

    @Test
    fun oneSourceWithoutValuesCompletesBeforeOneWithValues() {
        // Given
        val source1 = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(0f))
        val source2 = ObservableT.inputOf(
                events = listOf(2f to 4, 6f to 6, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        // When
        val result = operator.apply(inputs)

        // Then
        val expected = source1
        Assert.assertEquals(expected, result)
    }
}