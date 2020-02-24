package com.olan.finalyearproject.models

//Class that encapsulates all Journey tasks
interface JourneyTask: TaskClass {
    //TODO make origin & destination tuple types
    val origin: Any? //where the journey starts
    val destination: Any? //where journey ends

    val journeyType: Any?
    val distance: Any?
}