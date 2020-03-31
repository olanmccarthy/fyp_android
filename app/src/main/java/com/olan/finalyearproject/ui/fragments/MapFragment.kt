package com.olan.finalyearproject.ui.fragments

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebStorage
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.olan.finalyearproject.R
import com.olan.finalyearproject.Constants.MAPVIEW_BUNDLE_KEY
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.olan.finalyearproject.UserClient
import com.olan.finalyearproject.helpers.ConfirmRouteButtonListener
import com.olan.finalyearproject.models.User

class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    lateinit var mMapView: MapView
    lateinit var user: User
    lateinit var googleMap: GoogleMap
    lateinit var mapBoundary: LatLngBounds
    lateinit var destinationText: TextView
    lateinit var originText: TextView
    lateinit var originMarker: Marker
    lateinit var destinationMarker: Marker
    lateinit var origin: LatLng
    lateinit var destination: LatLng
    lateinit var confirmButton: FloatingActionButton
    var navController: NavController? = null

    //boolean for storing whether user is choosing origin or destination on map
    var originTextSelected = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mMapView = view!!.findViewById(R.id.map)
        originText = view.findViewById(R.id.originTextView)
        destinationText = view.findViewById(R.id.destinationTextView)
        confirmButton = view.findViewById(R.id.confirmRouteButton)

        originText.setOnClickListener(this)
        destinationText.setOnClickListener(this)

        initGoogleMap(savedInstanceState)

        user = ((activity!!.applicationContext) as UserClient).user!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        confirmButton.setOnClickListener(ConfirmRouteButtonListener(navController))
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
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView.onCreate(mapViewBundle)

        mMapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
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

    override fun onMapReady(map: GoogleMap) {
        map.isMyLocationEnabled = true
        googleMap = map
        googleMap.setOnMapClickListener(this)
        setCameraView()
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

    //handle map being clicked
    override fun onMapClick(p0: LatLng?) {
        d("olanDebug", "mapClicked with latitude: ${p0?.latitude} and longitude ${p0?.longitude}")
        if(originTextSelected){
            originText.text = "Origin: ${p0?.latitude}, ${p0?.longitude}"
            if (this::originMarker.isInitialized){
               originMarker.remove()
            }
            originMarker = googleMap.addMarker(MarkerOptions().position(p0!!).title("Origin"))
            origin = p0
        } else {
            destinationText.text = "Destination: ${p0?.latitude}, ${p0?.longitude}"
            if (this::destinationMarker.isInitialized){
                destinationMarker.remove()
            }
            destinationMarker = googleMap.addMarker(MarkerOptions().position(p0!!).title("Destination"))
            destination = p0
        }
    }

    //handle text views being clicked
    override fun onClick(v: View?) {
        //swap between which attribute is being selected
        d("olanDebug", "text view clicked")
        when(v!!.id){
            R.id.destinationTextView -> {
                d("olanDebug", "destination clicked")
                originTextSelected = false
                originText.setBackgroundColor(resources.getColor(R.color.textBoxNotSelected))
                destinationText.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
            }
            R.id.originTextView -> {
                d("olanDebug", "origin clicked")
                originTextSelected = true
                originText.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
                destinationText.setBackgroundColor(resources.getColor(R.color.textBoxNotSelected))
            }
        }
    }

}