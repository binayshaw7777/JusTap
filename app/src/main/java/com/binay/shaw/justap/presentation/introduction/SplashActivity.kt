package com.binay.shaw.justap.presentation.introduction

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.presentation.introduction.onboarding.OnboardingScreen
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        makeIntent(Util.isUserLoggedIn())
    }

    private fun makeIntent(userLoggedIn: Boolean) {
        val intent = if (userLoggedIn) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else if (onBoardingScreenViewed()) {
            Intent(this@SplashActivity, SignInScreen::class.java)
        } else {
            Intent(this@SplashActivity, OnboardingScreen::class.java)
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            startActivity(intent)
            finish()
        }
    }

    private fun onBoardingScreenViewed(): Boolean {
        val pref =
            applicationContext.getSharedPreferences(Constants.onBoardingPref, MODE_PRIVATE)
        return pref.getBoolean(Constants.isIntroOpened, false)
    }
}