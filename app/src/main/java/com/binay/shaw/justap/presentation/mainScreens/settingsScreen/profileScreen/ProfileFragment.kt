package com.binay.shaw.justap.presentation.mainScreens.settingsScreen.profileScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentProfileBinding
import com.binay.shaw.justap.utilities.ImageUtils
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel


class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private lateinit var localUser: LocalUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        initObservers()
        initialization()
        handleOperations()
//        updateAnalytics()

        return binding.root
    }

    private fun handleOperations() {
        binding.apply {
            include.leftIcon.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            editProfile.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_editProfileFragment)
            }

            profileImage.setOnClickListener {
                val imageUrl = localUser.userProfilePicture.toString()
                ImageUtils.showImagePreviewDialog(requireContext(), true, imageUrl, false).show()
            }

            profileBannerIV.setOnClickListener {
                val imageUrl = localUser.userBannerPicture.toString()
                ImageUtils.showImagePreviewDialog(requireContext(), false, imageUrl, false).show()
            }
        }
    }

    private fun initObservers() {
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userProfilePicture,
                it.userBannerPicture
            )
            binding.profileNameTV.text = localUser.userName
            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture.toString()
            val bannerURL = localUser.userBannerPicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            if (bannerURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileBannerIV, bannerURL)
        }
    }

//    private fun updateAnalytics() {
//        val userRef = Firebase.database.reference.child(Constants.users).child(Util.userID)
//            .child(Constants.analytics)
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // User exists in the database
//                    Util.log("Analytics exists")
//                    //Get the user
//                    if (dataSnapshot.child(Constants.scanCount).exists()) {
//                        val scanCount = dataSnapshot.child(Constants.scanCount).value.toString()
//                        if (scanCount.isNotEmpty()) {
//                            Util.log("Count scan: $scanCount")
//                            binding.scanCountTV.text = scanCount
//                        }
//                    } else {
//                        binding.scanCountTV.text = "0"
//                    }
//
//                    if (dataSnapshot.child(Constants.impressionCount).exists()) {
//                        val impressionCount =
//                            dataSnapshot.child(Constants.impressionCount).value.toString()
//                        if (impressionCount.isNotEmpty()) {
//                            Util.log("Count imp: $impressionCount")
//                            binding.impressionCountTV.text = impressionCount
//                        }
//                    } else {
//                        binding.impressionCountTV.text = "0"
//                    }
//
//                } else {
//                    // User does not exist in the database
//                    binding.scanCountTV.text = "0"
//                    binding.impressionCountTV.text = "0"
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Util.log("getUser:onCancelled ${error.toException()}")
//            }
//        })
//    }

    private fun initialization() {
//        (activity as MainActivity).supportActionBar?.hide()

        binding.include.apply {
            toolbarTitle.text = requireContext().resources.getString(R.string.Profile)
            leftIcon.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}