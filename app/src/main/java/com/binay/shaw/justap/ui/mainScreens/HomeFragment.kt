package com.binay.shaw.justap.ui.mainScreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.binay.shaw.justap.R
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.databinding.FragmentHomeBinding
import com.binay.shaw.justap.viewModel.Home_ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Home_ViewModel
    private lateinit var userID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var localUserDatabase: LocalUserDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initialization(container)

        viewModel.getUser(localUserDatabase)

        viewModel.firstName.observe(viewLifecycleOwner) {
            binding.profileNameTV.text = "Hi, $it"
        }



        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "Home"
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        userID = auth.uid.toString()
        viewModel = ViewModelProvider(this@HomeFragment)[Home_ViewModel::class.java]
        localUserDatabase = Room.databaseBuilder(requireContext(), LocalUserDatabase::class.java,
        "localDB").build()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}