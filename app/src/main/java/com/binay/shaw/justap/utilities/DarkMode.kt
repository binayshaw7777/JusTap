package com.binay.shaw.justap.utilities

import android.content.Context


object DarkMode {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    fun getDarkMode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
}
