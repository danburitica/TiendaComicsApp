package com.tiendacomics20.firestore

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tiendacomics20.models.User
import com.tiendacomics20.ui.activities.LoginActivity
import com.tiendacomics20.ui.activities.RegisterActivity
import com.tiendacomics20.utils.Constants

class FirestoreClass {

    private val mFireStore = Firebase.firestore

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Ocurrió un error durante el registro :(",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = Firebase.auth.currentUser

        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                when(activity){
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Ocurrió un error obteniendo los datos del usuario :(",
                    e
                )
            }
    }
}