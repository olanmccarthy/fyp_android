package com.olan.finalyearproject.models

import com.google.android.gms.maps.model.LatLng

class BikeJourney(
    override val userId: String = "",
    override val taskId: String = "",
    override val origin: Any? = LatLng(0.0, 0.0),
    override val destination: Any? = LatLng(0.0, 0.0),
    val isElectric: Any? = false,
    override val taskType: String = "journey",
    override val journeyType: String = "bicycle",
    override val distance: Long = 0L,
    override val carbonCost: Double = 0.0
) : JourneyTask {
    fun toJson(): String {
        val json = """{
            |"userId":"$userId",
            |"taskType":"journeyTask",
            |"journeyType":"bikeJourney",
            |"taskId":"$taskId",
            |"origin":"$origin",
            |"destination":"$destination",
            |"distance":"$distance",
            |"isElectric":"$isElectric"
            }""".trimMargin()
        return json
    }
}