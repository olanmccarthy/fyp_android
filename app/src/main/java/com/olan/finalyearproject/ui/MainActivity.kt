package com.olan.finalyearproject.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.Constants
import com.olan.finalyearproject.R

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //firebase & firestore instances
    var mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val constants = Constants()
    var navController: NavController? = null

    //boolean to ensure user is granting permission to use location services
    var userLocationPermissionGranted = false


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
        checkMapServices()
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

    //check that both services and gps are enabled
    fun checkMapServices(): Boolean{
        if(isServicesOk()){
            if(isGPSEnabled()){
                return true
            }
        }
        return false
    }

    //check if google services are available and if not try to resolve issue
    fun isServicesOk(): Boolean {
        d("olanDebug", "checking if services okay")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        when {
            available == ConnectionResult.SUCCESS -> {
                d("olanDebug", "google play sevices working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                d("olanDebug", "fixable error found in services")
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, constants.ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    //check if gps is enabled
    fun isGPSEnabled(): Boolean {
        val manager = getSystemService( LOCATION_SERVICE) as LocationManager?
        d("olanDebug", "isGPSEnabled running")
        //TODO test if not enabling GPS breaks this forced unwrap
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            d("olanDebug", "no GPS")
            buildAlertMessageNoGPS()
            return false
        }
        return true
    }

    //create the alert that will show users that they need to enable GPS
    fun buildAlertMessageNoGPS(){
        val builder = AlertDialog.Builder(this)

        builder.setMessage("This application requires GPS to work properly, would you like to enable it?")
            .setCancelable(false)
                //this button will bring up location settings
            .setPositiveButton("Yes", DialogOnClickListener())

        val alert = builder.create()
        alert.show()
    }

    //logic to handle what happens after user is finished with location settings
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        d("olanDebug", "onActivityResult running")
        when (requestCode){
            constants.PERMISSION_REQUEST_ENABLE_GPS -> {
                if(userLocationPermissionGranted){
                    getChatrooms() //TODO replace this with proper function
                } else {
                    getLocationPermission()
                }
            }
        }
    }

    //requests location permission, result is handled by callback function
    fun getLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            userLocationPermissionGranted = true
            getChatrooms() //todo replace with proper function
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    //handles logic for after permissions have been requested
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        userLocationPermissionGranted = false
        when (requestCode){
            constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    userLocationPermissionGranted = true
                }
            }
        }
    }

    //todo remove this
    fun getChatrooms(){
        Toast.makeText(this, "ALL PERMISSIONS GRANTED", Toast.LENGTH_SHORT).show()
    }

    //listener class that will start the user settings activity to enable GPS
    inner class DialogOnClickListener(): DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val enableGPSIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(enableGPSIntent, constants.PERMISSION_REQUEST_ENABLE_GPS )
        }
    }
}