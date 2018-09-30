package aodev.blue.rxsandbox.model.operator

import aodev.blue.rxsandbox.model.Timeline


interface Operator<in I, out O, P, out R : Timeline<O>>  {

    fun params(input: List<Timeline<I>>): P?

    fun apply(params: P): R

    fun expression(): String
}