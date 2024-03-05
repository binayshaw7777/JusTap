package com.binay.shaw.justap.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.binay.shaw.justap.R
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.presentation.authentication.signInScreen.SignInScreen
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


object Util {

    var userID: String = "0"

    var unusedAccounts = mutableListOf<String>()

    fun log(message: String) {
        Log.d("", message)
    }

    fun getBaseStringForFiltering(originalString: String): String {
        val stringBuilder = StringBuilder()

        for (char in originalString.toCharArray()) {
            if (char.isLetter())
                stringBuilder.append(char)
        }

        return stringBuilder.toString()
    }

    fun getFirstName(fullName: String): String {
        if (fullName.isNotEmpty())
            return fullName.split(" ")[0]
        return ""
    }

    fun isUserLoggedIn(): Boolean {
        return if (FirebaseAuth.getInstance().currentUser == null) {
            log("You are not logged in")
            false
        } else {
            log("You are logged in!")
            true
        }
    }

    @SuppressLint("ServiceCast")
    fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * Extension function that saves a Bitmap to the device storage and returns the Uri of the saved image.
     */
    fun Bitmap.saveToStorageAndGetUri(context: Context): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val outputStream: OutputStream?
        val uri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore API to save the image for Android Q (API level 29) and above
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val contentResolver = context.contentResolver
            uri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let { contentResolver?.openOutputStream(it) }
        } else {
            // Use traditional file API to save the image for below Android Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(imagesDir, filename)
            uri = Uri.fromFile(imageFile)
            outputStream = FileOutputStream(imageFile)
        }

        outputStream?.use {
            this.compress(Bitmap.CompressFormat.JPEG, 100, it)
            log("Saved to Photos")
        }
        return uri
    }




//    fun saveMediaToStorage(bitmap: Bitmap, context: Context): Boolean {
//        var success = false
//        val filename = "${System.currentTimeMillis()}.jpg"
//        var fos: OutputStream? = null
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            context.contentResolver?.also { resolver ->
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//                }
//                val imageUri: Uri? =
//                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//                fos = imageUri?.let { resolver.openOutputStream(it) }
//            }
//        } else {
//            val imagesDir =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//            val image = File(imagesDir, filename)
//            fos = FileOutputStream(image)
//        }
//        fos?.use {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//            log("Saved to Photos")
//            success = true
//        }
//        return success
//    }

    fun loadImagesWithGlide(imageView: ImageView, url: String) {
        Glide.with(imageView)
            .load(url)
            .centerCrop()
            .error(R.drawable.default_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    /** Status values
     * 0 - Default
     * 1 - name is empty
     * 2 - email is empty
     * 3 - email is not valid
     * 4 - password is empty
     * 5 - password is less than 8 characters
     * 6 - password must contains Uppercase, lowercase and symbols
     * 7 - success
     * */
    fun validateUserAuthInput(name: String?, email: String?, password: String?): Int {

        name?.let {
            if (name.isEmpty())
                return 1
        }
        email?.let {
            if (email.isEmpty())
                return 2
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return 3
        }

        password?.let {
            if (password.isEmpty())
                return 4
            else if (password.length < 8)
                return 5
            else if (!isValidPassword(password))
                return 6
        }

        return 7
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.firstOrNull { it.isDigit() } == null) return false
        if (password.filter { it.isLetter() }
                .firstOrNull { it.isUpperCase() } == null) return false
        if (password.filter { it.isLetter() }
                .firstOrNull { it.isLowerCase() } == null) return false
        if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false

        return true
    }

    fun Context.createBottomSheet(): BottomSheetDialog {
        return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    }

    fun Activity.createBottomSheet(): BottomSheetDialog {
        return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
    }

    fun View.setBottomSheet(bottomSheet: BottomSheetDialog) {
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.setContentView(this)
        bottomSheet.create()
        bottomSheet.show()
    }


    fun getImageDrawableFromAccountName(accountName: String): Int {

        val map = HashMap<String, Int>()
        map["Phone"] = R.drawable.phone
        map["Email"] = R.drawable.email
        map["Instagram"] = R.drawable.instagram
        map["LinkedIn"] = R.drawable.linkedin
        map["GitHub"] = R.drawable.github
        map["Facebook"] = R.drawable.facebook
        map["Twitter"] = R.drawable.twitter
        map["YouTube"] = R.drawable.youtube
        map["Snapchat"] = R.drawable.snapchat
        map["Twitch"] = R.drawable.twitch
        map["Website"] = R.drawable.website
        map["Discord"] = R.drawable.discord
        map["LinkTree"] = R.drawable.linktree
        map["Custom Link"] = R.drawable.custom_link
        map["Telegram"] = R.drawable.telegram
        map["Spotify"] = R.drawable.spotify
        map["WhatsApp"] = R.drawable.whatsapp

        return map[accountName]!!

    }

    fun colorIsNotTheSame(firstColor: Int, secondColor: Int): Boolean {
        return firstColor != secondColor
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.S)
    fun vibrateDevice(duration: Long, context: Context) {
        try {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(duration)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun showNoInternet(activity: Activity) {
        Alerter.create(activity)
            .setTitle(activity.resources.getString(R.string.noInternet))
            .setText(activity.resources.getString(R.string.noInternetDescription))
            .setBackgroundColorInt(
                ContextCompat.getColor(
                    activity.baseContext,
                    R.color.negative
                )
            )
            .setIcon(R.drawable.wifi_off)
            .setDuration(2000L)
            .show()
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    /**
     * Extension function that shares an image and text using an Android Intent
     */
    fun ImageView.shareImageAndText(context: Context, shareText: String) {
        // Get the Drawable from the ImageView
        val drawable: Drawable? = this.drawable

        // Convert the Drawable to a Bitmap
        val bitmap: Bitmap = (drawable as? BitmapDrawable)?.bitmap ?: Bitmap.createBitmap(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888)
        drawable?.draw(Canvas(bitmap))

        // Share the Bitmap and text using an Intent
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"

        // Add the Bitmap as an attachment to the Intent
        val imageUri = bitmap.saveToStorageAndGetUri(context)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

        // Add some text to the Intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)

        // Start the Intent chooser to share the content
        val chooserIntent = Intent.createChooser(shareIntent, "Share Image")
        context.startActivity(chooserIntent)
    }

    fun clearDataAndLogout(scope: CoroutineScope, context: Context) {
        scope.launch(Dispatchers.Main) {
            val sharedPreferences =
                context.getSharedPreferences(Constants.qrPref, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val signOutFromFirebase =
                launch(Dispatchers.IO) { FirebaseAuth.getInstance().signOut() }
            signOutFromFirebase.join()
            LocalUserDatabase.getDatabase(context).clearTables()
            val intent = Intent(context, SignInScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent).also { context.findActivity()?.finish() }
            log("Logged out")
        }
    }

    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}