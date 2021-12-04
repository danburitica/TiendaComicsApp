package com.tiendacomics20.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tiendacomics20.R

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar_forgot_password_activity))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val email: String = findViewById<EditText>(R.id.et_email_forgot_password).text.toString().trim {it <= ' '}
            if (email.isEmpty()){
                showErrorSnackBar("Por favor introduzca un email", true)
            }else{
                showProgressDialog("Por favor espera...")
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(
                                this,
                                "El enlace fue enviado al correo electrónico",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
}