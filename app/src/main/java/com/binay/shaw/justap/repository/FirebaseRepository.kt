package com.binay.shaw.justap.repository

import com.binay.shaw.justap.utilities.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FirebaseRepository {
    private val auth = Firebase.auth
    private val database = Firebase.database.reference

    suspend fun deleteUser() {
        auth.currentUser?.delete()?.await()
        database.child("Users").child(Util.userID).removeValue().await()
    }

//    suspend fun loginUser(email: String, password: String): FirebaseUser? {
//        return try {
//            val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
//            authResult.user
//        } catch (e: Exception) {
//            null
//        }
//    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthInvalidUserException) {
            null // Return null if the user is not registered
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthUserCollisionException) {
            null // Return null if the user email already exists
        } catch (e: Exception) {
            null
        }
    }
}