package com.binay.shaw.justap.presentation.mainScreens.settingsScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.LanguageItemAdapter
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.databinding.LanguageModalBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.model.SettingsState
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.DarkMode
import com.binay.shaw.justap.utilities.LifeCyclePrinter
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.binay.shaw.justap.utilities.getLanguageItems
import com.binay.shaw.justap.utilities.setLocate
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsItemList: ArrayList<SettingsItem>
    private lateinit var settingsItemAdapter: SettingsItemAdapter
    private lateinit var localUserDatabase: LocalUserDatabase
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private lateinit var feedback: ImageView
    private lateinit var localUser: LocalUser
    private var isDarkModeEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        initObservers()
        initialization()
        handleOperations()
        lifecycle.addObserver(LifeCyclePrinter("LIFECYCLE"))

        binding.toProfile.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_settings_to_profileFragment)
        }

        feedback.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(requireContext().resources.getString(R.string.mailTo))
            startActivity(openURL)
        }

        return binding.root
    }

    private fun handleOperations() {
        setupSettingsOptions()
    }

    private fun setupSettingsOptions() {
        settingsItemList.apply {
            add(SettingsItem(0, R.drawable.edit_stroke, getString(R.string.edit_profile)))
            add(SettingsItem(1, R.drawable.scanner_icon, getString(R.string.customizeQR)))
            add(SettingsItem(2, R.drawable.info_icon, getString(R.string.AboutUs)))
            add(SettingsItem(3, R.drawable.help_icon, getString(R.string.need_help)))
            add(SettingsItem(4, R.drawable.dark_mode_icon, getString(R.string.dark_mode), true))
            add(SettingsItem(5, R.drawable.translate_icon, getString(R.string.language)))
            add(SettingsItem(6, R.drawable.rate_icon, getString(R.string.rate_justap)))
            add(SettingsItem(7, R.drawable.logout_icon, getString(R.string.LogoutTitle)))
        }

        settingsItemAdapter = SettingsItemAdapter(requireContext(), settingsItemList) {

            when (it) {
                SettingsState.getSettingsState(SettingsState.TO_EDIT_PROFILE) -> {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_settings_to_editProfileFragment)
                }

                SettingsState.getSettingsState(SettingsState.TO_CUSTOMIZE_QR) -> {
                    gotoCustomizeQR()
                }

                SettingsState.getSettingsState(SettingsState.TO_ABOUT_US) -> {
                    val action = SettingsFragmentDirections.actionSettingsToResultFragment(
                        resultString = null,
                        isResult = false
                    )
                    Navigation.findNavController(binding.root).navigate(action)
                }

                SettingsState.getSettingsState(SettingsState.TO_NEED_HELP) -> {
                    needHelp()
                }

                SettingsState.getSettingsState(SettingsState.TO_DARK_MODE) -> {
                    switchDarkMode()
                }

                SettingsState.getSettingsState(SettingsState.TO_TRANSLATE) -> {
                    showTranslationPrompt()
                }

                SettingsState.getSettingsState(SettingsState.TO_RATE_APP) -> {
                    openPlayStore()
                }

                SettingsState.getSettingsState(SettingsState.TO_LOGOUT) -> {
                    logout()
                }
            }
        }
        /**setRecycler view Adapter*/
        binding.settingsRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsItemAdapter
        }
    }

    private fun showTranslationPrompt() {
        val languageList = getLanguageItems()

        val dialog = LanguageModalBinding.inflate(layoutInflater)
        val bottomSheet = requireActivity().createBottomSheet()
        dialog.apply {
            val languageAdapter = LanguageItemAdapter(languageList) { selectedLanguage ->
                setLocate(selectedLanguage)
                requireActivity().recreate()
                bottomSheet.dismiss()
            }
            languageRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = languageAdapter
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }


    private fun switchDarkMode() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                DarkMode.setDarkMode(requireContext(), isDarkModeEnabled.not())
                withContext(Dispatchers.Main) {
                    requireActivity().recreate()
                }
            }
        } catch (e: Exception) {
            Util.log("Dark mode error: ${e.message}")
        }
    }

    private fun gotoCustomizeQR() {
        findNavController().navigate(R.id.action_settings_to_customizeQRFragment)
    }

    private fun initObservers() {
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            it?.let {
                createUser(it)
            }

            val name = Util.getFirstName(localUser.userName)
            binding.settingsUserName.text = name

            val profileURL = localUser.userProfilePicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
        }
    }

    private fun createUser(user: LocalUser) {
        localUser = LocalUser(
            user.userID,
            user.userName,
            user.userEmail,
            user.userBio,
            user.userProfilePicture,
            user.userBannerPicture
        )
    }

    private fun needHelp() {
        val dialog = ParagraphModalBinding.inflate(layoutInflater)
        val bottomSheet = requireActivity().createBottomSheet()
        dialog.apply {
            paragraphHeading.text = resources.getString(R.string.welcome_to_justap)
            paragraphContent.text = resources.getString(R.string.needHelpSettingsDescription)
            lottieAnimationLayout.apply {
                setAnimation(R.raw.help_lottie)
                visibility = View.VISIBLE
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun openPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(Constants.APP_URL)
        startActivity(intent)
    }


    @SuppressLint("SetTextI18n")
    private fun initialization() {

//        (activity as MainActivity).supportActionBar?.hide()
        binding.include.apply {
            toolbarTitle.text =
                requireContext().resources.getString(R.string.Settings)
            leftIcon.apply {
                feedback = this
                setImageResource(R.drawable.feedback_icon)
                visibility = View.VISIBLE
            }
        }
        localUserDatabase = Room.databaseBuilder(
            requireContext(), LocalUserDatabase::class.java,
            "localDB"
        ).build()

        settingsItemList = ArrayList()
        isDarkModeEnabled = DarkMode.getDarkMode(requireContext())
    }

    private fun logout() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.LogoutTitle)
            optionsContent.text =
                requireContext().resources.getString(R.string.LogoutDescription)
            positiveOption.text = requireContext().resources.getString(R.string.LogoutTitle)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )

            negativeOption.text = requireContext().resources.getString(R.string.LogoutCancel)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )

            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.clearDataAndLogout(lifecycleScope, requireContext())
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Logout cancelled")
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}