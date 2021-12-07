package com.tiendacomics20.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tiendacomics20.R
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.User
import com.tiendacomics20.utils.Constants
import com.tiendacomics20.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_add_product.*

class AccountActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setupActionBar()

        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_account_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)

        toolbar_account_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails(){
        showProgressDialog("Por favor espera...")
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User){
        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this).loadUserPicture(user.image, iv_account_photo)
        tv_account_name.text = "${user.firstName} ${user.lastName}"
        tv_account_email.text = user.email
        tv_account_mobile_number.text = user.mobile
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.tv_edit -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


}