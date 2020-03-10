package com.olan.finalyearproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.R

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //firebase & firestore instances
    var mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var navController: NavController? = null


    //variables for nav drawer
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var headerView: View
    //id of user currently logged in
    var userId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //listen checks and returns to login screen and kills process if user isn't found
        mAuth.addAuthStateListener {
            if (mAuth.currentUser == null){
                d("olanDebug", "null user found") //TODO remove after testing
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }

        //set userId to be id of current user
        if (mAuth.currentUser != null){
            userId = mAuth.currentUser?.uid
        }

        setContentView(R.layout.activity_main)

        //nav drawer stuff
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.usernameField).text = "${mAuth.currentUser?.email}"

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    //function controlling actions taken when items in nav drawer are selected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_current_plan -> {
                Toast.makeText(this, "Current Plan clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_previous_plans -> {
                Toast.makeText(this, "Previous Plans clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                this.finish()
            }

            //TODO add actions for stats and food
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}