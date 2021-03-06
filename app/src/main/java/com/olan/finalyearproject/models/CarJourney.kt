package com.olan.finalyearproject.models

class CarJourney(
    override val uid: String, override val origin: Any?, override val destination: Any?,
    val carMake: String, val carModel: String, val passengers: Int = 1
): JourneyTask {

    override val taskType = "journey"
    override val journeyType = "car"

    override val distance: Double = calculateDistance()

    override val carbonCost = calculateCarbonCost()

    //TODO make this meaningful
    fun calculateCarbonCost(): Double {
        return 1.2
    }

    fun calculateDistance(): Double{
        return 2.2
    }
}