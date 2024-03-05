package com.binay.shaw.justap.presentation.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.ActivityForgotPasswordScreenBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.viewModel.FirebaseViewModel


class ForgotPasswordScreen : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordScreenBinding
    private val firebaseViewModel by viewModels<FirebaseViewModel> { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        initialization()
        handleClicks()
    }

    private fun initObservers() {
        firebaseViewModel.run {
            resetPasswordRequest.observe(this@ForgotPasswordScreen) {
                if (it) {
                    goBackToSignInScreen()
                }
            }
            errorLiveData.observe(this@ForgotPasswordScreen) {
                stopProgress()
                Toast.makeText(this@ForgotPasswordScreen, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goBackToSignInScreen() {
        stopProgress()
        Toast.makeText(this, R.string.email_sent, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@ForgotPasswordScreen, SignInScreen::class.java))
    }

    private fun handleClicks() {
        binding.btnResetPassword.progressButtonBg.setOnClickListener {
            resetPassword()
        }
        binding.include.leftIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun resetPassword() {
        if (!Util.checkForInternet(this)) {
            Util.showNoInternet(this)
            return
        }
        showProgress()
        val email = binding.etEmail.text.toString().trim()

        val validation = Util.validateUserAuthInput(null, email, null)
        if (validation < 7) {
            stopProgress()
            handleErrorInput(validation)
            return
        }

        firebaseViewModel.resetPassword(email)
    }

    private fun handleErrorInput(validationCode: Int) {
        binding.apply {
            when (validationCode) {
                2 -> {
                    emailHelperTV.text = getString(R.string.enter_email)
                    emailHelperTV.visibility = View.VISIBLE
                }
                3 -> {
                    emailHelperTV.text = getString(R.string.invalid_email)
                    emailHelperTV.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun showProgress() {
        binding.apply {
            emailHelperTV.visibility = View.GONE
            btnResetPassword.apply {
                buttonText.visibility = View.GONE
                buttonProgress.visibility = View.VISIBLE
            }
        }
    }

    private fun stopProgress() {
        binding.apply {
            btnResetPassword.apply {
                buttonText.visibility = View.VISIBLE
                buttonProgress.visibility = View.GONE
            }
        }
    }

    private fun initialization() {
        supportActionBar?.hide()
        binding.apply {
            include.apply {
                toolbarTitle.text = getString(R.string.forgot_password_title)
                leftIcon.visibility = View.VISIBLE
            }
            btnResetPassword.buttonText.text = resources.getString(R.string.SendResetLink)
        }
    }
}