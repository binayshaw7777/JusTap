package com.binay.shaw.justap.presentation.mainScreens.homeScreen.accountFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.viewModel.AccountsViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class AddEditViewModel : ViewModel() {

    val saveStatus = MutableLiveData<Int>()
    val deleteStatus = MutableLiveData<Int>()
    val updateStatus = MutableLiveData<Int>()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    val selectedAccount = MutableLiveData<String>()

    init {
        saveStatus.value = 0
        deleteStatus.value = 0
        updateStatus.value = 0
    }

    /**
     * 0 - Default
     * 1 - Failed to upload in Firebase
     * 2 - Failed to update Room DB
     * 3 - Success in both
     * */

    fun saveData(
        accountsViewModel: AccountsViewModel,
        userID: String,
        account: Accounts
    ) = viewModelScope.launch {

        val createNewDataInFirebaseAndRoomDB = launch {

            val createAccountObjectInFirebase = viewModelScope.async(Dispatchers.IO) {
                saveInFirebase(userID, account)
            }

            val createAccountObjectInRoomDB = viewModelScope.async(Dispatchers.IO) {
                saveInRoomDB(accountsViewModel, account)
            }

            saveStatus.value =
                createAccountObjectInFirebase.await() + createAccountObjectInRoomDB.await()

        }

        createNewDataInFirebaseAndRoomDB.join()
        saveStatus.value = saveStatus.value?.plus(1)


    }


    private suspend fun saveInFirebase(
        userID: String,
        account: Accounts
    ): Int {

        return withContext(Dispatchers.IO) {
            val ref =
                firebaseDatabase.getReference("/${Constants.users}/$userID/${Constants.accounts}/${account.accountID}")
            ref.setValue(account).addOnSuccessListener {
                Util.log("Successfully updated data in firebase")
            }.addOnFailureListener {
                Util.log("Failed to update data in firebase")
            }.await()
            1
        }

    }

    private suspend fun saveInRoomDB(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ): Int {
        return withContext(Dispatchers.IO) {
            accountsViewModel.insertAccount(account)
            1
        }
    }

    fun updateEntry(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ) = viewModelScope.launch {

        val updateFirebaseAndRoomDB = launch {

            val updateAccountObjectInFirebase = viewModelScope.async(Dispatchers.IO) {
                updateInFirebase(Util.userID, account)
            }

            val updateAccountObjectInRoomDB = viewModelScope.async(Dispatchers.IO) {
                updateInRoomDB(accountsViewModel, account)
            }

            updateStatus.value = (
                    updateAccountObjectInFirebase.await() + updateAccountObjectInRoomDB.await()
                    )
        }
        updateFirebaseAndRoomDB.join()
        updateStatus.value = updateStatus.value?.plus(1)
    }

    private suspend fun updateInRoomDB(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ): Int {

        return withContext(Dispatchers.IO) {
            try {
                accountsViewModel.updateAccount(account)
                Util.log("Updated in RoomDB")
                1
            } catch (e: java.lang.Exception) {
                Util.log("Error: $e")
                5
            }

        }
    }

    private suspend fun updateInFirebase(
        userID: String,
        account: Accounts
    ): Int {
        return withContext(Dispatchers.IO) {
            val ref =
                firebaseDatabase.getReference("/${Constants.users}/$userID/${Constants.accounts}/${account.accountID}")
            try {
                val updates: MutableMap<String, Any> = HashMap()
                updates[Constants.accountId] = account.accountID
                updates[Constants.accountName] = account.accountName
                updates[Constants.accountData] = account.accountData
                updates[Constants.showAccount] = account.showAccount

                ref.updateChildren(updates)
                Util.log("Updated from Firebase")
                1
            } catch (e: java.lang.Exception) {
                5
            }
        }
    }


    fun deleteEntry(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ) = viewModelScope.launch {

        val deleteDataInFirebaseAndRoomDB = launch {

            val deleteAccountObjectInFirebase = viewModelScope.async(Dispatchers.IO) {
                deleteInFirebase(Util.userID, account)
            }

            val deleteAccountObjectInRoomDB = viewModelScope.async(Dispatchers.IO) {
                deleteInRoomDB(accountsViewModel, account)
            }

            deleteStatus.value = (
                    deleteAccountObjectInFirebase.await() + deleteAccountObjectInRoomDB.await()
                    )
        }

        deleteDataInFirebaseAndRoomDB.join()
        deleteStatus.value = deleteStatus.value?.plus(1)

    }

    private suspend fun deleteInRoomDB(
        accountsViewModel: AccountsViewModel,
        account: Accounts
    ): Int {

        return withContext(Dispatchers.IO) {
            try {
                accountsViewModel.deleteAccount(account)
                Util.log("Deleted from RoomDB")
                1
            } catch (e: java.lang.Exception) {
                Util.log("Error: $e")
                5
            }

        }
    }

    private suspend fun deleteInFirebase(
        userID: String,
        account: Accounts
    ): Int {
        return withContext(Dispatchers.IO) {
            val ref =
                firebaseDatabase.getReference("/${Constants.users}/$userID/${Constants.accounts}/")
            try {
                ref.child("${account.accountID}").removeValue()
                Util.log("Deleted from Firebase")
                1
            } catch (e: java.lang.Exception) {
                5
            }
        }
    }

}