package com.tiendacomics20.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tiendacomics20.R
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.Product
import com.tiendacomics20.utils.Constants
import com.tiendacomics20.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageURI: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        setupActionBar()

        iv_add_update_product.setOnClickListener(this)
        btn_add_product.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_product_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when (v.id){
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    ){
                        Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun uploadProductImage(){
        showProgressDialog("Por favor espera...")
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageURI, Constants.PRODUCT_IMAGE)
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this,
            "¡Producto cargado con éxito!",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun imageUploadSuccess(imageURL: String){
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(
            Constants.TIENDACOMICS_PREFERENCES, Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim { it <= ' ' },
            et_product_price.text.toString().trim { it <= ' ' },
            et_product_description.text.toString().trim { it <= ' ' },
            et_product_quantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this, product)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar("El permiso para almacenar está permitido", false)
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "¡Lo sentimos! No tienes permiso para almacenar. Puedes permitirlo desde los ajustes",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))

                    mSelectedImageURI = data.data!!

                    try {
                        GlideLoader(this).loadProductPicture(mSelectedImageURI!!, iv_product_image)
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageURI == null -> {
                showErrorSnackBar("Por favor selecciona una imagen para tu producto", true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor ingresa un nombre para tu producto", true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor ingresa un precio para tu producto", true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor ingresa una descripción para tu producto",true)
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor ingresa una cantidad para tu producto",true)
                false
            }
            else -> {
                true
            }
        }
    }

}