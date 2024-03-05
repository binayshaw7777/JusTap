package com.binay.shaw.justap.presentation.mainScreens.qrScreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.binay.shaw.justap.presentation.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.databinding.FragmentQRScannerBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.utilities.Util.createBottomSheet
import com.binay.shaw.justap.utilities.Util.setBottomSheet
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.S)
class ScannerFragment : BaseFragment() {

    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private var preDefinedListOfSocialQRData = mutableListOf<String>()
    private var dataFound = false
    private lateinit var pvScan: androidx.camera.view.PreviewView
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = requireActivity().display
                display?.getRealMetrics(metrics)
            } else {
                val display = requireActivity().windowManager.defaultDisplay
                display.getMetrics(metrics)
            }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    private var _binding: FragmentQRScannerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQRScannerBinding.inflate(layoutInflater, container, false)
        initialization()

        binding.include.leftIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.scannerInfo.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text =
                    requireContext().resources.getString(R.string.scanner_info_title)
                paragraphContent.text =
                    requireContext().resources.getString(R.string.scanner_info_content)
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

        setupCamera()

        return binding.root
    }

    private fun initialization() {
//        (activity as MainActivity).supportActionBar?.hide()
        binding.include.apply {
            toolbarTitle.text = requireContext().resources.getString(R.string.Scanner)
            leftIcon.visibility = View.VISIBLE
        }
        pvScan = binding.scanPreview

        preDefinedListOfSocialQRData.apply {
            add("https://wa.me/qr/")
            add("http://wa.me/qr/")
            add("http://instagram")
            add("https://instagram")
            add("https://facebook")
            add("https://twitter")
            add("http://facebook")
            add("http://twitter")
        }
    }

    override fun onResume() {
        super.onResume()
        if (dataFound)
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST
                            )
                        }
                    }
                } catch (e: ExecutionException) {
                    // Handle any errors (including cancellation) here.
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                } catch (e: InterruptedException) {
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                }
            }, ContextCompat.getMainExecutor(requireActivity())
        )
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation).build()

        previewUseCase?.setSurfaceProvider(pvScan.surfaceProvider)

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(this, it, previewUseCase)
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun bindAnalyseUseCase() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation).build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(/* lifecycleOwner = */this, it, analysisUseCase
                )
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner, imageProxy: ImageProxy
    ) {
        if (imageProxy.image == null) return
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
            val barcode = barcodes.getOrNull(0)
            barcode?.rawValue?.let { data ->
                cameraProvider?.unbindAll()
                dataFound = true
                if (!Util.checkForInternet(requireContext())) {
                    Util.showNoInternet(requireActivity())
                    handleBackButtonPress()
                } else {

                    Util.vibrateDevice(500L, requireContext())

//                    val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))
                    Util.log("Scanned encrypted Result: $data")

                    if (isLink(data)) {
                        Util.log("IS LINK")
                        for (regex in preDefinedListOfSocialQRData) {
                            if (data.contains(regex))
                                openLink(data)
                        }

                    } else {
//                        val decrypted = encryption.decryptOrNull(data)

                        val action =
                            ScannerFragmentDirections.actionScannerFragmentToResultFragment(
                                resultString = data,
                                isResult = true
                            )
                        findNavController().navigate(action)
                    }

                }
            }
        }.addOnFailureListener {

        }.addOnCompleteListener {
            imageProxy.close()
        }
    }


    private fun isLink(data: String): Boolean {
        if (data.contains("https://") || data.contains("http://"))
            return true
        return false
    }

    private fun openLink(link: String) {
        Util.log("Link Opened is: $link")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                setupCamera()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean = this.let {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
    } == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}