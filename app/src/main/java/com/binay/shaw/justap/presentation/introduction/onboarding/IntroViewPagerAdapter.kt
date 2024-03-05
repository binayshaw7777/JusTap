package com.binay.shaw.justap.presentation.introduction.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.PagerAdapter
import com.binay.shaw.justap.databinding.OnboardingLayoutBinding

class IntroViewPagerAdapter(var mContext: Context, var mListScreen: List<ScreenItem>) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding =
            OnboardingLayoutBinding.inflate(LayoutInflater.from(mContext), container, false)
        binding.introTitle.text = mListScreen[position].title
        binding.introDescription.text = mListScreen[position].description
        binding.introImg.setImageResource(mListScreen[position].screenImg)
        container.addView(binding.root)
        return binding.root
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}