package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.Timeline


interface Operator<in T, out R>  {

    fun apply(input: List<Timeline<T>>): Timeline<R>?

    val expression: String

    val docUrl: String? // Nullable because some operators are missing from the ReactiveX doc
}