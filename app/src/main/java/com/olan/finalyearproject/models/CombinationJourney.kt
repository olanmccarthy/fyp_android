package com.olan.finalyearproject.models

class CombinationJourney(override val uid: String): TaskClass {
    //TODO lots of refactoring and thought to be put into this later
    override val taskType = "journey"
    val journies: Array<JourneyTask> = arrayOf()
    override val carbonCost = calculateCarbonCost()

    fun calculateCarbonCost(): Double {
        return 1.11
    }
}