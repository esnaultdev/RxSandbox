package aodev.blue.rxsandbox.model.functions


interface Function<T : Any, R : Any> : Expressible {
    fun apply(t: T): R
}

interface Function2<T1 : Any, T2 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2): R
}

interface Function3<T1 : Any, T2 : Any, T3 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3): R
}

interface Function4<T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4): R
}

interface Function5<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): R
}

interface Function6<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): R
}

interface Function7<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): R
}

interface Function8<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): R
}

interface Function9<T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, T9 : Any, R : Any> : Expressible {
    fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): R
}

interface Predicate<T : Any> : Expressible {
    fun test(t: T): Boolean
}

inline fun <T : Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t: T) -> R
) : Function<T, R> {
    return object : Function<T, R> {
        override fun apply(t: T): R {
            return block(t)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2) -> R
) : Function2<T1, T2, R> {
    return object : Function2<T1, T2, R> {
        override fun apply(t1: T1, t2: T2): R {
            return block(t1, t2)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3) -> R
) : Function3<T1, T2, T3, R> {
    return object : Function3<T1, T2, T3, R> {
        override fun apply(t1: T1, t2: T2, t3: T3): R {
            return block(t1, t2, t3)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4) -> R
) : Function4<T1, T2, T3, T4, R> {
    return object : Function4<T1, T2, T3, T4, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4): R {
            return block(t1, t2, t3, t4)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, T5 : Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4, t5: T5) -> R
) : Function5<T1, T2, T3, T4, T5, R> {
    return object : Function5<T1, T2, T3, T4, T5, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): R {
            return block(t1, t2, t3, t4, t5)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6) -> R
) : Function6<T1, T2, T3, T4, T5, T6, R> {
    return object : Function6<T1, T2, T3, T4, T5, T6, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): R {
            return block(t1, t2, t3, t4, t5, t6)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7: Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7) -> R
) : Function7<T1, T2, T3, T4, T5, T6, T7, R> {
    return object : Function7<T1, T2, T3, T4, T5, T6, T7, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): R {
            return block(t1, t2, t3, t4, t5, t6, t7)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7: Any, T8: Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8 : T8) -> R
) : Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
    return object : Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): R {
            return block(t1, t2, t3, t4, t5, t6, t7, t8)
        }

        override val expression: String = expression
    }
}

inline fun <T1 : Any, T2: Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7: Any, T8: Any, T9: Any, R : Any> functionOf(
        expression: String,
        crossinline block: (t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8 : T8, t9: T9) -> R
) : Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
    return object : Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
        override fun apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): R {
            return block(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        }

        override val expression: String = expression
    }
}

inline fun <T : Any> predicateOf(
        expression: String,
        crossinline block: (t: T) -> Boolean
): Predicate<T> {
    return object : Predicate<T> {
        override fun test(t: T): Boolean {
            return block(t)
        }

        override val expression: String = expression
    }
}