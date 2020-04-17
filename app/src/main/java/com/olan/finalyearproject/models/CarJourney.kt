package com.olan.finalyearproject.models

class CarJourney(
    override val userId: String,
    override val taskId: String,
    override val origin: Any?,
    override val destination: Any?,
    val carMake: String,
    val carModel: String,
    val passengers: Int = 1,
    override val distance: Long?,
    val carId: Int?
): JourneyTask {

    override val taskType = "journey"
    override val journeyType = "car"

    override val carbonCost = calculateCarbonCost()

    //TODO make this meaningful
    fun calculateCarbonCost(): Double {
        return 1.2
    }

    fun toJson(): String{
        val json = """{
            |"userId":"$userId",
            |"taskId":"$taskId",
            |"taskType":"journeyTask",
            |"journeyType":"carJourney",
            |"origin":"$origin",
            |"destination":"$destination",
            |"distance":"$distance",
            |"carMake":"$carMake",
            |"carModel":"$carModel",
            |"passengers":"$passengers",
            |"carId":"$carId"
        }""".trimMargin()
        return json
    }
}