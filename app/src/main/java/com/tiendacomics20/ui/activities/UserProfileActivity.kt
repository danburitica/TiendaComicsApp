package com.tiendacomics20.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tiendacomics20.R
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.User
import com.tiendacomics20.utils.Constants
import com.tiendacomics20.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        if (mUserDetails.profileCompleted == 0) {
            tv_title_user_profile.text = "Completa tu Perfil"

            et_name_user_profile.isEnabled = false
            et_name_user_profile.setText(mUserDetails.firstName)

            et_lastname_user_profile.isEnabled = false
            et_lastname_user_profile.setText(mUserDetails.lastName)

            et_email_user_profile.isEnabled = false
            et_email_user_profile.setText(mUserDetails.email)
        } else {
            setupActionBar()
            tv_title_user_profile.text = "Edita tu Perfil"
            GlideLoader(this).loadUserPicture(mUserDetails.image, iv_user_photo)

            et_name_user_profile.isEnabled = false
            et_name_user_profile.setText(mUserDetails.firstName)

            et_lastname_user_profile.isEnabled = false
            et_lastname_user_profile.setText(mUserDetails.lastName)


            et_email_user_profile.setText(mUserDetails.email)
            et_mobile_user_profile.setText(mUserDetails.mobile)
            et_address_user_profile.setText(mUserDetails.address)
        }

        iv_user_photo.setOnClickListener(this)
        btn_save_user_profile.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showErrorSnackBar("Ya tienes permiso para almacenar", false)
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save_user_profile -> {

                    if (validateUserProfileDetails()) {
                        showProgressDialog("Por favor espera...")

                        if (mSelectedImageFileUri != null)
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        val firstName = et_name_user_profile.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_lastname_user_profile.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName){
            userHashMap[Constants.FIRST_NAME] = lastName
        }

        val mobileNumber = et_mobile_user_profile.text.toString().trim { it <= ' ' }
        val address = et_address_user_profile.text.toString().trim { it <= ' ' }

        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile){
            userHashMap[Constants.MOBILE] = mobileNumber
        }
        if (address.isNotEmpty() && address != mUserDetails.address) {
            userHashMap[Constants.ADDRESS] = address
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this,
            "??Tu cuenta se actualiz?? correctamente!",
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar("El permiso para almacenar est?? permitido", false)
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "??Lo sentimos! No tienes permiso para almacenar. Puedes permitirlo desde los ajustes",
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
                    try {
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "Hubo un error al seleccionar la imagen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean{
        return when {
            TextUtils.isEmpty(et_mobile_user_profile.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor ingresa un n??mero de celular", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String){
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}