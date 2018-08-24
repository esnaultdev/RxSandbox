package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.Timeline


interface Operator<in T, out R>  {

    fun apply(timeline: Timeline<T>): Timeline<R>
}
