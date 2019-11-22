package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.functionOf
import aodev.blue.rxsandbox.model.inputOf
import org.junit.Assert
import org.junit.Test


class ObservableZipTest {

    private fun <T : Any, R : Any> operator(zipper: (List<T>) -> R): ObservableZip<T, R> {
        return ObservableZip(functionOf("zip", zipper))
    }

    @Test
    fun zipNeverSources() {
        // Given
        val source1 = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipNoSources() {
        // Given
        val inputs = emptyList<ObservableT<Int>>()

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT<Int>(emptyList(), ObservableT.Termination.None)
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun mergeOneSource() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val inputs = listOf(source1)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        Assert.assertEquals(source1, zipped)
    }

    @Test
    fun zipOneEmptyIgnoreOthers() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 1, 2f to 3, 7f to 4),
                termination = ObservableT.Termination.Error(8f)
        )
        val source2 = ObservableT.inputOf(
                events = emptyList<Pair<Float, Int>>(),
                termination = ObservableT.Termination.Complete(4f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        Assert.assertEquals(source2, zipped)
    }

    @Test
    fun zipEmptySources() {
        // Given
        val source1 = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(4f))
        val source2 = ObservableT<Int>(emptyList(), ObservableT.Termination.Complete(2f))
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        Assert.assertEquals(source2, zipped)
    }

    @Test
    fun zipStopAtError() {
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

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(4f to 5),
                termination = ObservableT.Termination.Error(5f)
        )
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipNeverComplete() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 2, 4f to 5),
                termination = ObservableT.Termination.Complete(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(7f to 7),
                termination = ObservableT.Termination.None
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(7f to 9),
                termination = ObservableT.Termination.None
        )
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipLastComplete() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 5, 1f to 2, 2f to 4),
                termination = ObservableT.Termination.Complete(2f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(7f to 7, 8f to 9),
                termination = ObservableT.Termination.Complete(9f)
        )
        val source3 = ObservableT.inputOf(
                events = listOf(2f to 3, 8f to 8),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2, source3)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(7f to 15, 8f to 19),
                termination = ObservableT.Termination.Complete(8f)
        )
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipCompleteEarlyAtValue() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 2, 2f to 2),
                termination = ObservableT.Termination.Complete(2f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(2f to 3, 4f to 4),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(2f to 5, 4f to 6),
                termination = ObservableT.Termination.Complete(4f)
        )
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipCompleteEarlyAtComplete() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 2),
                termination = ObservableT.Termination.Complete(5f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(4f to 3),
                termination = ObservableT.Termination.Complete(8f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(4f to 5),
                termination = ObservableT.Termination.Complete(5f)
        )
        Assert.assertEquals(expected, zipped)
    }

    @Test
    fun zipErrorInNonMinimalTimeline() {
        // Given
        val source1 = ObservableT.inputOf(
                events = listOf(0f to 2, 8f to 4),
                termination = ObservableT.Termination.Complete(8f)
        )
        val source2 = ObservableT.inputOf(
                events = listOf(2f to 3, 3f to 4, 4f to 5),
                termination = ObservableT.Termination.Error(5f)
        )
        val inputs = listOf(source1, source2)

        val operator = operator<Int, Int> { it.sum() }

        // When
        val zipped = operator.apply(inputs)

        // Then
        val expected = ObservableT.inputOf(
                events = listOf(2f to 5),
                termination = ObservableT.Termination.Error(5f)
        )
        Assert.assertEquals(expected, zipped)
    }
}
