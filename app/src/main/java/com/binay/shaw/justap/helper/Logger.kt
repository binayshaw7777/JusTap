package com.binay.shaw.justap.helper

import android.util.Log
//import com.binay.shaw.justap.BuildConfig
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

object Logger {

    fun debugLog(tag: String?, msg: String?) {
//        if (BuildConfig.DEBUG) {
            Log.d(tag, msg!!)
//        }
    }

    fun Any.debugLog(tag: String? = "DEBUG_TAG") {
        Log.d(tag, toString())
    }

    fun debugLog(msg: String?) {
//        if (BuildConfig.DEBUG) {
            Log.d("Log", msg!!)
//        }
    }

    fun logException(tag: String, exception: Exception, logLevel: LogLevel, logToCrashlytics : Boolean = false) {
        when (logLevel) {
            LogLevel.DEBUG -> Log.d(tag, null, exception)
            LogLevel.ERROR -> Log.e(tag, null, exception)
            LogLevel.INFO -> Log.i(tag, null, exception)
            LogLevel.VERBOSE -> Log.v(tag, null, exception)
            LogLevel.WARN -> Log.w(tag, null, exception)
        }
        if (logToCrashlytics) {
            //TODO: send log to crashlytics like Firebase
            Firebase.crashlytics.log(exception.toString())
        }
    }

    enum class LogLevel {
        DEBUG, ERROR, INFO, VERBOSE, WARN
    }
}