package com.olan.finalyearproject.helpers

import android.util.Log
import android.util.Log.d
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.models.BikeJourney
import com.olan.finalyearproject.models.TaskClass
import com.olan.finalyearproject.models.WalkingJourney

class GetCurentPlan(val uid: String) {
    val db = FirebaseFirestore.getInstance()
    var currentPlanArray = ArrayList<TaskClass>()

    fun getCurrentPlan(): ArrayList<TaskClass> {
        db.collection("users").document(uid).collection("currentPlan").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //loop through all tasks in currentPlan collection
                    d("currentPlan", "task is successful")
                    for (document in task.result!!) {
                        //case for different types of task types
                        when(document.get("taskType").toString()){
                            "JourneyTask" ->{
                                d("currentPlan", "task is of type Journey")
                                //case for different journey types
                                when(document.get("journeyType").toString()){
                                    "bicycling" ->{
                                        d("currentPlan", "task is of type Bike Journey")
                                        val task = BikeJourney(
                                            uid = uid,
                                            destination = document.get("destination"),
                                            origin = document.get("origin"),
                                            isElectric = document.get("isElectric")
                                        )
                                        d("olanDebug", "adding $task to array")
                                        currentPlanArray.add(task)
                                    }
                                    "driving" ->{}
                                    "combination" ->{}
                                    "transit" ->{}
                                    "walking" ->{
                                        d("currentPlan", "task is of type WalkingJourney")
                                        val task = WalkingJourney(
                                            uid = uid,
                                            destination = document.get("destination"),
                                            origin = document.get("origin")
                                        )
                                        d("olanDebug", "adding $task to array")
                                        currentPlanArray.add(task)
                                    }
                                }
                            }
                            //TODO add handlers for different types of tasks when created
                        }
                    }
                } else {
                    Log.d("olanDebug", "could not get currentPlan from firestore")
                }

            }
        d("olanDebug", "returning $currentPlanArray")
        return currentPlanArray
    }

}