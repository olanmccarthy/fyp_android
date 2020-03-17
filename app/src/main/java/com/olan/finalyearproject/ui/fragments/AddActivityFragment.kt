package com.olan.finalyearproject.ui.fragments

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.olan.finalyearproject.R
import com.olan.finalyearproject.helpers.*

class AddActivityFragment : Fragment(), View.OnClickListener{

    //initialise lateinit vars for all view items
    lateinit var taskTypeSpinner: Spinner
    lateinit var journeyTypeTextView: TextView
    lateinit var journeyTypeSpinner: Spinner
    lateinit var isElectricSwitch: Switch
    lateinit var carMakeSpinner: Spinner
    lateinit var carModelSpinner: Spinner
    lateinit var addActivityButton: FloatingActionButton
    lateinit var mapButton: Button
    lateinit var originText: TextView
    lateinit var destinationText: TextView

    //initialise lateinit arrays for collections of views
    lateinit var journeyTaskViews: Array<View>
    lateinit var carJourneyViews: Array<View>
    lateinit var bikeJourneyViews: Array<View>

    //initialise lateinit adapters for spinners
    lateinit var carMakeAdapter: ArrayAdapter<String>
    lateinit var carModelAdapter: ArrayAdapter<String>

    //arrays to contain car models and makes
    var carMakes = arrayListOf("None")
    var carModels = arrayListOf("None")

    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get nav controller to enable moving to map fragment
        navController = Navigation.findNavController(view)

        //get references to items in view
        taskTypeSpinner = view.findViewById(R.id.taskTypeSpinner)
        journeyTypeSpinner = view.findViewById(R.id.journeyTypeSpinner)
        journeyTypeTextView = view.findViewById(R.id.journeyTypeTextView)
        originText = view.findViewById(R.id.originText)
        destinationText = view.findViewById(R.id.destinationText)
        isElectricSwitch = view.findViewById(R.id.isElectricSwitch)
        carMakeSpinner = view.findViewById(R.id.carMakeSpinner)
        carModelSpinner = view.findViewById(R.id.carModelSpinner)
        addActivityButton = view.findViewById(R.id.addActivityButton)
        mapButton = view.findViewById(R.id.mapButton)

        //create arrays of items associated with view selections
        journeyTaskViews = arrayOf(journeyTypeTextView, journeyTypeSpinner)
        carJourneyViews = arrayOf(carMakeSpinner, carModelSpinner, originText, destinationText, mapButton)
        bikeJourneyViews = arrayOf(isElectricSwitch, originText, destinationText, mapButton)

        //create adapter for viewing the task type spinner
        val taskTypeAdapter = ArrayAdapter.createFromResource(
            activity!!, R.array.TaskTypes, android.R.layout.simple_spinner_dropdown_item
        )
        taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskTypeSpinner.adapter = taskTypeAdapter


        val journeyTypeAdapter = ArrayAdapter.createFromResource(
            activity!!, R.array.JourneyTypes, android.R.layout.simple_spinner_dropdown_item
        )
        journeyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        journeyTypeSpinner.adapter = journeyTypeAdapter

        carMakeAdapter = ArrayAdapter(
            activity!!, android.R.layout.simple_spinner_dropdown_item, carMakes
        )
        carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        carMakeSpinner.adapter = carMakeAdapter

        carModelAdapter  = ArrayAdapter(
            activity!!, android.R.layout.simple_spinner_dropdown_item, carModels
        )
        carModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        carModelSpinner.adapter = carModelAdapter

        //set the listeners for various views
        taskTypeSpinner.onItemSelectedListener = TaskTypeSpinnerListener(journeyTaskViews)
        journeyTypeSpinner.onItemSelectedListener = JourneyTypeSpinnerListener(
            carJourneyViews, bikeJourneyViews, carMakes, carMakeAdapter, activity!!)
        carMakeSpinner.onItemSelectedListener = CarMakeSpinnerListener(carModels, carModelAdapter, activity!!)

        //set listeners for our buttons
        mapButton.setOnClickListener(MapsButtonListener(navController!!))
        addActivityButton.setOnClickListener(this)
    }

    //functionality for add activity button being pressed
    //easier to make the fragment itself the listener as there are a lot of different variables at work depending on selection
    override fun onClick(v: View?) {
        //what type of task is being created
        when(taskTypeSpinner.selectedItem.toString()){
            "JourneyTask" ->{
                //what type of journey is being created
                when(journeyTypeSpinner.selectedItem.toString()){
                    "Bike Journey" ->{
                        if(
                            originText.text.toString().trim().isNotEmpty() && destinationText.text.toString().trim().isNotEmpty()
                        ){
                            d("olanDebug", "bike journey - origin: ${originText.text} destination: ${destinationText.text} isElectric: ${isElectricSwitch.showText}")
                        } else {
                            d("olanDebug", "bike journey - origin or destination are blank")
                        }
                    }
                    "Car Journey" ->{
                        if (
                            originText.text.toString().trim().isNotEmpty() && destinationText.text.toString().trim().isNotEmpty() &&
                                    carMakeSpinner.selectedItem.toString() != "None" && carModelSpinner.selectedItem.toString() != "None"

                        ){
                            d("olanDebug", "car journey - origin: ${originText.text} destination: ${destinationText.text} carMake: ${carMakeSpinner.selectedItem} carModel: ${carModelSpinner.selectedItem}")
                        } else {
                            d("olanDebug", "car journey - all conditions not met")
                        }
                    }
                    "Transit Journey" ->{

                    }
                    "Walking Journey" ->{

                    }
                }
            }
            //TODO add handling for other task types
        }
    }

}
//data class to hold json responses to getting car makes
class MakeHolder(val makes: List<String>)