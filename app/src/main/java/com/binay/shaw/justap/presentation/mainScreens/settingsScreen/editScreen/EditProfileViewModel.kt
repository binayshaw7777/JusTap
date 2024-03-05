package com.binay.shaw.justap.presentation.mainScreens.settingsScreen.editScreen

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class EditProfileViewModel : ViewModel() {

    var status = MutableLiveData<Int>()

    init {
        status.value = 0
    }

    /**
     * 0 - Default
     * 1 - Pfp success
     * 2 - pfp fail
     * 3 - banner success
     * 4 - banner fail
     * 5 - firebaseUserUpload success
     * 6 - firebaseUserUpload fail
     * */


    suspend fun updateUser(
        firebaseDatabase: FirebaseDatabase,
        storageRef: StorageReference,
        originalID: String,
        userMap: MutableMap<String, Any>,
        originalPFP: String,
        originalBanner: String,
        profilePictureURI: Uri?,
        profileBannerURI: Uri?,
        localUserViewModel: LocalUserViewModel
    ) = viewModelScope.launch {


        val uploadInFirebaseStorage = launch {

            val profilePictureUpdate = viewModelScope.async(Dispatchers.IO) {
                fireProfileUpdate(storageRef, originalID, originalPFP, profilePictureURI)
            }

            val profileBannerUpdate = viewModelScope.async(Dispatchers.IO) {
                fireBannerUpdate(storageRef, originalID, originalBanner, profileBannerURI)
            }


            userMap["profilePictureURI"] = profilePictureUpdate.await()
            userMap["profileBannerURI"] = profileBannerUpdate.await()

        }

        uploadInFirebaseStorage.join()

        val uploadInDatabases = launch {


            val updateFirebaseRealtimeDatabase = viewModelScope.async(Dispatchers.IO) {
                fireUpdateRealtimeDatabase(firebaseDatabase, originalID, userMap)
            }

            val updateLocalDatabase = viewModelScope.async(Dispatchers.IO) {
                fireUpdateLocalDatabase(localUserViewModel, userMap)
            }

            status.value = updateFirebaseRealtimeDatabase.await() + updateLocalDatabase.await()


        }

        uploadInDatabases.join()
        status.value = status.value?.plus(1)


    }

    private suspend fun fireUpdateLocalDatabase(
        localUserViewModel: LocalUserViewModel,
        userMap: MutableMap<String, Any>
    ): Int {

        return withContext(Dispatchers.IO) {

            val localUser = LocalUser(
                userMap["userID"].toString(),
                userMap["name"].toString(),
                userMap["email"].toString(),
                userMap["bio"].toString(),
                userMap["profilePictureURI"].toString(),
                userMap["profileBannerURI"].toString(),
            )
            localUserViewModel.updateUser(localUser)
            1
        }

    }

    private suspend fun fireUpdateRealtimeDatabase(
        firebaseDatabase: FirebaseDatabase,
        originalID: String,
        userMap: MutableMap<String, Any>
    ): Int {

        return withContext(Dispatchers.IO) {

            val ref = firebaseDatabase.getReference("/Users/$originalID")
            ref.setValue(userMap).addOnSuccessListener {
                Util.log("Successfully updated data in firebase")
            }.addOnFailureListener {
                Util.log("Failed to update data in firebase")
            }.await()
            1
        }
    }

    private suspend fun fireBannerUpdate(
        storageRef: StorageReference,
        originalID: String,
        originalBanner: String,
        profileBannerURI: Uri?
    ): String {

        val imageRef = storageRef.child("file/profileBanner/$originalID")

        return withContext(Dispatchers.IO) {

            if (profileBannerURI != null) {
                imageRef
                    .putFile(profileBannerURI)
                    .await() // await() instead of snapshot
                    .storage
                    .downloadUrl
                    .await() // await the url
                    .toString()

            } else if (originalBanner.isNotEmpty()) {
                originalBanner
            } else ""
        }

    }

    private suspend fun fireProfileUpdate(
        storageRef: StorageReference,
        originalID: String,
        originalPFP: String,
        profilePictureURI: Uri?
    ): String {

        return withContext(Dispatchers.IO) {
            val imageRef = storageRef.child("file/profilePicture/$originalID")
            if (profilePictureURI != null) {

                imageRef
                    .putFile(profilePictureURI)
                    .await() // await() instead of snapshot
                    .storage
                    .downloadUrl
                    .await() // await the url
                    .toString()

            } else if (originalPFP.isNotEmpty()) {
                originalPFP
            } else ""
        }

    }


}
