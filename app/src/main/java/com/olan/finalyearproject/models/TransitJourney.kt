package com.olan.finalyearproject.models

import java.util.*

class TransitJourney(
    override val userId: String,
    override val taskId: String,
    override val origin: Array<Double>,
    override val destination: Array<Double>,
    val departureTime: Date,
    val arrivalTime: Date,
    val passengers: Int = 0
) : JourneyTask {
    override val taskType = "journey"
    override val journeyType = "transit"

    override val distance = calculateDistance()
    override val carbonCost = calculateCarbonCost()
    val transitMode = calculateTransitMode()

    //TODO make this meaningful
    fun calculateDistance(): Double {
        return 3.33
    }
    //TODO make this meaningful
    fun calculateCarbonCost(): Double {
        return 4.44
    }

    fun calculateTransitMode(): String{
        return "Bus"
    }
}