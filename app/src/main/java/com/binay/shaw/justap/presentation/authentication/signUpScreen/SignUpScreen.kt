package com.binay.shaw.justap.presentation.authentication.signUpScreen

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.databinding.ActivitySignUpScreenBinding
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.binay.shaw.justap.viewModel.FirebaseViewModel


class SignUpScreen : BaseActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding
    private val firebaseViewModel by viewModels<FirebaseViewModel> { ViewModelFactory() }
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initObservers()
        clickHandlers()
    }

    private fun clickHandlers() {
        binding.run {

            //Sign Up Button
            btnCreateAccount.progressButtonBg.setOnClickListener {
                createAccount()
            }

            loginInstead.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            passwordToggle.setOnClickListener {
                togglePassword()
            }

        }
    }

    private fun createAccount() {
        if (!Util.checkForInternet(this@SignUpScreen)) {
            Util.showNoInternet(this@SignUpScreen)
            return
        }
        showProgress()

        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val validation = Util.validateUserAuthInput(name, email, password)
        if (validation < 7) {
            stopProgress()
            handleErrorInput(validation)
            return
        }

        firebaseViewModel.createNewAccount(
            name, email, password
        )
    }

    private fun initObservers() {
        firebaseViewModel.run {
            registerStatus.observe(this@SignUpScreen) {
                if (it)
                    startActivity(Intent(this@SignUpScreen, SignInScreen::class.java))
            }
            errorLiveData.observe(this@SignUpScreen) {
                stopProgress()
                Toast.makeText(
                    this@SignUpScreen,
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleErrorInput(validationCode: Int) {
        binding.apply {
            when (validationCode) {
                1 -> {
                    nameHelperTV.text = getString(R.string.enter_name)
                    nameHelperTV.visibility = View.VISIBLE
                }
                2 -> {
                    emailHelperTV.text = getString(R.string.enter_email)
                    emailHelperTV.visibility = View.VISIBLE
                }
                3 -> {
                    emailHelperTV.text = getString(R.string.invalid_email)
                    emailHelperTV.visibility = View.VISIBLE
                }
                4 -> {
                    passwordHelperTV.text = getString(R.string.enter_password)
                    passwordHelperTV.visibility = View.VISIBLE
                }
                5 -> {
                    passwordHelperTV.text = getString(R.string.small_size_password)
                    passwordHelperTV.visibility = View.VISIBLE
                }
                6 -> {
                    passwordHelperTV.text = getString(R.string.invalid_password)
                    passwordHelperTV.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun stopProgress() {
        binding.btnCreateAccount.apply {
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        }
    }

    private fun showProgress() {
        binding.apply {
            passwordHelperTV.visibility = View.GONE
            nameHelperTV.visibility = View.GONE
            emailHelperTV.visibility = View.GONE
            btnCreateAccount.apply {
                buttonText.visibility = View.GONE
                buttonProgress.visibility = View.VISIBLE
            }
        }
    }

    private fun togglePassword() {
        hideKeyboard()
        val showPasswordResId =
            if (isPasswordVisible) R.drawable.visibility_on else R.drawable.visibility_off
        isPasswordVisible = isPasswordVisible.not()
        val passwordTransMethod = if (isPasswordVisible) null else PasswordTransformationMethod()

        binding.passwordToggle.setImageResource(showPasswordResId)
        binding.etPassword.transformationMethod = passwordTransMethod
    }

    private fun initViews() {
        binding.apply {
            include.toolbarTitle.text = resources.getString(R.string.createAccount)
            btnCreateAccount.buttonText.text = resources.getString(R.string.createANewAccount)
        }
    }
}