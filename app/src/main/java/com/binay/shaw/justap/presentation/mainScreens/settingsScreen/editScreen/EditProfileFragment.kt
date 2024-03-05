package com.binay.shaw.justap.presentation.mainScreens.settingsScreen.editScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.databinding.FragmentEditProfileBinding
import com.binay.shaw.justap.databinding.MyToolbarBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.utilities.ImageUtils
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.P)
class EditProfileFragment : BaseFragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolBar: MyToolbarBinding
    private lateinit var localUserViewModel: LocalUserViewModel
    private var profilePictureURI: Uri? = null
    private var profileBannerURI: Uri? = null
    private lateinit var localUser: LocalUser
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var editProfileViewModel: EditProfileViewModel
    private var editImageMode = 0   // 0 - Default, 1 - Profile picture, 2 - Banner picture


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        initialization()

        toolBar.leftIcon.setOnClickListener {
            handleBackButtonPress()
        }

        binding.cancelChanges.setOnClickListener {
            handleBackButtonPress()
        }

        binding.confirmChanges.setOnClickListener {

            val inputName = binding.newNameET.text.toString().trim()
            val inputBio = binding.newBioET.text.toString().trim()

            if (!Util.checkForInternet(requireContext())) {
                Util.showNoInternet(requireActivity())
                return@setOnClickListener
            }
            editChanges(inputName, inputBio, profilePictureURI, profileBannerURI)
        }

        binding.editBanner.setOnClickListener {
            editImageMode = 2
            ImagePicker.with(this)
                .compress(1024)
                .crop()
                .createIntent {
                    binding.bannerProgressBar.visibility = View.VISIBLE
                    imagePickerActivityResult.launch(it)
                }
        }

        binding.editPFP.setOnClickListener {
            editImageMode = 1
            ImagePicker.with(this)
                .compress(1024)
                .cropSquare()
                .createIntent {
                    binding.avatarProgressBar.visibility = View.VISIBLE
                    imagePickerActivityResult.launch(it)
                }
        }

        return binding.root
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

    private fun handleBackButtonPress() {
        val inputName = binding.newNameET.text.toString().trim()
        val inputBio = binding.newBioET.text.toString().trim()

        if (inputName.isNotEmpty() || inputBio.isNotEmpty()
            || profileBannerURI != null || profilePictureURI != null
        ) {

            val dialog = OptionsModalBinding.inflate(layoutInflater)
            val bottomSheet = requireContext().createBottomSheet()
            dialog.apply {

                optionsHeading.text = requireContext().resources.getString(R.string.DiscardChanged)
                optionsContent.text =
                    requireContext().resources.getString(R.string.DiscardChangedDescription)
                positiveOption.text = requireContext().resources.getString(R.string.Discard)
                positiveOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.negative
                    )
                )
                negativeOption.text = requireContext().resources.getString(R.string.ContinueEditing)
                negativeOption.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color
                    )
                )
                positiveOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Go back")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                negativeOption.setOnClickListener {
                    bottomSheet.dismiss()
                    Util.log("Stay")
                }
            }
            dialog.root.setBottomSheet(bottomSheet)
        } else
            requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun editChanges(
        inputName: String,
        inputBio: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?,
    ) {

        val originalID = localUser.userID
        val originalEmail = localUser.userEmail
        val originalName = localUser.userName
        val originalBio = localUser.userBio
        var originalPFP = localUser.userProfilePicture
        var originalBanner = localUser.userBannerPicture

        if (isInvalidData(inputName, inputBio, profilePictureURI, profileBannerURI))
            return

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmChanges)
            optionsContent.text =
                requireContext().resources.getString(R.string.ConfirmChangesDescription)
            positiveOption.text = requireContext().resources.getString(R.string.SaveChanges)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )
            negativeOption.text = requireContext().resources.getString(R.string.DontSave)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                binding.progressAnimation.progressParent.visibility = View.VISIBLE

                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                Util.log("Save")

                val hashMap: MutableMap<String, Any> = HashMap()
                hashMap["userID"] = originalID
                hashMap["email"] = originalEmail

                if (inputName.isNotEmpty())
                    hashMap["name"] = inputName
                else hashMap["name"] = originalName

                if (inputBio.isNotEmpty())
                    hashMap["bio"] = inputBio
                else if (originalBio?.isNotEmpty() == true)
                    hashMap["bio"] = originalBio
                else
                    hashMap["bio"] = ""

                if (originalPFP.isNullOrEmpty())
                    originalPFP = ""

                if (originalBanner.isNullOrEmpty()) {
                    originalBanner = ""
                }

                lifecycleScope.launch {
                    editProfileViewModel.updateUser(
                        firebaseDatabase, storageRef, originalID,
                        hashMap, originalPFP!!, originalBanner!!, profilePictureURI,
                        profileBannerURI, localUserViewModel
                    )
                }

                editProfileViewModel.status.observe(viewLifecycleOwner) {
                    if (it == 3) {
                        Glide.get(requireContext()).clearMemory()
                        binding.progressAnimation.progressParent.visibility = View.GONE
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        showAlerter(
                            resources.getString(R.string.profile_updated),
                            resources.getString(R.string.profile_updated_description),
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.positive
                            ),
                            R.drawable.check,
                            2500L
                        )

                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Don't Save")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)

    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val bitmap =
                        ImageUtils.getBitmapFromFileUri(requireContext().contentResolver, fileUri)
                    Util.log("Image Uri is: $fileUri && Bitmap is: $bitmap")
                    if (editImageMode == 1) {
                        profilePictureURI = fileUri
                        binding.profileImage.setImageBitmap(bitmap)

                    } else if (editImageMode == 2) {
                        profileBannerURI = fileUri
                        binding.bannerProgressBar.visibility = View.GONE
                        binding.profileBannerIV.setImageBitmap(bitmap)
                        binding.profileBannerIV.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    clearImageRequestOperations()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                    clearImageRequestOperations()

                }
                else -> {
//                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
//                    clearImageRequestOperations()
                }
            }
        }

    private fun clearImageRequestOperations() {
        if (editImageMode == 1)
            binding.avatarProgressBar.visibility = View.GONE
        else if (editImageMode == 2)
            binding.bannerProgressBar.visibility = View.GONE
        editImageMode = 0
    }


    private fun isInvalidData(
        inputName: String,
        inputBio: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?
    ): Boolean {
        if (inputName.isEmpty() && inputBio.isEmpty() && (profilePictureURI == null ||
                    profilePictureURI.toString()
                        .isEmpty()) && (profileBannerURI == null || profileBannerURI.toString()
                .isEmpty())
        ) {
            Toast.makeText(requireContext(), "Make changes to update", Toast.LENGTH_SHORT).show()
            return true
        }

        return false
    }


    private fun initialization() {
//        (activity as MainActivity).supportActionBar?.hide()

        toolBar = binding.include
        toolBar.toolbarTitle.text = requireContext().resources.getString(R.string.EditProfile)
        toolBar.leftIcon.visibility = View.VISIBLE
        localUserViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[LocalUserViewModel::class.java]
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userProfilePicture,
                it.userBannerPicture
            )
            binding.apply {
                newNameET.hint = localUser.userName
                newBioET.apply {
                    hint = localUser.userBio
                    isSingleLine = false
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    maxLines = 3
                    gravity = Gravity.START or Gravity.TOP
                }
            }
            val profileURL = localUser.userProfilePicture.toString()
            val bannerURL = localUser.userBannerPicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            if (bannerURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileBannerIV, bannerURL)
        }
        storageRef = Firebase.storage.reference
        firebaseDatabase = FirebaseDatabase.getInstance()
        editProfileViewModel =
            ViewModelProvider(this@EditProfileFragment)[EditProfileViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}