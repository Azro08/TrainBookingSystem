package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {


    suspend fun login(email: String, password: String): String {
        val deferred = CompletableDeferred<String>()
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deferred.complete("Done")
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        deferred.complete(errorMessage)
                    }
                }
        } catch (e: Exception) {
            deferred.complete(e.message ?: "Unknown error")
        }

        return deferred.await()
    }


    suspend fun signup(email: String, password: String): String {
        val deferred = CompletableDeferred<String>()

        try {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deferred.complete(task.result.user?.uid ?: "")
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error"
                        deferred.complete(errorMessage)
                    }
                }
        } catch (e: Exception) {
            deferred.complete(e.message ?: "Unknown error")
        }

        return deferred.await()
    }

    suspend fun getUserRole(): String? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            return try {
                val usersCollection = firestore.collection("accounts")
                val userDocument = usersCollection.document(uid)
                val documentSnapshot = userDocument.get().await()
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(Account::class.java)
                    Log.d("getUserRole", userData.toString())
                    userData?.role
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        // Return null if the user is not logged in or any error occurs
        return null
    }

    suspend fun saveUser(account: Account): String {
        val usersCollection = firestore.collection("accounts")
        return try {
            // Firestore-specific logic here
            val userDocRef = usersCollection.document(account.id)
            userDocRef.set(account).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}