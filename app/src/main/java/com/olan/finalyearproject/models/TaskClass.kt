package com.olan.finalyearproject.models

//Abstract Class for all Tasks user can input
interface TaskClass{
    val userId: String //userId belonging to user
    val taskId: String //id of the taks itself
    val carbonCost: Any? //the carbon cost on the task
    val taskType: Any? //what type of task it is
}