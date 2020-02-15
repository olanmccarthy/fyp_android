package com.olan.finalyearproject.models

//Class that encapsulates all Journey tasks
interface JourneyTask: Task {
    //TODO make origin & destination tuple types
    val origin: Array<Double> //where the journey starts
    val destination: Array<Double> //where journey ends

    val journeyType: String
    val distance: Double
}