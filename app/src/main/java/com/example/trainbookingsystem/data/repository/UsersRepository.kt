package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) {

    private val accountsCollection = firestore.collection("accounts")

    suspend fun getAccount(): Account? {
        val userId: String = firebaseAuth.currentUser?.uid ?: ""
        val userDocument = accountsCollection.document(userId)
        val documentSnapshot = userDocument.get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(Account::class.java)
        } else {
            null
        }
    }

    suspend fun updateAccount(
        updatedFields: Map<String, Any>,
        password: String,
        oldPassword: String = "",
    ): String {
        val userId: String = firebaseAuth.currentUser?.uid ?: ""
        val email = firebaseAuth.currentUser?.email ?: ""
        val userDocument = accountsCollection.document(userId)

        val result = runCatching {
            userDocument.update(updatedFields).await()
            if (password.isNotEmpty() && oldPassword.isNotEmpty() && email.isNotEmpty()) {
                runCatching {
                    firebaseAuth.signInWithEmailAndPassword(email, oldPassword).await()
                }.onFailure {
                    return it.message.toString()
                }
                runCatching {
                    firebaseAuth.currentUser?.updatePassword(password)?.await()
                }.onFailure {
                    return it.message.toString()
                }
            }
            "Done"
        }.onFailure {
            return it.message.toString()
        }

        return result.getOrThrow()
    }

    suspend fun getAccountByEmail(email: String): Account? {
        val query = accountsCollection.whereEqualTo("email", email).limit(1)
        val querySnapshot = query.get().await()
        val account = querySnapshot.documents.firstOrNull()?.toObject(Account::class.java)
        Log.d("AccountRepository", "getAccountByEmail: $account")
        return account
    }


}
