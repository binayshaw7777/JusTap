package com.binay.shaw.justap.presentation.mainScreens.resultScreen

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.ResultItemAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentScanResultBinding
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.ImageUtils
import com.binay.shaw.justap.utilities.LinksUtils
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.presentation.mainScreens.historyScreen.LocalHistoryViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class ResultFragment : BaseFragment() {

    private val args: ResultFragmentArgs by navArgs()
    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ScanResultViewModel> { ViewModelFactory() }
    private lateinit var recyclerViewAdapter: ResultItemAdapter


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScanResultBinding.inflate(layoutInflater, container, false)
        initialization()

        binding.include.leftIcon.setOnClickListener {
            handleBackButtonPress()
        }

        val data = args.resultString
        Util.log("Scanned Result: $data")

        if (args.isResult && !data.isNullOrEmpty()) {
            setUpResultView(args.resultString!!)
        } else {
            setUpAboutMe()
        }

        return binding.root
    }

    private fun setUpAboutMe() {
        binding.apply {
            profileImage.setImageResource(R.drawable.aboutme_pfp)
            profileImage.setOnClickListener {
                ImageUtils.showImagePreviewDialog(requireContext(), true, null, true).show()
            }
            profileBannerIV.setImageResource(R.drawable.aboutme_banner)
            profileBannerIV.setOnClickListener {
                ImageUtils.showImagePreviewDialog(requireContext(), false, null, true).show()
            }
            profileNameTV.text = getString(R.string.BinayShaw)
            profileBioTV.text = getString(R.string.AboutMeDescription)
            downloadResume.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val download = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.resumeURL))
                    startActivity(download)
                }
            }
        }
        viewModel.getDevelopersAccount(resources.getStringArray(R.array.account_names))
        viewModel.showCaseAccountsListDevLiveData.observe(viewLifecycleOwner) {
            binding.progressAnimation.progressParent.visibility = View.GONE
            recyclerViewAdapter.clearData()
            recyclerViewAdapter.setData(it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUpResultView(resultString: String) {

        var profileImage: String? = null
        var bannerImage: String? = null

        val localUserHistoryViewModel = ViewModelProvider(
            this@ResultFragment,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalHistoryViewModel::class.java]

        viewModel.getDataFromUserID(resultString)
//        viewModel.updateAnalytics(resultString)

        viewModel.showCaseAccountsListLiveData.observe(viewLifecycleOwner) {
            recyclerViewAdapter.clearData()
            val showCaseAccountsList = ArrayList<Accounts>()
            for (accounts in it) {
                if (accounts.showAccount) {
                    showCaseAccountsList.add(accounts)
                }
            }
            recyclerViewAdapter.setData(showCaseAccountsList)
        }

        viewModel.scanResultUser.observe(viewLifecycleOwner) {

            binding.progressAnimation.progressParent.visibility = View.GONE

            createLocalHistory(it, localUserHistoryViewModel)

            val tempUser = it
            profileImage = tempUser.profilePictureURI.toString()
            bannerImage = tempUser.profileBannerURI.toString()


            binding.apply {
                profileNameTV.text = tempUser.name
                if (!tempUser.bio.isNullOrEmpty())
                    profileBioTV.text = tempUser.bio
                else
                    profileBioTV.visibility = View.GONE

                if (profileImage.isNullOrEmpty().not()) {
                    Util.loadImagesWithGlide(binding.profileImage, profileImage!!)
                }

                if (bannerImage.isNullOrEmpty().not()) {
                    Util.loadImagesWithGlide(binding.profileBannerIV, bannerImage!!)
                }
            }
        }

        binding.profileImage.setOnClickListener {
            ImageUtils.showImagePreviewDialog(requireContext(), true, profileImage, false).show()
        }
        binding.profileBannerIV.setOnClickListener {
            ImageUtils.showImagePreviewDialog(requireContext(), false, bannerImage, false).show()
        }

    }

    private fun createLocalHistory(user: User, localHistoryViewModel: LocalHistoryViewModel) {

        if (user.profilePictureURI.isNullOrEmpty().not()) {
            val myOptions = RequestOptions()
                .override(100, 100)
            Glide.with(requireContext())
                .asBitmap()
                .apply(myOptions)
                .encodeFormat(Bitmap.CompressFormat.PNG) // Set the output format to PNG
                .load(user.profilePictureURI)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Save the bitmap to your LocalHistory object
                        viewModel.saveLocalHistory(user, resource, localHistoryViewModel)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Do nothing
                    }
                })
        } else {
            viewModel.saveLocalHistory(user, null, localHistoryViewModel)
        }
    }

    private fun initialization() {
//        (activity as MainActivity).supportActionBar?.hide()
        binding.include.leftIcon.visibility = View.VISIBLE

        recyclerViewAdapter = ResultItemAdapter {
            LinksUtils.processData(it, requireContext())
        }
        binding.accountsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recyclerViewAdapter
        }
        binding.progressAnimation.apply {
            progressParent.visibility = View.VISIBLE
            progressText.text = resources.getString(R.string.PreparingResult)
        }

        if (args.isResult) {
            binding.include.toolbarTitle.text = resources.getString(R.string.ScanCompleted)
        } else {
            binding.include.toolbarTitle.text = resources.getString(R.string.AboutUs)
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                handleBackButtonPress()
                true
            } else false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun handleBackButtonPress() =
        requireActivity().onBackPressedDispatcher.onBackPressed()
}