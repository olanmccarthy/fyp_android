package com.olan.finalyearproject.models

//Abstract Class for all Tasks user can input
interface TaskClass{
    val uid: String //UID of task
    val carbonCost: Any? //the carbon cost on the task
    val taskType: Any? //what type of task it is
}