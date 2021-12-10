package com.tiendacomics20.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tiendacomics20.models.Product
import com.tiendacomics20.models.User
import com.tiendacomics20.ui.activities.*
import com.tiendacomics20.ui.fragments.DashboardFragment
import com.tiendacomics20.ui.fragments.ProductsFragment
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

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.TIENDACOMICS_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when(activity){
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is AccountActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AccountActivity -> {
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

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Ocurrió un error mientras se cargaban los detalles del usuario",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String){
        val sRef: StorageReference = Firebase.storage.reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                    activity,
                    imageFileURI
                    )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "URL Imagen Firebase",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("URL Imagen Descargable", uri.toString())
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                        is AddProductActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                        activity.javaClass.simpleName,
                        exception.message,
                        exception
                    )
                }
        }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product){
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Ocurrió un error mientras se cargaba el producto",
                    e
                )
            }
    }

    fun getProductsList(fragment: Fragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Lista de productos", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when(fragment){
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Ocurrió un error mientras se cargaban los productos")
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(fragment.requireActivity().javaClass.simpleName,
                "Ocurrió un error mientras se eliminaba el producto", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if(product != null){
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Ocurrió un error mientras se obtenía el detalle del producto")
            }
    }
}