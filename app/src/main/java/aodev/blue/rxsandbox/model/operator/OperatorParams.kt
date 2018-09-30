package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline


object ParamsNone {
    fun <T> fromInput(input: List<Timeline<T>>): ParamsNone? {
        return if (input.isEmpty()) {
            ParamsNone
        } else {
            null
        }
    }
}


class ParamsObservable<T>(
        val observable: ObservableT<T>
) {
    companion object {
        fun <T> fromInput(input: List<Timeline<T>>): ParamsObservable<T>? {
            return if (input.size != 1) {
                null
            } else {
                val timeline = input[0]
                if (timeline is ObservableT) {
                    ParamsObservable(timeline)
                } else {
                    null
                }
            }
        }
    }
}


class ParamsSingle<T>(
        val single: SingleT<T>
) {
    companion object {
        fun <T> fromInput(input: List<Timeline<T>>): ParamsSingle<T>? {
            return if (input.size != 1) {
                null
            } else {
                val timeline = input[0]
                if (timeline is SingleT) {
                    ParamsSingle(timeline)
                } else {
                    null
                }
            }
        }
    }
}


class ParamsMaybe<T>(
        val maybe: MaybeT<T>
) {
    companion object {
        fun <T> fromInput(input: List<Timeline<T>>): ParamsMaybe<T>? {
            return if (input.size != 1) {
                null
            } else {
                val timeline = input[0]
                if (timeline is MaybeT) {
                    ParamsMaybe(timeline)
                } else {
                    null
                }
            }
        }
    }
}


class ParamsCompletable(
        val completable: CompletableT
) {
    companion object {
        fun fromInput(input: List<Timeline<Unit>>): ParamsCompletable? {
            return if (input.size != 1) {
                null
            } else {
                val timeline = input[0]
                if (timeline is CompletableT) {
                    ParamsCompletable(timeline)
                } else {
                    null
                }
            }
        }
    }
}
