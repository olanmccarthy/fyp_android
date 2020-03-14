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
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.R
import com.olan.finalyearproject.UserClient
import com.olan.finalyearproject.models.User


//TODO have way to check if user is already signed in / previously signed in
class LoginActivity: AppCompatActivity() {
    //initialise FirebaseAuth instance
    var mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //get references to both buttons
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener{ view ->
            //debug_signIn(view) //TODO remove for production
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
                //set the userClient singleton to be user
                val userId = task.result!!.user!!.uid
                db.collection("users").document(userId).get()
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful){

                            var user = task.result!!.toObject(User::class.java)
                            d("olanDebug", "retrieved user instance ${user.toString()}")
                            ((applicationContext) as UserClient).user = user
                            val intent = Intent(this, MainActivity::class.java)
                            //intent.putExtra("id", mAuth.currentUser?.email)
                            startActivity(intent)
                            finish() //this prevents user from going back
                        } else {
                            showMessage(view, "Error: ${task.exception?.message}")
                        }
                    }

            } else {
                showMessage(view, "Error: ${task.exception?.message}")
            }
        })
    }
    //sign in function to when I'm debugging
    fun debug_signIn(view: View){
        mAuth.signInWithEmailAndPassword("olan@test.com", "test123").addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                //intent.putExtra("id", mAuth.currentUser?.email)
                startActivity(intent)
                finish() //this prevents user from going back
            } else {
                showMessage(view, "Error: ${task.exception?.message}")
            }
        })
    }

    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }
}