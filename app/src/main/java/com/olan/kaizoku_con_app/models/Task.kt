package com.olan.kaizoku_con_app.models

//Abstract Class for all Tasks user can input
interface Task{
    val uid: Int //UID of task
    val carbonCost: Double //the carbon cost on the task
    val taskType: String //what type of task it is
}