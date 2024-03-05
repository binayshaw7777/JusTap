package com.binay.shaw.justap.presentation.common

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.databinding.FragmentImagePickerBinding
import com.binay.shaw.justap.utilities.ImageUtils.getBitmapFromUri
import com.binay.shaw.justap.utilities.Util
import com.github.dhaval2404.imagepicker.ImagePicker


@RequiresApi(Build.VERSION_CODES.P)
class ImagePickerFragment : BaseFragment() {

    private var _binding: FragmentImagePickerBinding? = null
    private val binding get() = _binding!!
    private var selectedBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePickerBinding.inflate(layoutInflater, container, false)

        initViews()
        clickHandlers()

        return binding.root
    }

    private fun clickHandlers() {
        binding.apply {

            selectImageButton.setOnClickListener {
                ImagePicker.with(this@ImagePickerFragment)
                    .cropSquare()
                    .compress(1000)
                    .maxResultSize(275, 275)
                    .createIntent {
                        imagePickerActivityResult.launch(it)
                    }
            }

            include.leftIcon.setOnClickListener {
                findNavController().popBackStack()
            }

            saveImageButton.setOnClickListener {
                // In Fragment B, pass the Bitmap back to Fragment A
                if (selectedBitmap != null) {
                    val navController = findNavController()
                    navController.previousBackStackEntry?.savedStateHandle?.set("qrOverlay", selectedBitmap)
                    navController.popBackStack()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initViews() {

        binding.apply {
            include.apply {
                toolbarTitle.text = getString(R.string.image_picker)
                leftIcon.visibility = View.VISIBLE
            }
        }


    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val bitmap = requireContext().getBitmapFromUri(fileUri)
                    Util.log("Image Uri is: $fileUri && Bitmap is: $bitmap")
                    binding.imagePreviewPlaceholder.setImageBitmap(bitmap)
                    binding.saveImageButton.visibility = View.VISIBLE
                    selectedBitmap = bitmap
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}