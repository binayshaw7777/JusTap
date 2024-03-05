package com.binay.shaw.justap.presentation.mainScreens.homeScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.AccountsItemAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentHomeBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.utilities.ImageUtils
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.presentation.mainScreens.homeScreen.accountFragments.AddEditViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel


@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private val accountsViewModel by viewModels<AccountsViewModel> { ViewModelFactory() }
    private lateinit var localUser: LocalUser
    private var accountsList = mutableListOf<Accounts>()
    private lateinit var recyclerViewAdapter: AccountsItemAdapter
    private val addEditViewModel by viewModels<AddEditViewModel> { ViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        initObservers()
        initialization()
        clickHandlers()

        return binding.root
    }

    private fun initObservers() {
        addEditViewModel.updateStatus.observe(viewLifecycleOwner) {
            if (it == 3) {
                Toast.makeText(requireContext(), resources.getString(R.string.data_added_successfully), Toast.LENGTH_SHORT).show()
                addEditViewModel.updateStatus.value = 0
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            localUser = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userProfilePicture,
                it.userBannerPicture
            )
            Util.userID = it.userID

            val name = Util.getFirstName(localUser.userName)
            binding.profileNameTV.text = "Hi $name,"

            binding.profileBioTV.text = localUser.userBio
            val profileURL = localUser.userProfilePicture!!
            if (profileURL.isNotEmpty()) {
                Util.loadImagesWithGlide(binding.profileImage, profileURL)
            }
        }

        accountsViewModel.getAllUser.observe(viewLifecycleOwner) {
            Util.log(it.toString())
            updateEmptyState(it)
            accountsList.clear()
            accountsList.addAll(it)
            recyclerViewAdapter.setData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun updateEmptyState(accountList: List<Accounts>?) {
        if (accountList.isNullOrEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.emptyState.visibility = View.GONE
        }
    }

    private fun clickHandlers() {
        binding.apply {
            fabLayout.setOnClickListener {
                gotoAddAccountFragment()
            }

            fabCircle.setOnClickListener {
                gotoAddAccountFragment()
            }

            include.rightIcon.setOnClickListener {
                showAccountInfoDialog()
            }
            profileImage.setOnClickListener {
                val imageUrl = localUser.userProfilePicture.toString()
                ImageUtils.showImagePreviewDialog(requireContext(), true, imageUrl, false).show()
            }
        }
    }

    private fun showAccountInfoDialog() {
        val dialog = ParagraphModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {
            paragraphHeading.text = resources.getString(R.string.HowToUse)
            paragraphContent.text = resources.getString(R.string.HowToUseDescription)
            paragraphImageView.apply {
                setImageResource(R.drawable.account_item_info)
                visibility = View.VISIBLE
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    private fun gotoAddAccountFragment() {
        getUnusedAccountsList()
        if (Util.unusedAccounts.size == 0) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.youve_used_all_accounts),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val action = HomeFragmentDirections.actionHomeToAddEditFragment(0)
        findNavController().navigate(action)
        binding.fabLayout.visibility = View.GONE
    }

    private fun getUnusedAccountsList() {
        val unusedAccounts = mutableListOf<String>()
        val usedAccounts = mutableListOf<String>()

        accountsViewModel.getAllUser.observe(viewLifecycleOwner) {
            val allAccounts = resources.getStringArray(R.array.account_names)
            for (l in it) {
                usedAccounts.add(l.accountName)
            }

            for (account in allAccounts) {
                if (!usedAccounts.contains(account))
                    unusedAccounts.add(account)
            }
        }
        Util.unusedAccounts = unusedAccounts
    }

    private fun initialization() {
        binding.include.apply {
            toolbarTitle.text = resources.getString(R.string.Home)
            rightIcon.apply {
                setImageResource(R.drawable.info_icon)
                visibility = View.VISIBLE
            }
        }

        recyclerViewAdapter = AccountsItemAdapter(requireActivity()) { newAccount ->
            for (account in accountsList) {
                if (account.accountID == newAccount.accountID) {
                    val currentState = account.showAccount
                    account.showAccount = currentState.not()

                    addEditViewModel.updateEntry(
                        accountsViewModel,
                        account
                    )
                }
            }
        }

        binding.accountsRv.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

//        handleOnScrollFAB()
    }

//    private fun handleOnScrollFAB() {
//        binding.accountsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && binding.fabText.visibility == View.VISIBLE) {
//                    binding.fabText.visibility = View.GONE
//                } else if (dy < 0 && binding.fabText.visibility != View.VISIBLE) {
//                    binding.fabText.visibility = View.VISIBLE
//                }
//            }
//        })
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        recyclerViewAdapter.notifyDataSetChanged()
    }
}