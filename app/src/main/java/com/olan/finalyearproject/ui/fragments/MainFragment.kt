package com.olan.finalyearproject.ui.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.R
import com.olan.finalyearproject.models.BikeJourney
import com.olan.finalyearproject.models.CarJourney
import com.olan.finalyearproject.models.TaskClass
import com.olan.finalyearproject.models.WalkingJourney
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), View.OnClickListener {

    var navController: NavController? = null

    //firebase & firestore instances
    var mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        var currentPlanArray = ArrayList<TaskClass>()

        //get collection from firestore and then load the view afterwards
        //TODO encapsulate this in another function
        db.collection("users").document(mAuth.currentUser?.uid!!).collection("currentPlan").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //loop through all tasks in currentPlan collection
                    for (document in task.result!!) {
                        //case for different types of task types
                        when(document.get("taskType").toString()){
                            "JourneyTask" ->{
                                //case for different journey types
                                when(document.get("journeyType").toString()){
                                    "bicycling" ->{
                                        val task = BikeJourney(
                                            uid = mAuth.currentUser?.uid!!,
                                            destination = document.get("destination"),
                                            origin = document.get("origin"),
                                            isElectric = document.get("isElectric")
                                        )
                                        currentPlanArray.add(task)
                                    }
                                    "driving" ->{
                                        val task = CarJourney(
                                            uid = mAuth.currentUser?.uid!!,
                                            origin = document.get("origin"),
                                            destination = document.get("destination"),
                                            carMake = document.get("carMake").toString(),
                                            carModel = document.get("carModel").toString()
                                        )
                                        currentPlanArray.add(task)
                                    }
                                    "combination" ->{}
                                    "transit" ->{}
                                    "walking" ->{
                                        val task = WalkingJourney(
                                            uid = mAuth.currentUser?.uid!!,
                                            destination = document.get("destination"),
                                            origin = document.get("origin")
                                        )
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
                //apply the recycler view once database has been retrieved
                recylerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = TaskAdapter(currentPlanArray)
                }
            }

        view.findViewById<FloatingActionButton>(R.id.addActivityButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        //case for what id is pressed
        when(v!!.id){
            //navigate to the add activity fragment
            R.id.addActivityButton -> navController!!.navigate(R.id.action_mainFragment_to_addActivityFragment)
        }
    }

}
