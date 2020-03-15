package com.olan.finalyearproject.models

class BikeJourney(
    override val uid: String = "", override val origin: Any? = 0.0,
    override val destination: Any? = 0.0, val isElectric: Any? = false
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