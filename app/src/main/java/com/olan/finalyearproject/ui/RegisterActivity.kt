package com.olan.finalyearproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.olan.finalyearproject.R

class RegisterActivity : AppCompatActivity() {
    //initialise FirebaseAuth instance
    var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //get reference to register button
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener{ view ->
            registerUser(view)
        }
    }
    //registers user if passwords match, throws error otherwise
    fun registerUser(view: View){
        val email = findViewById<TextView>(R.id.emailTextField).text.toString()
        val firstPass = findViewById<TextView>(R.id.passwordTextField1).text.toString()
        val secondPass = findViewById<TextView>(R.id.passwordTextField2).text.toString()

        if (firstPass == secondPass){
            mAuth.createUserWithEmailAndPassword(email, firstPass).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful){
                    val intent = Intent(this, LoginActivity::class.java)
                    //TODO when going to login activity have fields auto filled
                    startActivity(intent)
                    finish() //this stops user from going back
                } else {
                    showMessage(view, "Error: ${task.exception?.message}")
                }
            })
        } else {
            showMessage(view, "Passwords do not match!")
        }
    }

    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}