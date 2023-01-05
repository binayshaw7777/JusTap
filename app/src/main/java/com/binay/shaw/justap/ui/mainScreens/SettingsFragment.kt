package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var settingsItemList: ArrayList<SettingsItem>
    private lateinit var settingsItemAdapter: SettingsItemAdapter
    private lateinit var localUserDatabase: LocalUserDatabase
    private lateinit var localUserViewModel: LocalUserViewModel
    private lateinit var logoutIV: ImageView
    private lateinit var feedback: ImageView
    private lateinit var localUser: LocalUser

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

//        localUserViewModel.name.observe(viewLifecycleOwner) {
//            binding.settingsUserName.text = it
//        }

        /**set List*/
        settingsItemList = ArrayList()

        settingsItemList.add(SettingsItem(1, R.drawable.edit_icon, "Edit profile", false))
        settingsItemList.add(SettingsItem(2, R.drawable.moon, "Dark mode", true))
        settingsItemList.add(
            SettingsItem(
                3,
                R.drawable.scanner_fullscale_icon,
                "Customize QR",
                false
            )
        )
        settingsItemList.add(SettingsItem(4, R.drawable.info_icon, "About us", false))
        settingsItemList.add(SettingsItem(5, R.drawable.help_icon, "Need help?", false))
        /**set find Id*/
        recyclerView = binding.settingsRV
        /**set Adapter*/
        settingsItemAdapter = SettingsItemAdapter(requireContext(), settingsItemList)
        /**setRecycler view Adapter*/
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = settingsItemAdapter


        binding.toProfile.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_settings_to_profileFragment)
        }

        logoutIV.setOnClickListener {
            logout()
        }

        feedback.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("mailto:binayshaw7777@gmail.com?Subject=JusTap%20Android%20application%20feedback%20main&Body=Your%20feedback%20is%20important%20to%20us%21")
            startActivity(openURL)
        }

        return binding.root
    }


    private fun initialization(container: ViewGroup?) {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "Settings"
        auth = FirebaseAuth.getInstance()
        localUserDatabase = Room.databaseBuilder(
            requireContext(), LocalUserDatabase::class.java,
            "localDB"
        ).build()
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
                it.userPhone,
                it.userProfilePicture,
                it.userBannerPicture
            )
            binding.settingsUserName.text = localUser.userName
            val profileURL = localUser.userProfilePicture.toString()
            if (profileURL.isNotEmpty())
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
        }

        logoutIV = binding.root.findViewById(R.id.rightIcon)
        logoutIV.setImageResource(R.drawable.logout_icon)
        logoutIV.visibility = View.VISIBLE
        feedback = binding.root.findViewById(R.id.leftIcon)
        feedback.setImageResource(R.drawable.feedback_icon)
        feedback.visibility = View.VISIBLE


    }


    private fun logout() {
        AwesomeDialog.build(requireActivity())
            .title(
                "Logout", ResourcesCompat.getFont(requireContext(), R.font.roboto_medium),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .body(
                "Are you sure you want to logout?",
                ResourcesCompat.getFont(requireContext(), R.font.roboto),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .background(R.drawable.card_drawable)
            .onPositive(
                "Logout",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.negative_red)
            ) {
                FirebaseAuth.getInstance().signOut()
                localUserViewModel.deleteUser()
                val intent = Intent(requireContext(), SignIn_Screen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(
                    intent
                ).also { requireActivity().finish() }
                Util.log("Logged out")
            }
            .onNegative(
                "Cancel",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.text_color)
            ) {
                Util.log("Logout cancelled")
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}