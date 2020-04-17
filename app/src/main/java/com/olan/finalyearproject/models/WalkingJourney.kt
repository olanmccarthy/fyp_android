package com.olan.finalyearproject.models

class WalkingJourney(
    override val userId: String,
    override val taskId: String,
    override val origin: Any?,
    override val destination: Any?
) : JourneyTask {
    override val carbonCost: Double = 0.0
    override val taskType = "journey"
    override val journeyType = "walk"
    override val distance = calculateDistance()

    fun calculateDistance(): Double {
        return 1.11
    }
}