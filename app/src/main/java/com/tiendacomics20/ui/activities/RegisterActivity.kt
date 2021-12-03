package com.tiendacomics20.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tiendacomics20.R

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        val textView = findViewById<TextView>(R.id.tv_login)
        textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar_register_activity))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_name_register).text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar("Por favor introduzca un nombre", true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_lastname_register).text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar("Por favor introduzca un apellido", true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_email_register).text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar("Por favor introduzca un email", true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_password_register).text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar("Por favor introduzca una contraseña", true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_confirm_password).text.toString().trim {it <= ' '}) -> {
                showErrorSnackBar("Por favor confirme la contraseña", true)
                false
            }

            findViewById<EditText>(R.id.et_password_register).text.toString().trim {it <= ' '} != findViewById<EditText>(R.id.et_confirm_password).text.toString().trim {it <= ' '} -> {
                showErrorSnackBar("Las contraseñas no coinciden, verifique nuevamente", true)
                false
            }

            !findViewById<CheckBox>(R.id.cb_terms_and_condition).isChecked -> {
                showErrorSnackBar("Por favor acepte los términos y condiciones", true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser(){
        if (validateRegisterDetails()) {
            val email: String = findViewById<EditText>(R.id.et_email_register).text.toString().trim {it <= ' '}
            val password: String = findViewById<EditText>(R.id.et_password_register).text.toString().trim {it <= ' '}

            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful){
                            showErrorSnackBar("¡Su Registro fue exitoso!", false)
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
        }
    }
}