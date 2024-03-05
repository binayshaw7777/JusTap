package com.binay.shaw.justap.presentation.mainScreens.homeScreen.accountFragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentAddEditBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import com.binay.shaw.justap.utilities.Validator.Companion.isValidPhone
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.binay.shaw.justap.model.Accounts


class AddEditFragment : BaseFragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditFragmentArgs by navArgs()
    private val viewModel by viewModels<AddEditViewModel> { ViewModelFactory() }
    private val accountsViewModel by viewModels<AccountsViewModel> { ViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddEditBinding.inflate(layoutInflater, container, false)

        initialization()
        initObservers()
        clickHandlers()

        return binding.root
    }

    private fun initObservers() {
        viewModel.run {

            updateStatus.observe(viewLifecycleOwner) { status ->
                if (status == 3) {
                    Util.log("Status value = $status")
                    updateStatus.postValue(0)

                    showAlerter(
                        resources.getString(R.string.data_updated_successfully),
                        "",
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.positive
                        ),
                        R.drawable.check,
                        800L
                    )

                    binding.progressAnimation.progressParent.visibility = View.GONE
                    findNavController().navigateUp()
                }
            }

            deleteStatus.observe(viewLifecycleOwner) { status ->
                if (status == 3) {
                    Util.log("Status value = $status")
                    deleteStatus.postValue(0)

                    showAlerter(
                        resources.getString(R.string.data_deleted_successfully),
                        "",
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.negative
                        ),
                        R.drawable.delete,
                        800L
                    )

                    binding.progressAnimation.progressParent.visibility = View.GONE
                    findNavController().navigateUp()
                }
            }

            saveStatus.observe(viewLifecycleOwner) {
                if (it == 3) {
                    saveStatus.value = 0
                    //Success
                    showAlerter(
                        resources.getString(R.string.data_saved_successfully),
                        "",
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.positive
                        ),
                        R.drawable.check,
                        800L
                    )

                    binding.progressAnimation.progressParent.visibility =
                        View.GONE
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

    }

    private fun clickHandlers() {

        onCancelChangesHandler()
        onInfoHandler()
        onDeleteAccountHandler()
        onAccountNameEntered()
        onConfirmHandler()
    }

    private fun onConfirmHandler() {
        binding.confirmChanges.setOnClickListener {
            val isSaveMode = binding.confirmChanges.text.equals(getString(R.string.add_account))
            val editTextInput = binding.accountData.text.toString()
            var selectedAccount = viewModel.selectedAccount.value.toString()

            args.accounts?.let {
                if (isSaveMode.not()) {
                    selectedAccount = it.accountName
                }
            }

            if (dataIsValid(selectedAccount, editTextInput)) {
                if (isSaveMode)
                    saveData(editTextInput)
                else
                    updateData(editTextInput)
            } else {
                return@setOnClickListener
            }
        }
    }

    private fun onAccountNameEntered() {
        binding.apply {
            accountName.afterTextChanged {
                accountData.setText("")
                viewModel.selectedAccount.postValue(it)
                chooseAccount(it)
            }
        }
    }

    private fun onDeleteAccountHandler() {
        binding.include.rightIcon.setOnClickListener {
            deleteAccount()
        }
    }

    private fun onInfoHandler() {
        binding.info.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = resources.getString(R.string.EnterURLorUsername)
                paragraphContent.text =
                    resources.getString(R.string.AddEditFragmentBottomModalDescription)
                paragraphContent.gravity = Gravity.CENTER
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun onCancelChangesHandler() {
        binding.cancelChanges.setOnClickListener {
            handleBackButtonPress()
        }
    }

    private fun handleBackButtonPress() {
        val inputName = binding.accountData.text.toString().trim()

        if (inputName.isNotEmpty()) {

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
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                negativeOption.setOnClickListener {
                    bottomSheet.dismiss()
                }
            }
            dialog.root.setBottomSheet(bottomSheet)
        } else
            requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun updateData(newData: String) {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = resources.getString(R.string.UpdateChanges)
            optionsContent.text = resources.getString(R.string.AreYouSureToUpdateThisSocial)
            positiveOption.text = resources.getString(R.string.YesUpdate)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            negativeOption.text = resources.getString(R.string.DontUpdate)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )

            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                if (Util.checkForInternet(requireContext())) {
                    binding.progressAnimation.progressParent.visibility = View.VISIBLE
                    makeUpdateAccountRequest(newData)
                } else {
                    Util.showNoInternet(requireActivity())
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

    }

    private fun makeUpdateAccountRequest(newData: String) {
        args.accounts?.let {
            val array = resources.getStringArray(R.array.account_names)
            val index = array.indexOf(it.accountName)
            it.accountID = index
            it.accountData = newData

            viewModel.updateEntry(accountsViewModel, it)
        }
    }

    private fun deleteAccount() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ConfirmDelete)
            optionsContent.text = resources.getString(R.string.AreYouSureToDeleteThisSocial)
            positiveOption.text = resources.getString(R.string.YesDelete)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )
            negativeOption.text = resources.getString(R.string.DontDelete)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                if (Util.checkForInternet(requireContext())) {
                    binding.progressAnimation.progressParent.visibility = View.VISIBLE
                    makeDeleteAccountRequest()
                } else {
                    Util.showNoInternet(requireActivity())
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun makeDeleteAccountRequest() {
        args.accounts?.let {
            val array = resources.getStringArray(R.array.account_names)
            val index = array.indexOf(it.accountName)
            it.accountID = index

            viewModel.deleteEntry(accountsViewModel, it)
        }
    }


    private fun saveData(accountData: String) {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text =
                requireContext().resources.getString(R.string.AddNewSocial)
            optionsContent.text =
                requireContext().resources.getString(R.string.ConfirmAddDescription)
            positiveOption.text = requireContext().resources.getString(R.string.yes_save)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            negativeOption.text = requireContext().resources.getString(R.string.DontSave)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                if (Util.checkForInternet(requireContext())) {
                    binding.progressAnimation.progressParent.visibility = View.VISIBLE
                    makeSaveRequest(accountData)
                } else {
                    Util.showNoInternet(requireActivity())
                    return@setOnClickListener
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
                Util.log("Don't Save")
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun makeSaveRequest(accountData: String) {

        viewModel.selectedAccount.value?.let { it1 ->
            getStringIndex(it1)
        }?.let { index ->

            val account = Accounts(index, viewModel.selectedAccount.value!!, accountData, true)

            //Save new Data
            viewModel.saveData(
                accountsViewModel,
                Util.userID,
                account
            )
        }

    }

    private fun getStringIndex(string: String): Int {
        val stringArray = resources.getStringArray(R.array.account_names)
        return stringArray.indexOf(string)
    }

    private fun dataIsValid(selectedAccount: String?, accountData: String): Boolean {
        if (selectedAccount.isNullOrEmpty() || accountData.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.fill_all_the_inputs),
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }
        if (selectedAccount == getString(R.string.email) && accountData.isValidEmail(false).not()) {
            Toast.makeText(requireContext(), "Invalid Email", Toast.LENGTH_SHORT).show()
            return false
        }
        if ((selectedAccount == getString(R.string.phone)
                    || selectedAccount == getString(R.string.whatsapp))
            && accountData.isValidPhone(false).not()
        ) {
            Toast.makeText(requireContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setImageOnAccountNameChange(imageID: Int) {
        binding.apply {
            accountLogo.apply {
                setImageResource(imageID)
                visibility = View.VISIBLE
            }
            remainingLayout.visibility = View.VISIBLE
        }
    }

    private fun initialization() {

        //Top app bar
//        (activity as MainActivity).supportActionBar?.hide()
        binding.include.apply {

            //Mode = 0 -> Add | Mode = 1 -> Edit
            if (args.mode == 0) {
                toolbarTitle.text = resources.getString(R.string.AddAccount)
                rightIcon.visibility = View.GONE
            } else if (args.mode == 1) {
                toolbarTitle.text = resources.getString(R.string.EditAccount)
                rightIcon.apply {
                    setImageResource(R.drawable.delete)
                    visibility = View.VISIBLE
                }
                args.accounts?.let {
                    chooseAccount(it.accountName)
                    binding.apply {
                        remainingLayout.visibility = View.VISIBLE
                        menuAccount.visibility = View.GONE
                        accountNameHeader.visibility = View.GONE
                        accountData.hint = it.accountData
                        confirmChanges.text = resources.getString(R.string.UpdateChanges)
                    }
                }
            }

            leftIcon.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    handleBackButtonPress()
                }
            }
        }


        // Account List
        val accounts = Util.unusedAccounts
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            accounts
        )
        binding.accountName.setAdapter(arrayAdapter)
    }

    private fun AutoCompleteTextView.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    private fun chooseAccount(it: String) {

        setImageOnAccountNameChange(Util.getImageDrawableFromAccountName(it))
        binding.apply {
            val accountDataTvString = StringBuilder()
            accountDataTvString.append(resources.getString(R.string.enter))

            when (it) {
                resources.getStringArray(R.array.account_names)[0] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_PHONE
                        hint = resources.getString(R.string.dummy_phone_number)
                    }
                    accountDataTvString.append(it).append(resources.getString(R.string.number))
                    accountInputTV.text = accountDataTvString.toString()
                }
                resources.getStringArray(R.array.account_names)[1] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        hint = resources.getString(R.string.dummy_email)
                    }
                    accountDataTvString.append(it)
                    binding.accountInputTV.text = accountDataTvString.toString()
                }
                resources.getStringArray(R.array.account_names)[16] -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_PHONE
                        hint = resources.getString(R.string.dummy_phone_number)
                    }
                    accountDataTvString.append(it).append(resources.getString(R.string.number))
                    accountInputTV.text = accountDataTvString.toString()
                }
                else -> {
                    accountData.apply {
                        inputType = InputType.TYPE_CLASS_TEXT
                        hint = resources.getString(R.string.username123)
                    }
                    accountDataTvString.append(it)
                        .append(resources.getString(R.string.url_username))
                    accountInputTV.text = accountDataTvString.toString()
                }
            }

        }
    }
}