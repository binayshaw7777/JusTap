package com.binay.shaw.justap


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binay.shaw.justap.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var timer = 0L
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        //Hides action bar
        supportActionBar?.hide()


        val bottomNavigationView = binding.bottomNav
        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.home, R.id.scanner, R.id.history, R.id.settings))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setupWithNavController2(navController)

    }

    fun BottomNavigationView.setupWithNavController2(navController: NavController) {
        val bottomNavigationView = this
        bottomNavigationView.setOnItemReselectedListener { item ->
            // Pop everything up to the reselected item
            val reselectedDestinationId = item.itemId
            navController.popBackStack(reselectedDestinationId, false)
        }
    }

    override fun onBackPressed() {

        val navController: NavController = findNavController(R.id.fragmentContainerView)
        val count = navController.backQueue.size

        if (count <= 2) {
            if (timer + 2000L > System.currentTimeMillis()) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(
                    applicationContext, "Press once again to exit!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            timer = System.currentTimeMillis()
        } else {
            navController.popBackStack()
        }
    }
}
















//        binding.logout.setOnClickListener {
//            auth.signOut()
//            startActivity(Intent(this@MainActivity, SignIn_Screen::class.java)).also { finish() }
//        }
//
//        if (Util.isDarkMode(baseContext)) {
//            binding.UIModeSwitch.isChecked = true
//        }
//
//        binding.UIModeSwitch.setOnClickListener {
//            switchTheme()
//        }
//
//    }
//
//    private fun switchTheme() {
//        sharedPreferences = getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("FIRST_TIME", false)
//        if (Util.isDarkMode(baseContext)) {
//            editor.putBoolean("DARK_MODE", false)
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        } else {
//            editor.putBoolean("DARK_MODE", true)
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        }
//        editor.apply()
//    }