package com.olan.finalyearproject.ui.fragments


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.olan.finalyearproject.Constants.ERROR_DIALOG_REQUEST
import com.olan.finalyearproject.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.olan.finalyearproject.Constants.PERMISSION_REQUEST_ENABLE_GPS
import com.olan.finalyearproject.R
import com.olan.finalyearproject.models.BikeJourney
import com.olan.finalyearproject.models.CarJourney
import com.olan.finalyearproject.models.TaskClass
import com.olan.finalyearproject.models.WalkingJourney
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), View.OnClickListener {

    var navController: NavController? = null

    //firebase & firestore instances
    var mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    //instantiate object for getting devices last known location
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    //boolean to ensure user is granting permission to use location services
    var userLocationPermissionGranted = false
    //array that contains a user's current plan
    var currentPlanArray = ArrayList<TaskClass>()
    //boolean to check if current plan array has been loaded or not
    var currentPlanArrayLoaded = false

    lateinit var addActivityButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)


        if (checkMapServices()){
            if(userLocationPermissionGranted){
                getCurrentPlan()
                getLastKnownLocation()
            } else {
                getLocationPermission()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.addActivityButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        //case for what id is pressed
        when(v!!.id){
            //navigate to the add activity fragment
            R.id.addActivityButton -> navController!!.navigate(R.id.action_mainFragment_to_addActivityFragment)
        }
    }


    @SuppressLint("RestrictedApi")
    fun getCurrentPlan(){
        //display add activity button
        addActivityButton = view!!.findViewById(R.id.addActivityButton)
        //make add activity button visible
        addActivityButton.visibility = View.VISIBLE

        //get collection from firestore and then load the view afterwards
        if (!currentPlanArrayLoaded){
            db.collection("users").document(mAuth.currentUser?.uid!!).collection("currentPlan").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //loop through all tasks in currentPlan collection
                        for (document in task.result!!) {
                            //case for different types of task types
                            when(document.get("taskType").toString()){
                                "JourneyTask" ->{
                                    //case for different journey types
                                    when(document.get("journeyType").toString()){
                                        "bicycling" ->{
                                            val taskItem = BikeJourney(
                                                uid = mAuth.currentUser?.uid!!,
                                                destination = document.get("destination"),
                                                origin = document.get("origin"),
                                                isElectric = document.get("isElectric")
                                            )
                                            currentPlanArray.add(taskItem)
                                        }
                                        "driving" ->{
                                            val taskItem = CarJourney(
                                                uid = mAuth.currentUser?.uid!!,
                                                origin = document.get("origin"),
                                                destination = document.get("destination"),
                                                carMake = document.get("carMake").toString(),
                                                carModel = document.get("carModel").toString()
                                            )
                                            currentPlanArray.add(taskItem)
                                        }
                                        "combination" ->{}
                                        "transit" ->{}
                                        "walking" ->{
                                            val taskItem = WalkingJourney(
                                                uid = mAuth.currentUser?.uid!!,
                                                destination = document.get("destination"),
                                                origin = document.get("origin")
                                            )
                                            currentPlanArray.add(taskItem)
                                        }
                                    }
                                }
                                //TODO add handlers for different types of tasks when created
                            }
                        }
                        currentPlanArrayLoaded = true
                    } else {
                        Log.d("olanDebug", "could not get currentPlan from firestore")
                    }
                    //apply the recycler view once database has been retrieved
                    recylerView.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = TaskAdapter(currentPlanArray)
                    }
                }
        }

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

    fun getLastKnownLocation(){
        d("olanDebug", "last known location called")

        mFusedLocationClient.lastLocation.addOnCompleteListener {task ->
            if (task.isSuccessful) {
                d("olanDebug", "task is successful ${task}")
                val location = task.result
                var geoPoint = GeoPoint(0.0, 0.0)
                if (location != null){
                    geoPoint = GeoPoint(location.latitude, location.longitude)
                }
                d("olanDebug", "latitude ${geoPoint.latitude}, longitude ${geoPoint.longitude}")
            }
        }

    }

    //check if google services are available and if not try to resolve issue
    fun isServicesOk(): Boolean {
        Log.d("olanDebug", "checking if services okay")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d("olanDebug", "google play sevices working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d("olanDebug", "fixable error found in services")
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> Toast.makeText(activity, "You can't make map requests", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    //check if gps is enabled
    fun isGPSEnabled(): Boolean {
        val manager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        Log.d("olanDebug", "isGPSEnabled running")
        //TODO test if not enabling GPS breaks this forced unwrap
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("olanDebug", "no GPS")
            buildAlertMessageNoGPS()
            return false
        }
        return true
    }

    //create the alert that will show users that they need to enable GPS
    fun buildAlertMessageNoGPS(){
        val builder = AlertDialog.Builder(context!!)

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
        Log.d("olanDebug", "onActivityResult running")
        when (requestCode){
            PERMISSION_REQUEST_ENABLE_GPS -> {
                if(userLocationPermissionGranted){
                    getCurrentPlan()
                    getLastKnownLocation()
                } else {
                    getLocationPermission()
                }
            }
        }
    }

    //requests location permission, result is handled by callback function
    fun getLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            userLocationPermissionGranted = true
            getCurrentPlan()
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
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
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    userLocationPermissionGranted = true
                }
            }
        }
    }

    //listener class that will start the user settings activity to enable GPS
    inner class DialogOnClickListener(): DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val enableGPSIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(enableGPSIntent, PERMISSION_REQUEST_ENABLE_GPS )
        }
    }

}
