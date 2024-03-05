package com.binay.shaw.justap.base


import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.binay.shaw.justap.utilities.DarkMode
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import java.util.Locale


abstract class BaseActivity : AppCompatActivity() {

    fun setTheme() {
        val trace: Trace = FirebasePerformance.getInstance().newTrace("Dark Mode Trace")
        trace.start()
        val darkModeEnabled = DarkMode.getDarkMode(this)
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        trace.stop()
    }

    fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocate(language)
        }
    }

    private fun setLocate(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.hide()
    }


    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
    }

    fun hideKeyboard() {
        val view: View? = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}