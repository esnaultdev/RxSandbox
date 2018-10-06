package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.Timeline


interface Operator<in T, out R>  {

    fun apply(input: List<Timeline<T>>): Timeline<R>?

    val expression: String
}