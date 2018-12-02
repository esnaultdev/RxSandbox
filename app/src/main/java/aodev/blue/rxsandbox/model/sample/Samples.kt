package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.AsyncTree
import aodev.blue.rxsandbox.model.TimelineType


fun getSample(timelineType: TimelineType, operatorName: String): AsyncTree<Int>? {
    return when(timelineType) {
        TimelineType.OBSERVABLE -> getObservableSample(operatorName)
        TimelineType.SINGLE -> getSingleSample(operatorName)
        TimelineType.MAYBE -> getMaybeSample(operatorName)
        TimelineType.COMPLETABLE -> getCompletableSample(operatorName)
    }
}