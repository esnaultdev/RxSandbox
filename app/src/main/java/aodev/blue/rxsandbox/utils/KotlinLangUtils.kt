package aodev.blue.rxsandbox.utils

/**
 * Make a when exhaustive even when it's not used as an expression.
 */
val Any?.exhaustive get() = Unit