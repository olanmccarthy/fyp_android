package com.olan.finalyearproject.models

class BikeJourney(
    override val uid: Int, override val origin: Array<Double>,
    override val destination: Array<Double>, val isElectric: Boolean
) : JourneyTask {

    override val taskType = "journey"
    override val journeyType = "bicycle"

    override val distance = calculateDistance()
    override val carbonCost = calculateCarbonCost()

    //TODO make this meaningful
    fun calculateDistance(): Double {
        return 3.33
    }
    //TODO make this meaningful
    fun calculateCarbonCost(): Double {
        return 4.44
    }
}