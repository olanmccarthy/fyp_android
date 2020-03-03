package com.olan.finalyearproject.ui.fragments


import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.GsonBuilder
import com.olan.finalyearproject.R
import com.olan.finalyearproject.helpers.CarMakeSpinnerListener
import com.olan.finalyearproject.helpers.JourneyTypeSpinnerListener
import com.olan.finalyearproject.helpers.TaskTypeSpinnerListener
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.w3c.dom.Text
import java.io.IOException

class AddActivityFragment : Fragment() {

    //initialise lateinit vars for all view items
    lateinit var taskTypeSpinner: Spinner
    lateinit var journeyTypeTextView: TextView
    lateinit var journeyTypeSpinner: Spinner
    lateinit var originTextView: TextView
    lateinit var destinationTextView: TextView
    lateinit var isElectricSwitch: Switch
    lateinit var carMakeSpinner: Spinner
    lateinit var carModelSpinner: Spinner

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get references to items in view
        taskTypeSpinner = view.findViewById(R.id.taskTypeSpinner)
        journeyTypeSpinner = view.findViewById(R.id.journeyTypeSpinner)
        journeyTypeTextView = view.findViewById(R.id.journeyTypeTextView)
        originTextView = view.findViewById(R.id.originText)
        destinationTextView = view.findViewById(R.id.destinationText)
        isElectricSwitch = view.findViewById(R.id.isElectricSwitch)
        carMakeSpinner = view.findViewById(R.id.carMakeSpinner)
        carModelSpinner = view.findViewById(R.id.carModelSpinner)

        //create arrays of items associated with view selections
        journeyTaskViews = arrayOf(journeyTypeTextView, journeyTypeSpinner, originTextView, destinationTextView)
        carJourneyViews = arrayOf(carMakeSpinner, carModelSpinner)
        bikeJourneyViews = arrayOf(isElectricSwitch)

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

        //set the listener for both spinners to be this class
        taskTypeSpinner.onItemSelectedListener = TaskTypeSpinnerListener(journeyTaskViews)
        journeyTypeSpinner.onItemSelectedListener = JourneyTypeSpinnerListener(
            carJourneyViews, bikeJourneyViews, carMakes, carMakeAdapter, activity!!)
        carMakeSpinner.onItemSelectedListener = CarMakeSpinnerListener(carModels, carModelAdapter, activity!!)
    }

}
//data class to hold json responses to getting car makes
class MakeHolder(val makes: List<String>)