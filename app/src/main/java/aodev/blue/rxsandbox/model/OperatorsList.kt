package aodev.blue.rxsandbox.model

val observableOperators = listOf(
        OperatorCategory(
                name = "conditional",
                operatorNames = listOf(
                        "all",
                        "any"
                )
        ),
        OperatorCategory(
                name = "create",
                operatorNames = listOf(
                        "empty",
                        "interval",
                        "just",
                        "never",
                        "range",
                        "repeat",
                        "throw",
                        "timer"
                )
        ),
        OperatorCategory(
                name = "filter",
                operatorNames = listOf(
                        "debounce",
                        "distinct",
                        "distinctUntilChanged",
                        "elementAt",
                        "filter",
                        "first",
                        "ignoreElements",
                        "last",
                        "skip",
                        "skipLast",
                        "take",
                        "takeLast"
                )
        ),
        OperatorCategory(
                name = "transform",
                operatorNames = listOf(
                        "map"
                )
        ),
        OperatorCategory(
                name = "utility",
                operatorNames = listOf(
                        "delay"
                )
        )
)

val singleOperators = listOf(
        OperatorCategory(
                name = "utility",
                operatorNames = listOf(
                        "delay"
                )
        )
)

val maybeOperators = listOf(
        OperatorCategory(
                name = "utility",
                operatorNames = listOf(
                        "delay"
                )
        )
)

val completableOperators = listOf(
        OperatorCategory(
                name = "utility",
                operatorNames = listOf(
                        "delay"
                )
        )
)