package com.binay.shaw.justap.utilities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import com.binay.shaw.justap.R
import com.binay.shaw.justap.model.Language
import java.util.Locale


fun Fragment.setLocate(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)

    val config = Configuration()
    config.locale = locale
    requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

    val editor = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
    editor.putString("My_Lang", lang)
    editor.apply()
}

fun Activity.setLocate(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)

    val config = Configuration()
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)

    val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
    editor.putString("My_Lang", lang)
    editor.apply()
}

fun getLanguageItems() : List<Language> {
    return listOf(
        Language("English", "en", R.drawable.us_flag),
        Language("हिंदी", "hi", R.drawable.in_flag)
    )
}