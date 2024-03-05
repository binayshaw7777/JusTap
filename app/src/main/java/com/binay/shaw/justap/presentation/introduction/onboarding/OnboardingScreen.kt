package com.binay.shaw.justap.presentation.introduction.onboarding


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseActivity
import com.binay.shaw.justap.databinding.ActivityOnboardingScreenBinding
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.google.android.material.tabs.TabLayout


class OnboardingScreen : BaseActivity() {

    private lateinit var binding: ActivityOnboardingScreenBinding
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLocate()
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemList: MutableList<ScreenItem> = getScreenItemList()
        val introViewPagerAdapter = IntroViewPagerAdapter(this, itemList)
        binding.screenViewpager.adapter = introViewPagerAdapter

        binding.tabIndicator.setupWithViewPager(binding.screenViewpager)

        binding.nextButton.setOnClickListener {
            position = binding.screenViewpager.currentItem
            if (position < itemList.size) {
                position++
                binding.screenViewpager.currentItem = position
            }
            if (position == itemList.size - 1) {
                // when we reach to the last screen
                loadLastScreen()
            }
        }

        binding.tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == itemList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })

        binding.getStarted.setOnClickListener {
            //open main activity
            savePrefsData()
            val login = Intent(applicationContext, SignInScreen::class.java)
            startActivity(login)
            finish()
        }

        binding.tvSkip.setOnClickListener {
            //open main activity
            binding.screenViewpager.currentItem = itemList.size
        }

    }

    private fun getScreenItemList(): MutableList<ScreenItem> {
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                getString(R.string.connect_with_ease),
                getString(R.string.exchange_contact_info_and_social_links_quickly_and_securely_with_justap_just_tap_scan_and_connect),
                R.drawable.first_onboarding_image
            )
        )
        mList.add(
            ScreenItem(
                getString(R.string.fast_and_efficient),
                getString(R.string.justap_is_the_quickest_and_most_efficient_way_to_exchange_details_with_others_just_tap_and_scan),
                R.drawable.second_onboarding_image
            )
        )
        mList.add(
            ScreenItem(
                getString(R.string.all_in_one_place),
                getString(R.string.keep_all_your_contact_info_and_social_links_in_one_convenient_location_with_justap_stay_organized_and_connected),
                R.drawable.third_onboarding_image
            )
        )
        return mList
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences(Constants.onBoardingPref, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(Constants.isIntroOpened, true)
        editor.apply()
    }

    // show the GET STARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        binding.nextButton.visibility = View.INVISIBLE
        binding.getStarted.visibility = View.VISIBLE
        binding.tvSkip.visibility = View.INVISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE
        binding.getStarted.animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
    }
}