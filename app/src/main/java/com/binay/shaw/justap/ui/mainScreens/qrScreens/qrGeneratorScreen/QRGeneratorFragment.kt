package com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentQRGeneratorBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.helper.Util.setBottomSheet
import com.google.android.material.snackbar.Snackbar


class QRGeneratorFragment : Fragment() {

    private var _binding: FragmentQRGeneratorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : QRGeneratorViewModel
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null
    private lateinit var sharedPreference: SharedPreferences


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQRGeneratorBinding.inflate(layoutInflater, container, false)
        initialization()

        val firstSelectedColor = sharedPreference.getInt(
            "firstColor",
            ResourcesCompat.getColor(resources, R.color.text_color, null)
        )
        val secondSelectedColor = sharedPreference.getInt(
            "secondColor",
            ResourcesCompat.getColor(resources, R.color.bg_color, null)
        )

        viewModel.generateQR(displayMetrics, overlay,
            firstSelectedColor,
            secondSelectedColor)

        viewModel.status.observe(viewLifecycleOwner) {
            binding.qrCodePreview.setImageBitmap(viewModel.bitmap.value)
        }

        binding.qrCodePreview.setOnClickListener {
            Toast.makeText(requireContext(), "Long press to save in Gallery", Toast.LENGTH_SHORT).show()
        }

        binding.qrInfo.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = requireContext().resources.getString(R.string.OfflineMode)
                paragraphContent.text = requireContext().resources.getString(R.string.OfflineModeInfo)
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

        binding.qrCodePreview.setOnLongClickListener {
            Util.saveMediaToStorage(viewModel.bitmap.value as Bitmap, requireContext()).also { status ->
                if (status)
                    Snackbar.make(binding.root, "Successfully saved in Storage", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.onlineOfflineModeSwitch.setOnTouchListener{
                _, event -> event.actionMasked == MotionEvent.ACTION_MOVE
        }

        binding.onlineOfflineModeSwitch.setOnClickListener {
            Toast.makeText(requireContext(), "Tapped", Toast.LENGTH_SHORT).show()
        }

        binding.scanQRCode.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_scanner_to_scannerFragment)
        }

        return binding.root
    }

    private fun initialization() {

        (activity as MainActivity).supportActionBar?.hide()
        binding.include.toolbarTitle.text = requireContext().resources.getString(R.string.MyQRCode)
        viewModel = ViewModelProvider(this@QRGeneratorFragment)[QRGeneratorViewModel::class.java]
        displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        overlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo_black_stroke)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())
        sharedPreference = requireContext().getSharedPreferences("QRPref", Context.MODE_PRIVATE)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}