package aodev.blue.rxsandbox.ui

import android.app.Application
import android.os.Looper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers


class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        // Setup the main thread scheduler as async
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
    }
}