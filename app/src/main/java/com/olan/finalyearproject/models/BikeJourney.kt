package com.olan.finalyearproject.models

import com.google.android.gms.maps.model.LatLng

class BikeJourney(
    override val uid: String = "",
    override val origin: Any? = LatLng(0.0, 0.0),
    override val destination: Any? = LatLng(0.0, 0.0),
    val isElectric: Any? = false,
    override val taskType: String = "journey",
    override val journeyType: String = "bicycle",
    override val distance: Long = 0L,
    override val carbonCost: Double = 0.0
) : JourneyTask