package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline


object Input {

    object None {
        inline fun <T, R> from(input: List<Timeline<T>>, block: () -> Timeline<R>): Timeline<R>? {
            return if (input.isEmpty()) block() else null
        }
    }

    object Observable {
        inline fun <T, R> from(
                input: List<Timeline<T>>,
                block: (observable: ObservableT<T>) -> Timeline<R>
        ): Timeline<R>? {
            return if (input.size != 1) {
                null
            } else {
                val first = input[0]
                if (first is ObservableT) {
                    block(first)
                } else {
                    null
                }
            }
        }
    }

    object Single {
        inline fun <T, R> from(
                input: List<Timeline<T>>,
                block: (single: SingleT<T>) -> Timeline<R>
        ): Timeline<R>? {
            return if (input.size != 1) {
                null
            } else {
                val first = input[0]
                if (first is SingleT) {
                    block(first)
                } else {
                    null
                }
            }
        }
    }

    object Maybe {
        inline fun <T, R> from(
                input: List<Timeline<T>>,
                block: (maybe: MaybeT<T>) -> Timeline<R>
        ): Timeline<R>? {
            return if (input.size != 1) {
                null
            } else {
                val first = input[0]
                if (first is MaybeT) {
                    block(first)
                } else {
                    null
                }
            }
        }
    }

    object Completable {
        inline fun <T, R> from(
                input: List<Timeline<T>>,
                block: (completable: CompletableT) -> Timeline<R>
        ): Timeline<R>? {
            return if (input.size != 1) {
                null
            } else {
                val first = input[0]
                if (first is CompletableT) {
                    block(first)
                } else {
                    null
                }
            }
        }
    }
}