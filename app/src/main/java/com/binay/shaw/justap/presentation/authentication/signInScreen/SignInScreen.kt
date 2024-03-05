package com.binay.shaw.justap.presentation.authentication.signInScreen


import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.ActivitySignInScreenBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.presentation.authentication.ForgotPasswordScreen
import com.binay.shaw.justap.presentation.authentication.signUpScreen.SignUpScreen
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.viewModel.FirebaseViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel


class SignInScreen : BaseActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private val accountsViewModel by viewModels<AccountsViewModel> { ViewModelFactory() }
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private val firebaseViewModel by viewModels<FirebaseViewModel> { ViewModelFactory() }
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        loadLocate()
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        initViews()
        clickHandlers()
    }

    private fun clickHandlers() {
        binding.run {

            //Login Button onClick
            btnLogIn.progressButtonBg.setOnClickListener {
                login()
            }

            createAccount.setOnClickListener {
                startActivity(Intent(this@SignInScreen, SignUpScreen::class.java))
            }

            forgotPassword.setOnClickListener {
                startActivity(Intent(this@SignInScreen, ForgotPasswordScreen::class.java))
            }

            passwordToggle.setOnClickListener {
                togglePassword()
            }

        }
    }

    private fun login() {
        if (!Util.checkForInternet(this@SignInScreen)) {
            Util.showNoInternet(this@SignInScreen)
            return
        }
        showProgress()

        val email = binding.etEmail.text.toString().lowercase().trim()
        val password = binding.etPassword.text.toString().trim()

        val validation = Util.validateUserAuthInput(null, email, password)
        if (validation < 7) {
            stopProgress()
            handleErrorInput(validation)
            return
        }

        firebaseViewModel.logInUser(
            email, password
        )
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


    private fun initObservers() {
        firebaseViewModel.run {
            userLiveData.observe(this@SignInScreen) {
                fetchUserForLogIn()
            }
            errorLiveData.observe(this@SignInScreen) {
                stopProgress()
                binding.etPassword.setText("")
                Toast.makeText(
                    this@SignInScreen,
                    "Sign Failed: $it",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchUserForLogIn() {
        val user = firebaseViewModel.userLiveData.value
        val listAccounts = firebaseViewModel.accountsLiveData.value

        binding.progressAnimation.progressParent.visibility = View.GONE

        if (user != null) {
            localUserViewModel.insertUser(user)
            saveAccountsList(listAccounts)
        }

        val intent = Intent(this@SignInScreen, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveAccountsList(listAccounts: List<Accounts>?) {
        if (listAccounts != null) {
            for (singleAccount in listAccounts) {
                accountsViewModel.insertAccount(singleAccount)
            }
        }
    }

    private fun showProgress() {
        binding.apply {
            btnLogIn.apply {
                buttonText.visibility = View.GONE
                buttonProgress.visibility = View.VISIBLE
            }
            emailHelperTV.visibility = View.GONE
            passwordHelperTV.visibility = View.GONE
        }
    }

    private fun stopProgress() {
        binding.btnLogIn.apply {
            buttonText.visibility = View.VISIBLE
            buttonProgress.visibility = View.GONE
        }
    }

    private fun initViews() {
        binding.apply {
            include.toolbarTitle.text = getString(R.string.LogIn)
            btnLogIn.buttonText.text = getString(R.string.signin)
        }
    }
}