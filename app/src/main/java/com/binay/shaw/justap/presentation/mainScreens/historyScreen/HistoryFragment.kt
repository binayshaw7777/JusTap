package com.binay.shaw.justap.presentation.mainScreens.historyScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.HistoryAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentHistoryBinding
import com.binay.shaw.justap.databinding.OptionsModalBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.binay.shaw.justap.model.LocalHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HistoryFragment : BaseFragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val localUserHistoryViewModel by viewModels<LocalHistoryViewModel> { ViewModelFactory() }
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        initObservers()
        initialization()
        clickHandlers()

        return binding.root
    }

    private fun initObservers() {
        localUserHistoryViewModel.run {
            getAllHistory.observe(viewLifecycleOwner) {
                Util.log("Accounts Scanned: $it")
                updateEmptyState(it)
                accountListLiveData.value = emptyList()
                accountListLiveData.postValue(it)
                historyAdapter.setData(it)
            }
        }
    }

    private fun updateEmptyState(localHistoryLIST: List<LocalHistory>?) {
        if (localHistoryLIST.isNullOrEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.emptyState.visibility = View.GONE
        }
    }

    private fun clickHandlers() {
        setFilter()
        historyIconHandler()
        infoHandler()
        clearFilter()
    }

    private fun clearFilter() {
        binding.apply {
            clearText.setOnClickListener {
                hideKeyboard()
                it.visibility = View.GONE
                etFilter.apply {
                    setText("")
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0)
                    clearFocus()
                }
            }
        }
    }

    private fun infoHandler() {
        binding.include.leftIcon.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = resources.getString(R.string.how_history_saving_works)
                paragraphContent.text = resources.getString(R.string.historyScreenHelpDescription)
                lottieAnimationLayout.apply {
                    setAnimation(R.raw.delete_lottie)
                    visibility = View.VISIBLE
                }
            }
            dialog.root.setBottomSheet(bottomSheet)
        }
    }

    private fun historyIconHandler() {
        binding.include.rightIcon.setOnClickListener {
            if (historyAdapter.itemCount > 0)
                clearHistory()
            else
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_data_to_clear),
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFilter() {

        binding.etFilter.apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    if (p0.toString().isNotEmpty()) {
                        filter(Util.getBaseStringForFiltering(p0.toString().lowercase()))
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        binding.clearText.visibility = View.VISIBLE
                    } else {
                        binding.clearText.visibility = View.GONE
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0)
                        localUserHistoryViewModel.accountListLiveData.value?.let {
                            historyAdapter.setData(it)
                        }
                    }
                }
            })
        }
    }

    private fun filter(search: String) {
        val filterList = mutableListOf<LocalHistory>()
        if (search.isNotEmpty()) {
            for (current in localUserHistoryViewModel.accountListLiveData.value!!) {
                if (Util.getBaseStringForFiltering(current.username.lowercase()).contains(search)) {
                    filterList.add(current)
                }
            }
            historyAdapter.setData(filterList)
        } else
            historyAdapter.setData(localUserHistoryViewModel.accountListLiveData.value!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearHistory() {

        val dialog = OptionsModalBinding.inflate(layoutInflater)
        val bottomSheet = requireContext().createBottomSheet()
        dialog.apply {

            optionsHeading.text = requireContext().resources.getString(R.string.ClearHistory)
            optionsContent.text =
                requireContext().resources.getString(R.string.ClearHistoryDescription)
            positiveOption.text = requireContext().resources.getString(R.string.ClearHistory)
            positiveOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.negative
                )
            )
            negativeOption.text = requireContext().resources.getString(R.string.DontClearHistory)
            negativeOption.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color
                )
            )
            positiveOption.setOnClickListener {
                bottomSheet.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    localUserHistoryViewModel.clearHistory()
                    withContext(Dispatchers.Main) {
                        historyAdapter.notifyDataSetChanged()
                        showAlerter(
                            resources.getString(R.string.clearHistory),
                            resources.getString(R.string.clearHistoryDescription),
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.positive
                            ),
                            R.drawable.delete,
                            2000L
                        )
                    }
                }
            }
            negativeOption.setOnClickListener {
                bottomSheet.dismiss()
            }
        }
        dialog.root.setBottomSheet(bottomSheet)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun initialization() {

//        (activity as MainActivity).supportActionBar?.hide()
        binding.apply {
            include.apply {
                toolbarTitle.text = requireContext().resources.getString(R.string.History)
                rightIcon.visibility = View.VISIBLE
                leftIcon.visibility = View.VISIBLE
                leftIcon.setImageResource(R.drawable.info_icon)
            }
            historyAdapter = HistoryAdapter(
                requireContext(), { historyUser ->
                    //Handle on click
                    if (Util.checkForInternet(requireContext())) {
                        val action = HistoryFragmentDirections.actionHistoryToResultFragment(
                            historyUser.userID,
                            true
                        )
                        findNavController().navigate(action)
                    } else {
                        Util.showNoInternet(requireActivity())
                    }
                }, { userToDelete ->

                    val dialog = OptionsModalBinding.inflate(layoutInflater)
                    val bottomSheet = requireContext().createBottomSheet()
                    dialog.apply {

                        optionsHeading.text = "Delete ${userToDelete.username} from History"
                        optionsContent.text =
                            "${userToDelete.username}'s data will be removed from History"
                        positiveOption.text = resources.getString(R.string.Delete)
                        positiveOption.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.negative
                            )
                        )
                        negativeOption.text =
                            requireContext().resources.getString(R.string.DontDelete)
                        negativeOption.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.text_color
                            )
                        )
                        positiveOption.setOnClickListener {
                            bottomSheet.dismiss()
                            lifecycleScope.launch(Dispatchers.IO) {
                                localUserHistoryViewModel.deleteUserHistory(userToDelete)
                                withContext(Dispatchers.Main) {
                                    historyAdapter.notifyDataSetChanged()
                                    showAlerter(
                                        resources.getString(R.string.deleted),
                                        "${userToDelete.username} was remove from History!",
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.positive
                                        ),
                                        R.drawable.delete,
                                        2000L
                                    )
                                }
                            }
                        }
                        negativeOption.setOnClickListener {
                            bottomSheet.dismiss()
                        }
                    }
                    dialog.root.setBottomSheet(bottomSheet)
                })

            binding.historyRv.apply {
                adapter = historyAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    NavHostFragment.findNavController(this@HistoryFragment).popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onResume() {
        super.onResume()
        if (historyAdapter.itemCount == 0) {
            binding.emptyState.visibility = View.VISIBLE
        } else binding.emptyState.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
