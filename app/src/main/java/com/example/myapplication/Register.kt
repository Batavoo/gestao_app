package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity(), View.OnClickListener {

    private lateinit var mRegisterName: EditText
    private lateinit var mRegisterPhone: EditText
    private lateinit var mRegisterEmail: EditText
    private lateinit var mRegisterPassword: EditText
    private lateinit var mRegisterPasswordConfirmation: EditText
    private lateinit var mRegisterSignup: Button
    private lateinit var mRegisterSigninText: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = Firebase.auth
        mDatabase = Firebase.database

        mRegisterName = findViewById(R.id.register_editext_name)
        mRegisterPhone = findViewById(R.id.register_editext_phone)
        mRegisterEmail = findViewById(R.id.register_editext_email)
        mRegisterPassword = findViewById(R.id.register_editext_pw)
        mRegisterPasswordConfirmation = findViewById(R.id.register_editext_pwconfirm)

        mRegisterSignup = findViewById(R.id.register_button_signup)
        mRegisterSignup.setOnClickListener(this)
        mRegisterSigninText = findViewById(R.id.register_textview_signin)
        mRegisterSigninText.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.register_textview_signin -> {
                val it = Intent(this, Login::class.java)
                startActivity(it)
            }

            R.id.register_button_signup -> handlerSaveAction()

        }
    }


    private fun handlerSaveAction(){
        val name = mRegisterName.text.trim()
        val phone = mRegisterPhone.text.trim()
        val email = mRegisterEmail.text.trim()
        val password = mRegisterPassword.text.trim()
        val passwordConfirmation = mRegisterPasswordConfirmation.text.trim()

        var isFormFilled = true

        isFormFilled = isNameFilled(name) && isFormFilled
        isFormFilled = isPhoneFilled(phone) && isFormFilled
        isFormFilled = isEmailFilled(email) && isFormFilled
        isFormFilled = isPasswordFilled(password) && isFormFilled
        isFormFilled = isPasswordConfirmationFilled(passwordConfirmation) && isFormFilled

        if (isFormFilled) {

            if (password == passwordConfirmation) {

                // Guardar dados no banco de dados
                val usersRef = mDatabase.getReference("/users")
                val key = usersRef.push().key ?: ""

                val user = User(
                    id = key,
                    name = name.toString(),
                    phone = phone.toString(),
                    email = email.toString()
                )

                usersRef.child(key).setValue(user)

                // Cadastrar usuário
                mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            val dialog = AlertDialog.Builder(this)
                                .setTitle("Quallet")
                                .setMessage("User registered successfully!")
                                .setCancelable(false)
                                .setPositiveButton("OK"){dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }
                                .create()
                            dialog.show()
                        } else {
                            val dialog = AlertDialog.Builder(this)
                                .setTitle("Quallet")
                                .setMessage("An error ocurred when registering the new user. Try again")
                                .setCancelable(false)
                                .setPositiveButton("OK"){dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }
                                .create()
                            dialog.show()
                        }
                    }

            } else {
                mRegisterPasswordConfirmation.error = "The passwords don't match"
                return
            }
        }
    }

    private fun isNameFilled(name: CharSequence):Boolean {
        return if (name.isBlank()) {
            mRegisterName.error = "This field is mandatory"
            false
        } else {
            return true
        }
    }

    private fun isPhoneFilled(phone: CharSequence):Boolean {
        return if (phone.isBlank()) {
            mRegisterPhone.error = "This field is mandatory"
            false
        } else {
            return true
        }
    }

    private fun isEmailFilled(email: CharSequence):Boolean {
        return if (email.isBlank()) {
            mRegisterEmail.error = "This field is mandatory"
            false
        } else {
            return true
        }
    }

    private fun isPasswordFilled(password: CharSequence):Boolean {
        return if (password.isBlank()) {
            mRegisterPassword.error = "This field is mandatory"
            false
        } else {
            return true
        }
    }

    private fun isPasswordConfirmationFilled(passwordConfirmation: CharSequence):Boolean {
        return if (passwordConfirmation.isBlank()) {
            mRegisterPasswordConfirmation.error = "This field is mandatory"
            false
        } else {
            return true
        }
    }


}

















