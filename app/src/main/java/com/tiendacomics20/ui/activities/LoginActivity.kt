package com.tiendacomics20.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tiendacomics20.R
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.User
import com.tiendacomics20.utils.Constants
import org.w3c.dom.Text

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener(this)
        findViewById<Button>(R.id.btn_login).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_register).setOnClickListener(this)
    }
    override fun onClick(view: View?){
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                findViewById<EditText>(R.id.et_email).text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor introduzca su email", true)
                false
            }

            TextUtils.isEmpty(
                findViewById<EditText>(R.id.et_password).text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar("Por favor introduzca su contraseña", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun  logInRegisteredUser(){
        if (validateLoginDetails()) {
            showProgressDialog("Por favor espera...")

            val email = findViewById<EditText>(R.id.et_email).text.toString().trim {it <= ' '}
            val password = findViewById<EditText>(R.id.et_password).text.toString().trim {it <= ' '}

            Firebase.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User){
        hideProgressDialog()

        /*Log.i("Nombre: ", user.firstName)
        Log.i("Apellido: ", user.lastName)
        Log.i("Email: ", user.email)*/

        if (user.profileCompleted == 0){
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }else{
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}