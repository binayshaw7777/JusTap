package com.binay.shaw.justap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.User
import com.binay.shaw.justap.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseViewModel : ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    val userLiveData = MutableLiveData<LocalUser>()
    val accountsLiveData = MutableLiveData<List<Accounts>>()
    val errorLiveData = MutableLiveData<String>()
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
    val googleSignInStatus = MutableLiveData<Boolean>()
    val registerStatus = MutableLiveData<Boolean>()
    val resetPasswordRequest = MutableLiveData<Boolean>()


    fun deleteUser() = viewModelScope.launch {
        firebaseRepository.deleteUser()
        FirebaseAuth.getInstance().signOut()
    }

    fun resetPassword(email: String) = viewModelScope.launch(Dispatchers.IO) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                resetPasswordRequest.postValue(true)
            }.addOnFailureListener {
                errorLiveData.postValue(it.message.toString())
            }
    }

    fun logInUser(email: String, password: String) = viewModelScope.launch {
        val user = firebaseRepository.loginUser(email, password)
        if (user != null) {
            Util.log("User ID: $user \n ${user.uid}")
            databaseReference.child(Constants.users).child(user.uid).get()
                .addOnSuccessListener {
                    firebaseUserToLocalUser(it)
                }.addOnFailureListener {
                    errorLiveData.postValue(it.message.toString())
                }
        } else {
            errorLiveData.postValue("Please check your details")
        }
    }

    private fun firebaseUserToLocalUser(snapshot: DataSnapshot) {
        snapshot.apply {
            val id = child(Constants.userID).value.toString()
            val name = child(Constants.name).value.toString()
            val email = child(Constants.email).value.toString()
            val profilePicture = child(Constants.profilePictureUri).value.toString()
            val profileBanner = child(Constants.profileBannerUri).value.toString()
            val bio = child(Constants.bio).value.toString()
            if (snapshot.hasChild(Constants.accounts)) {
                firebaseAccountsToLocalAccounts(snapshot.child(Constants.accounts))
            }
            val user = LocalUser(id, name, email, bio, profilePicture, profileBanner)
            Util.log("Get user: $user")
            userLiveData.value = user
        }
    }

    private fun firebaseAccountsToLocalAccounts(snapshot: DataSnapshot) {
        val accountList = mutableListOf<Accounts>()
        snapshot.children.forEach { iterator ->
            val tempMap = iterator.value as java.util.HashMap<*, *>
            val acc = Accounts(
                (tempMap[Constants.accountId] as Long).toInt(),
                tempMap[Constants.accountName] as String,
                tempMap[Constants.accountData] as String,
                tempMap[Constants.showAccount] as Boolean
            )
            accountList.add(acc)
        }
        Util.log("Get accounts: $accountList")
        accountsLiveData.value = accountList
    }

    fun signInWithGoogle(currentUser: FirebaseUser?) = viewModelScope.launch(Dispatchers.IO) {
        currentUser?.let {
            val userRef = Firebase.database.reference.child(Constants.users).child(it.uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Util.log("User exists")
                        //Get the user
                        firebaseUserToLocalUser(dataSnapshot)
                        if (dataSnapshot.hasChild(Constants.accounts)) {
                            firebaseAccountsToLocalAccounts(
                                dataSnapshot.child(
                                    Constants.accounts
                                )
                            )
                        }
                    } else {
                        Util.log("User does not exist")
                        val saveUser = LocalUser(
                            it.uid,
                            it.displayName.toString(),
                            it.email.toString(),
                            "",
                            "",
                            ""
                        )
                        userRef.setValue(saveUser)
                        userLiveData.postValue(saveUser)
                    }
                    googleSignInStatus.postValue(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    Util.log("getUser:onCancelled ${error.toException()}")
                    googleSignInStatus.postValue(false)
                    errorLiveData.postValue(error.message)
                }
            })
        }
    }

    fun createNewAccount(name: String, email: String, password: String) = viewModelScope.launch {
        val userId = firebaseRepository.registerUser(email, password)?.uid
        if (userId != null) {
            val user = User(userId, name, email)

            FirebaseDatabase.getInstance().reference.child(Constants.users)
                .child(userId).setValue(user).addOnSuccessListener {
                    FirebaseAuth.getInstance().signOut()
                    registerStatus.postValue(true)
                }.addOnFailureListener {
                    errorLiveData.postValue(it.message.toString())
                }
        } else {
            errorLiveData.postValue("Please check your details")
        }
    }
}