package com.olan.finalyearproject.models

class WalkingJourney(
    override val uid: Int, override val origin: Array<Double>,
    override val destination: Array<Double>
) : JourneyTask {
    override val carbonCost: Double = 0.0
    override val taskType = "journey"
    override val journeyType = "walk"
    override val distance = calculateDistance()

    fun calculateDistance(): Double {
        return 1.11
    }
}