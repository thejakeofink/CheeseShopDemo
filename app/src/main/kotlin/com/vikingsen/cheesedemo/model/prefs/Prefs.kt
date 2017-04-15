package com.vikingsen.cheesedemo.model.prefs


import android.content.SharedPreferences
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs
@Inject constructor(private val preferences: SharedPreferences) {

    val httpLoggingLevel: HttpLoggingInterceptor.Level
        get() {
            val logLevelAsString = preferences.getString(PREF_HTTP_LOG_LEVEL, HttpLoggingInterceptor.Level.BASIC.name)
            return HttpLoggingInterceptor.Level.valueOf(logLevelAsString)
        }

    companion object {

        private val PREF_HTTP_LOG_LEVEL = "dev_http_log_level"
    }
}
