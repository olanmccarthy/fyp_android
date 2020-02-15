package com.olan.finalyearproject.models

class CarJourney(
    override val uid: Int, override val origin: Array<Double>, override val destination: Array<Double>,
    val carMake: String, val carModel: String, val passengers: Int = 0
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