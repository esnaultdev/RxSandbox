package aodev.blue.rxsandbox.model


sealed class Termination {
    object Error : Termination()
    object Complete : Termination()
}