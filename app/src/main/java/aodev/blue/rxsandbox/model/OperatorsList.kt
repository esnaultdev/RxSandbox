package aodev.blue.rxsandbox.model

val observableOperators = listOf(
        OperatorCategory(
                name = "custom",
                operatorNames = listOf(
                        "mapMerge"
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
                name = "transform",
                operatorNames = listOf(
                        "map",
                        "scan"
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
                name = "combine",
                operatorNames = listOf(
                        "combineLatest",
                        "merge",
                        "startWith",
                        "zip"
                )
        ),
        OperatorCategory(
                name = "utility",
                operatorNames = listOf(
                        "delay",
                        "timeout"
                )
        ),
        OperatorCategory(
                name = "conditional",
                operatorNames = listOf(
                        "all",
                        "amb",
                        "any",
                        "contains"
                )
        )
)

val singleOperators = listOf(
        OperatorCategory(
                name = "create",
                operatorNames = listOf(
                        "just"
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
