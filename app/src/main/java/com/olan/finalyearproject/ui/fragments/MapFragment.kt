package com.olan.finalyearproject.ui.fragments

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.olan.finalyearproject.R
import com.olan.finalyearproject.Constants.MAPVIEW_BUNDLE_KEY
import com.google.android.gms.maps.model.MarkerOptions
import com.olan.finalyearproject.UserClient
import com.olan.finalyearproject.models.User

class MapFragment: Fragment(), OnMapReadyCallback {

    lateinit var mMapView: MapView
    lateinit var user: User
    lateinit var googleMap: GoogleMap
    lateinit var mapBoundary: LatLngBounds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mMapView = view!!.findViewById(R.id.map) as MapView
        initGoogleMap(savedInstanceState)
        user = ((activity!!.applicationContext) as UserClient).user!!
        return view
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
        map.addMarker(MarkerOptions().position(LatLng(user.lastKnownLocation.latitude, user.lastKnownLocation.longitude)).title("Marker"))
        map.isMyLocationEnabled = true
        googleMap = map
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


}