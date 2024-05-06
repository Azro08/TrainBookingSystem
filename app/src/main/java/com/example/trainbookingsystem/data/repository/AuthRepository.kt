package com.example.trainbookingsystem.data.repository

import android.content.Context
import android.util.Log
import com.example.trainbookingsystem.R
import com.example.trainbookingsystem.data.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val usersRepository: UsersRepository,
    @ApplicationContext private val context: Context
) {


    suspend fun login(
        email: String,
        password: String,
        coroutineScope: CoroutineScope
    ): String {
        return suspendCoroutine { continuation ->
            coroutineScope.launch {
                val account = usersRepository.getAccountByEmail(email)
                if (account != null) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume("Done")
                            } else {
                                val errorMessage = context.getString(R.string.incorrect_password)
                                continuation.resume(errorMessage)
                            }
                        }
                } else {
                    continuation.resume(context.getString(R.string.account_not_found))
                }
            }
        }
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