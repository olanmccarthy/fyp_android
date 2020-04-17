package com.olan.finalyearproject.models

import com.google.firebase.firestore.GeoPoint

//data class to be instantiated as a singleton
class User(
    val email: String = "",
    val userId: String = "",
    var lastKnownLocation: GeoPoint = GeoPoint(0.0, 0.0),
    var curentPlan: ArrayList<TaskClass> = ArrayList()
)