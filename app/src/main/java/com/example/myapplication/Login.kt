package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail: EditText
    private lateinit var mLoginPassword: EditText
    private lateinit var mLoginRegister: TextView
    private lateinit var mLoginSignin: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Firebase Authentication
        mAuth = Firebase.auth

        mLoginEmail = findViewById(R.id.login_editext_email)
        mLoginPassword = findViewById(R.id.login_editext_pw)

        mLoginRegister = findViewById(R.id.login_textview_signup)
        mLoginRegister.setOnClickListener(this)

        mLoginSignin = findViewById(R.id.login_button_signin)
        mLoginSignin.setOnClickListener(this)



    }

    override fun onClick(view: View?) {
        when (view?.id){

            R.id.login_textview_signup -> {
                val it = Intent(this, Register::class.java)
                startActivity(it)
            }

            R.id.login_button_signin -> {
                val email = mLoginEmail.text.toString()
                val password = mLoginPassword.text.toString()

                if (email.isBlank()){
                    mLoginEmail.error = "This field is mandatory"
                    return
                }

                if (password.isBlank()){
                    mLoginPassword.error = "This field is mandatory"
                    return
                }

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            val it = Intent(this, MainActivity::class.java)
                            startActivity(it)
                        } else {
                            showDialog("Invalid user or password")
                        }
                    }
            }
        }
    }

    private fun showDialog (message:String){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Management App")
            .setMessage(message)
            .setCancelable(false)

            .setPositiveButton("OK"){dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }


}


















