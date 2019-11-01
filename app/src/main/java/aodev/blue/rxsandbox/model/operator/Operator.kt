package aodev.blue.rxsandbox.model.operator


interface Operator {

    val expression: String

    val docUrl: String? // Nullable because some operators are missing from the ReactiveX doc

    // TODO Include a description of the operator to display it in a bottom sheet
}
