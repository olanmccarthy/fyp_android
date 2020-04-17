package com.olan.finalyearproject.ui.fragments

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.olan.finalyearproject.Constants
import com.olan.finalyearproject.Constants.GOOGLE_MAPS_API_KEY
import com.olan.finalyearproject.R
import com.olan.finalyearproject.UserClient
import com.olan.finalyearproject.helpers.*
import com.olan.finalyearproject.models.BikeJourney
import com.olan.finalyearproject.models.Car
import com.olan.finalyearproject.models.CarJourney
import com.olan.finalyearproject.models.User
import com.squareup.okhttp.*
import java.io.IOException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class AddActivityFragment : Fragment(), View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    //view items
    lateinit var taskTypeSpinner: Spinner
    lateinit var journeyTypeTextView: TextView
    lateinit var journeyTypeSpinner: Spinner
    lateinit var isElectricSwitch: Switch
    lateinit var carMakeSpinner: Spinner
    lateinit var carModelSpinner: Spinner
    lateinit var addActivityButton: FloatingActionButton
    lateinit var mapButton: Button
    lateinit var originText: TextView
    lateinit var destinationText: TextView
    lateinit var mapLayout: ConstraintLayout
    lateinit var formLayout: ConstraintLayout
    lateinit var bikeLabel: TextView

    //map view items
    lateinit var mMapView: MapView
    lateinit var user: User
    lateinit var googleMap: GoogleMap
    lateinit var mapBoundary: LatLngBounds
    lateinit var destinationTextMap: TextView
    lateinit var originTextMap: TextView
    lateinit var originMarker: Marker
    lateinit var destinationMarker: Marker
    lateinit var confirmButton: FloatingActionButton

    //direction items
    lateinit var geoApiContext: GeoApiContext

    //data variables
    lateinit var origin: LatLng
    lateinit var destination: LatLng
    var duration: Long? = null //duration of journey in seconds
    var distance: Long? = null //distance of journey in metres
    var selectedCarId: Int? = null

    //boolean for storing whether user is choosing origin or destination on map
    var originTextSelected = true

    //collections of views
    lateinit var journeyTaskViews: Array<View>
    lateinit var carJourneyViews: Array<View>
    lateinit var bikeJourneyViews: Array<View>

    //adapters for spinners
    lateinit var carMakeAdapter: ArrayAdapter<String>
    lateinit var carModelAdapter: CarModelArrayAdapter

    //arrays to contain car models and makes
    var carMakes = arrayListOf("None")
    var carModels = arrayListOf(Car(0, "None"))

    //navigation controller
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_activity, container, false)
        //map view must be set here
        mMapView = view.findViewById(R.id.map)
        initGoogleMap(savedInstanceState)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //assign view items to variables
        assignViewItems(view)
        loadSpinners(view)

    }

    //set functionality for map
    override fun onMapReady(map: GoogleMap) {
        map.isMyLocationEnabled = true
        googleMap = map
        googleMap.setOnMapClickListener(this)
        setCameraView()
    }

    //set the camera on the map to be zoomed in around user location
    fun setCameraView(){
        val bottomBoundary = user.lastKnownLocation.latitude - 0.1
        val leftBoundary = user.lastKnownLocation.longitude - 0.1
        val topBoundary = user.lastKnownLocation.latitude + 0.1
        val rightBoundary = user.lastKnownLocation.longitude + 0.1

        mapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary), LatLng(topBoundary, rightBoundary)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapBoundary.center, 12F))
    }

    fun initGoogleMap(savedInstanceState: Bundle?){
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        }

        mMapView.onCreate(mapViewBundle)

        mMapView.getMapAsync(this)

        if (! ::geoApiContext.isInitialized){
            geoApiContext = GeoApiContext.Builder().apiKey(GOOGLE_MAPS_API_KEY).build()
        }
    }

    //functionality for add activity button being pressed and when destination and origin text blocks are clicked
    //easier to make the fragment itself the listener as there are a lot of different variables at work depending on selection
    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.addActivityButton -> {

                    //what type of task is being created
                    when(taskTypeSpinner.selectedItem.toString()){
                        "JourneyTask" ->{
                            //any journey task needs origin and destination
                            if (::origin.isInitialized && ::destination.isInitialized){
                                var json = "{}"
                                //what type of journey is being created
                                when(journeyTypeSpinner.selectedItem.toString()){
                                    "Bike Journey" ->{
                                        val item = BikeJourney(
                                            userId = user.userId,
                                            taskId = UUID.randomUUID().toString(),
                                            origin = origin,
                                            destination = destination,
                                            isElectric = isElectricSwitch.isChecked,
                                            distance = distance!!
                                        )
                                        //save bike item, go to main fragment, make rest api call to calculate carbon cost of task
                                        //update ui when cost is calculated & store on firebase
                                        json = item.toJson()
                                    }
                                    "Car Journey" ->{
                                        if ( carMakeSpinner.selectedItem.toString() != "None" && carModelSpinner.selectedItem.toString() != "None"){
                                            val item = CarJourney(
                                                userId = user.userId,
                                                taskId = UUID.randomUUID().toString(),
                                                origin = origin,
                                                destination = destination,
                                                carMake = carMakeSpinner.selectedItem.toString(),
                                                carModel = carModelSpinner.selectedItem.toString(),
                                                distance = distance,
                                                carId = selectedCarId
                                            )

                                            json = item.toJson()
                                        } else {
                                            d("olanDebug", "car journey - all conditions not met")
                                        }
                                    }
                                    "Transit Journey" ->{

                                    }
                                    "Walking Journey" ->{

                                    }

                                }
                                val JSON = MediaType.parse("application/json; charset=utf-8")
                                val client = OkHttpClient()
                                val request = Request.Builder()
                                    .url("http://leela.netsoc.co:5001/carbonCost")
                                    .post(RequestBody.create(JSON, json))
                                    .build()
                                d("olanDebug", "making call")
                                client.newCall(request).enqueue(object: Callback {
                                    override fun onFailure(request: Request?, e: IOException?) {
                                        d("olanDebug", "something went wrong with restapi call")
                                    }

                                    override fun onResponse(response: Response?) {
                                        val body = response?.body()?.string()
                                        if(navController != null){
                                            try {
                                                navController!!.navigate(R.id.action_addActivityFragment_to_mainFragment)
                                            } catch (e: NullPointerException){
                                                d("olanDebug", "Error navigating to main fragment $e")
                                            }

                                        } else {
                                            d("olanDebug", "NavController is null, cannot go to Main Fragment")
                                        }

                                    }

                                })
                            } else { //origin or destination not initialised
                                d("olanDebug", "JourneyTask needs origin and destination to be in place")
                            }
                        }
                        //TODO add handling for other task types
                    }
                }
                R.id.destinationTextView -> { //choosing destination on map view
                    originTextSelected = false
                    originTextMap.setBackgroundColor(resources.getColor(R.color.textBoxNotSelected))
                    destinationTextMap.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
                }
                R.id.originTextView -> { //choosing origin on map view
                    originTextSelected = true
                    originTextMap.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
                    destinationTextMap.setBackgroundColor(resources.getColor(R.color.textBoxNotSelected))
                }
                R.id.confirmRouteButton -> { //destination and origin have been chosen, remove map view
                    mapLayout.visibility = View.GONE
                    formLayout.visibility = View.VISIBLE
                }
            }
        }

    }

    //handle map being clicked
    override fun onMapClick(p0: LatLng?) {
        if(originTextSelected){
            originTextMap.text = "Origin: ${p0?.latitude}, ${p0?.longitude}"
            originText.text = "${p0?.latitude}, ${p0?.longitude}"
            if (this::originMarker.isInitialized){
                originMarker.remove()
            }
            if(p0 != null){
                originMarker = googleMap.addMarker(MarkerOptions().position(p0).title("Origin"))
                origin = p0
            } else {
                d("olanDebug", "p0 is null")
            }

        } else {
            destinationTextMap.text = "Destination: ${p0?.latitude}, ${p0?.longitude}"
            destinationText.text = "${p0?.latitude}, ${p0?.longitude}"
            if (this::destinationMarker.isInitialized){
                destinationMarker.remove()
            }
            if(p0 != null){
                destinationMarker = googleMap.addMarker(MarkerOptions().position(p0).title("Destination"))
                destination = p0
            } else {
                d("olanDebug", "p0 is null")
            }

        }
        calculateDirections()
    }

    //calculate directions from origin to destination on map view
    fun calculateDirections(){
        val directions = DirectionsApiRequest(geoApiContext)
        directions.alternatives(true)
        if(::origin.isInitialized && ::destination.isInitialized){
            directions.origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
            directions.destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .setCallback(object: PendingResult.Callback<DirectionsResult>{

                    override fun onResult(result: DirectionsResult?) {
                        if(result != null){
                            d("olanDebug", "routes: ${result.routes[0]}")
                            d("olanDebug", "duration: ${result.routes[0].legs[0].duration}")
                            duration = result.routes[0].legs[0].duration.inSeconds
                            d("olanDebug", "distance: ${result.routes[0].legs[0].distance}")
                            distance = result.routes[0].legs[0].distance.inMeters
                        }
                    }

                    override fun onFailure(e: Throwable?) {
                        if(e != null){
                            d("olanDebug", "failed to get directions")
                        }
                    }
                })
        } else {
            d("olanDebug", "origin and destination not initialised")
        }
    }

    //assign all view variables
    fun assignViewItems(view: View){
        //get nav controller to enable moving to map fragment
        navController = Navigation.findNavController(view)

        //get references to items in form view
        taskTypeSpinner = view.findViewById(R.id.taskTypeSpinner)
        journeyTypeSpinner = view.findViewById(R.id.journeyTypeSpinner)
        journeyTypeTextView = view.findViewById(R.id.journeyTypeTextView)
        originText = view.findViewById(R.id.originText)
        destinationText = view.findViewById(R.id.destinationText)
        isElectricSwitch = view.findViewById(R.id.isElectricSwitch)
        carMakeSpinner = view.findViewById(R.id.carMakeSpinner)
        carModelSpinner = view.findViewById(R.id.carModelSpinner)
        addActivityButton = view.findViewById(R.id.addActivityButton)
        mapButton = view.findViewById(R.id.mapButton)
        mapLayout = view.findViewById(R.id.mapLayout)
        formLayout = view.findViewById(R.id.formLayout)
        bikeLabel = view.findViewById(R.id.isElectricText)

        //items in map view
        originTextMap = view.findViewById(R.id.originTextView)
        destinationTextMap = view.findViewById(R.id.destinationTextView)
        confirmButton = view.findViewById(R.id.confirmRouteButton)

        //create arrays of items associated with view selections
        journeyTaskViews = arrayOf(journeyTypeTextView, journeyTypeSpinner)
        carJourneyViews = arrayOf(carMakeSpinner, carModelSpinner, originText, destinationText, mapButton)
        bikeJourneyViews = arrayOf(isElectricSwitch, originText, destinationText, mapButton, bikeLabel)

        user = ((activity!!.applicationContext) as UserClient).user!!
    }

    //load values into spinners, create adapters, set listeners etc
    fun loadSpinners(view: View){
        //create adapter for viewing the task type spinner
        if(activity != null){

            try{
                val taskTypeAdapter = ArrayAdapter.createFromResource(
                    activity!!, R.array.TaskTypes, android.R.layout.simple_spinner_dropdown_item
                )
                taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                taskTypeSpinner.adapter = taskTypeAdapter
                taskTypeSpinner.onItemSelectedListener = TaskTypeSpinnerListener(journeyTaskViews)
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }

            try{
                val journeyTypeAdapter = ArrayAdapter.createFromResource(
                    activity!!, R.array.JourneyTypes, android.R.layout.simple_spinner_dropdown_item
                )
                journeyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                journeyTypeSpinner.adapter = journeyTypeAdapter
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }

            try{
                carMakeAdapter = ArrayAdapter(
                    activity!!, android.R.layout.simple_spinner_dropdown_item, carMakes
                )
                carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                carMakeSpinner.adapter = carMakeAdapter
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }

            try{
                carModelAdapter  = CarModelArrayAdapter(
                    activity!!, android.R.layout.simple_spinner_dropdown_item, carModels
                )
                carModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                carModelSpinner.adapter = carModelAdapter
                carModelSpinner.onItemSelectedListener = CarModelSpinnerListener(carModels)
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }

            try{
                journeyTypeSpinner.onItemSelectedListener = JourneyTypeSpinnerListener(
                    carJourneyViews, bikeJourneyViews, carMakes, carMakeAdapter, activity!!)
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }

            try{
                carMakeSpinner.onItemSelectedListener = CarMakeSpinnerListener(carModels, carModelAdapter, activity!!)
            } catch(e: NullPointerException){
                d("olanDebug", "Activity is null $e")
            }
        } else {
            d("olanDebug", "Could not load spinners, adapters because Activity is null")
        }

        //set the listeners for various views
        mapButton.setOnClickListener(MapsButtonListener(formLayout, mapLayout))
        addActivityButton.setOnClickListener(this)
        confirmButton.setOnClickListener(this)
        originTextMap.setOnClickListener(this)
        destinationTextMap.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    inner class CarModelSpinnerListener(
        var carModels: ArrayList<Car>
    ) : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            d("olanDebug", "nothing selected for car model")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedCarId = carModels[position].id
            d("olanDebug", "selected id $selectedCarId")
        }
    }

}
//data class to hold json responses to getting car makes
class MakeHolder(val makes: List<String>)