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
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.olan.finalyearproject.Constants.ERROR_DIALOG_REQUEST
import com.olan.finalyearproject.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.olan.finalyearproject.Constants.PERMISSION_REQUEST_ENABLE_GPS
import com.olan.finalyearproject.R
import com.olan.finalyearproject.UserClient
import com.olan.finalyearproject.models.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.NullPointerException

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), View.OnClickListener {

    var navController: NavController? = null

    //firebase & firestore instances
    val db = FirebaseFirestore.getInstance()

    //instantiate object for getting devices last known location
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    //boolean to ensure user is granting permission to use location services
    var userLocationPermissionGranted = false
    //array that contains a user's current plan
    var currentPlanArray = ArrayList<TaskClass>()
    //boolean to check if current plan array has been loaded or not
    var currentPlanArrayLoaded = false

    lateinit var user: User

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
        if(activity != null){
            try{
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            } catch (e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }
        }

        user = ((activity!!.applicationContext) as UserClient).user!!

        if (checkMapServices()){
            if(userLocationPermissionGranted){
                getCurrentPlan()
                getLastKnownLocation()
            } else {
                getLocationPermission()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.confirmRouteButton).setOnClickListener(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recylerView)
        val mDetector = GestureDetectorCompat(activity, gestureDetector())
        recyclerView.setOnTouchListener{ v, event ->
            mDetector.onTouchEvent(event)
        }
    }

    override fun onStop() {
        currentPlanArrayLoaded = false
        super.onStop()
    }

    override fun onClick(v: View?) {
        //case for what id is pressed
        if(v != null){
            when(v.id){
                //navigate to the add activity fragment
                R.id.confirmRouteButton -> {
                    if(navController != null){
                        try{
                            navController!!.navigate(R.id.action_mainFragment_to_addActivityFragment)
                        } catch (e: NullPointerException){
                            d("olanDebug", "cannot navigate to add activity fragment, nav controller is null")
                        }
                    } else{
                        d("olanDebug", "cannot navigate to add activity fragment, nav controller is null")
                    }
                }
            }
        } else{
            d("olanDebug", "View is Null on clicking confirmRouteButton")
        }

    }


    @SuppressLint("RestrictedApi")
    fun getCurrentPlan(){

        //display add activity button
        addActivityButton = view!!.findViewById(R.id.confirmRouteButton)
        //make add activity button visible
        addActivityButton.visibility = View.VISIBLE

        //get collection from firestore and then load the view afterwards
        if (!currentPlanArrayLoaded){
            currentPlanArray = ArrayList<TaskClass>()
            db.collection("users").document(user.userId).collection("currentPlan").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //loop through all tasks in currentPlan collection
                        for (document in task.result!!) {
                            //case for different types of task types
                            when(document.get("taskType").toString()){
                                "journeyTask" ->{
                                    //case for different journey types
                                    when(document.get("journeyType").toString()){
                                        "bikeJourney" ->{
                                            val taskItem = BikeJourney(
                                                userId = user.userId,
                                                taskId = document.get("taskId") as String,
                                                destination = document.get("destination"),
                                                origin = document.get("origin"),
                                                isElectric = document.get("isElectric")
                                            )
                                            currentPlanArray.add(taskItem)
                                            if(taskItem.origin is String){
                                                d("olanDebug", "retrieved origin as string")
                                            }
                                        }
                                        "carJourney" ->{
                                            val taskItem = CarJourney(
                                                userId = user.userId,
                                                taskId = document.get("taskId") as String,
                                                origin = document.get("origin"),
                                                destination = document.get("destination"),
                                                carMake = document.get("carMake").toString(),
                                                carModel = document.get("carModel").toString(),
                                                carId = document.get("carId").toString().toInt(),
                                                distance = document.getLong("distance")
                                            )
                                            currentPlanArray.add(taskItem)
                                            if(taskItem.origin is String){
                                                d("olanDebug", "retrieved origin as string")
                                            }
                                        }
                                        "combinationJourney" ->{}
                                        "transitJourney" ->{}
                                        "walkingJourney" ->{
                                            val taskItem = WalkingJourney(
                                                userId = user.userId,
                                                taskId = document.get("taskId") as String,
                                                destination = document.get("destination"),
                                                origin = document.get("origin")
                                            )
                                            currentPlanArray.add(taskItem)
                                            if(taskItem.origin is String){
                                                d("olanDebug", "retrieved origin as string")
                                            }
                                        }
                                    }
                                }
                                //TODO add handlers for different types of tasks when created
                            }
                        }
                        user.curentPlan = currentPlanArray
                        currentPlanArrayLoaded = true
                    } else {
                        Log.d("olanDebug", "could not get currentPlan from firestore")
                    }
                    //apply the recycler view once database has been retrieved
                    d("olanDebug", "****** applying currentPlanArray $currentPlanArray to activity")
                    if(activity != null){
                        try{
                            recylerView.apply {
                                layoutManager = LinearLayoutManager(activity)
                                adapter = TaskAdapter(currentPlanArray, activity!!)
                            }
                        } catch (e: NullPointerException){
                            d("olanDebug", "null pointer on activity")
                        }
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
        //get the last known location of the device
        try{
            mFusedLocationClient.lastLocation.addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    val location = task.result
                    var geoPoint = GeoPoint(0.0, 0.0)
                    if (location != null){
                        geoPoint = GeoPoint(location.latitude, location.longitude)
                    }
                    user.lastKnownLocation = geoPoint
                }
            }
        } catch (e: NullPointerException){
            d("olanDebug", "ERROR: $e")
        }
    }

    //check if google services are available and if not try to resolve issue
    fun isServicesOk(): Boolean {
        d("olanDebug", "checking if services okay")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

        when {
            available == ConnectionResult.SUCCESS -> {
                d("olanDebug", "google play sevices working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                d("olanDebug", "fixable error found in services")
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
        d("olanDebug", "isGPSEnabled running")
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
        d("olanDebug", "onActivityResult running")
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

    public fun deleteTask(pos: Int){

    }

    //listener class that will start the user settings activity to enable GPS
    inner class DialogOnClickListener(): DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val enableGPSIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(enableGPSIntent, PERMISSION_REQUEST_ENABLE_GPS )
        }
    }

    inner class gestureDetector: GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {
            //d("olanDebug", "gesture: onShowPress")
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            //d("olanDebug", "gesture: onSingleTapUp")
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            //d("olanDebug", "gesture: onDown")
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //d("olanDebug", "gesture: onFling")
            currentPlanArrayLoaded = false
            getCurrentPlan()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
           // d("olanDebug", "gesture: onScroll")
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            //d("olanDebug", "gesture: onLongPress")
        }
    }

}