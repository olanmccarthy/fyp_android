package com.olan.finalyearproject.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.olan.finalyearproject.R

class LoginActivity: AppCompatActivity() {
    //initialise FirebaseAuth instance
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //get references to both buttons
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        //get references for both text fields
        var emailField = findViewById<TextView>(R.id.emailTextField)
        var passField = findViewById<TextView>(R.id.passwordTextField1)

        loginButton.setOnClickListener{ view ->
            signIn(view)
        }

        registerButton.setOnClickListener{
            moveToRegistration()
        }
    }
    //when register button is pressed, move to registration page
    fun moveToRegistration(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    //attempt to sign in when login button is pressed
    fun signIn(view: View){

        val email = findViewById<TextView>(R.id.emailTextField).text.toString()
        val password = findViewById<TextView>(R.id.passwordTextField1).text.toString()

        //function that displays snackbar
        d("olanDebug", "signIn running")
        showMessage(view, "Logging In...")
        d("olanDebug", "email is $email and password is $password")

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                //intent.putExtra("id", mAuth.currentUser?.email)
                startActivity(intent)
            }else {
                showMessage(view, "Error: ${task.exception?.message}")
            }
        })
    }

    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}