package com.example.quizletappandroidv1

import android.app.Application
import android.content.Context
import com.airbnb.lottie.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        var userId: String? = null
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("idUser", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("key_userid", null)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Optionally, set up a custom tree for release builds
//        else {
//            Timber.plant(ReleaseTree())
//        }

    }
}