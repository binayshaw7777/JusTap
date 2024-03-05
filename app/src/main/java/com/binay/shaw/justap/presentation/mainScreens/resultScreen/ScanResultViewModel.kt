package com.binay.shaw.justap.presentation.mainScreens.resultScreen

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.base.BaseViewModel
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalHistory
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.presentation.mainScreens.historyScreen.LocalHistoryViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ScanResultViewModel(application: Application) : BaseViewModel(application) {

    var showCaseAccountsListLiveData = MutableLiveData<List<Accounts>>()
    var showCaseAccountsListDevLiveData = MutableLiveData<List<Accounts>>()
    var scanResultUser = MutableLiveData<User>()
    var status = MutableLiveData<Int>()

    init {
        status.value = 0
    }


    fun getDataFromUserID(userID: String) {

        val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        database.child(userID).get()
            .addOnSuccessListener {
                val name = it.child(Constants.name).value.toString()
                val email = it.child(Constants.email).value.toString()
                val profilePicture = it.child(Constants.profilePictureUri).value.toString()
                val profileBanner = it.child(Constants.profileBannerUri).value.toString()
                val bio = it.child("bio").value.toString()

                if (it.hasChild(Constants.accounts)) {
                    val accountList = mutableListOf<Accounts>()
                    Util.log("accountsList: $accountList")
                    it.child(Constants.accounts).children.forEach { iterator ->
                        if (iterator != null) {
                            val tempMap = iterator.value as java.util.HashMap<*, *>
                            val acc = Accounts(
                                (tempMap[Constants.accountId] as Long).toInt(),
                                tempMap[Constants.accountName] as String,
                                tempMap[Constants.accountData] as String,
                                tempMap[Constants.showAccount] as Boolean
                            )
                            accountList.add(acc)
                        }
                    }

                    showCaseAccountsListLiveData.postValue(accountList)
                }

                scanResultUser.postValue(
                    User(
                        userID, name,
                        email, bio, profilePicture, profileBanner
                    )
                )

                Util.log(it.value.toString())

                status.value = 1    //Success
            }.addOnFailureListener {
                Util.log("Failed to fetch user data")
                status.value = 2    //Failed
            }

    }

    fun saveLocalHistory(
        user: User,
        profileByteArray: Bitmap?,
        localUserHistoryViewModel: LocalHistoryViewModel
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val dateString = StringBuilder()
            dateString.append(Constants.added_on).append(" ").append(Util.getCurrentDate())
            val localHistory = LocalHistory(user.userID, user.name, dateString.toString(), profileByteArray)
            localUserHistoryViewModel.insertUserHistory(localHistory)
        }


    fun getDevelopersAccount(accountsNameArray: Array<String>) {

        val accounts = mutableListOf<Accounts>()


        accounts.apply {
            add(
                Accounts(
                    1,
                    accountsNameArray[1],
                    Constants.myEmail,
                    true
                )
            )
            add(
                Accounts(
                    3,
                    accountsNameArray[3],
                    Constants.myLinkedIn,
                    true
                )
            )
            add(
                Accounts(4,
                    accountsNameArray[4],
                    Constants.myGitHub,
                    true)
            )
            add(
                Accounts(
                    6,
                    accountsNameArray[6],
                    Constants.myTwitter,
                    true
                )
            )
            add(
                Accounts(
                    10,
                    accountsNameArray[10],
                    Constants.myWebsite,
                    true
                )
            )
        }

        showCaseAccountsListDevLiveData.postValue(accounts)
    }

//    fun updateAnalytics(scannedUserId: String) = viewModelScope.launch(Dispatchers.IO) {
//
//        //First get the current count of current User and increment
//        //Then get the current count of the scanned User and increment
//
//        val postScanCount = async {
//            updateAnalyticsCountInFirebase(Util.userID, Constants.scanCount)
//        }
//
//        val postImpressionCount = async {
//            updateAnalyticsCountInFirebase(scannedUserId, Constants.impressionCount)
//        }
//
//        Util.log("ScanCount status: ${postScanCount.await()}\nImpressionCount status: ${postImpressionCount.await()}")
//    }

    private fun updateAnalyticsCountInFirebase(userID: String, dataToUpdate: String): Boolean {
        val userRef =
            Firebase.database.reference.child(Constants.users).child(userID).child(Constants.analytics)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    var toIncrement = 0

                    if (dataSnapshot.child(dataToUpdate).exists()) {
                        val scanCount = dataSnapshot.child(dataToUpdate).value.toString()
                        toIncrement = scanCount.toInt().plus(1)
                    }
                    userRef.child(dataToUpdate).setValue(toIncrement.toString())

                } else {
                    // User does not exist in the database
                    val mapOfAnalytics = mutableMapOf<String, String>()
                    mapOfAnalytics[Constants.scanCount] = "0"
                    mapOfAnalytics[Constants.impressionCount] = "0"
                    userRef.setValue(mapOfAnalytics)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Util.log("getUser:onCancelled ${error.toException()}")
            }
        })
        return true
    }

}