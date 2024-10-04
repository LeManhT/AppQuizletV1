package com.example.quizletappandroidv1

import android.app.Application
import android.content.Context
import com.airbnb.lottie.BuildConfig
import com.example.quizletappandroidv1.repository.quote.QuoteRepository
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
    }
}