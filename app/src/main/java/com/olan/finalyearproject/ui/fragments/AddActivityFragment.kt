package com.olan.finalyearproject.ui.fragments


import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.olan.finalyearproject.R
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 */
class AddActivityFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var taskTypeSpinner: Spinner
    lateinit var journeyTypeTextView: TextView
    lateinit var journeyTypeSpinner: Spinner
    lateinit var originTextView: TextView
    lateinit var destinationTextView: TextView
    lateinit var isElectricSwitch: Switch
    lateinit var carMakeSpinner: Spinner
    lateinit var carModelSpinner: Spinner

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


        //set the listener for both spinners to be this class
        taskTypeSpinner.onItemSelectedListener = this
        journeyTypeSpinner.onItemSelectedListener = this

        //create adapter for viewing the task type spinner
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.TaskTypes,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            taskTypeSpinner.adapter = adapter
        }

        //create the adapter for viewing the journey type spinner
        ArrayAdapter.createFromResource(
            activity!!,
            R.array.JourneyTypes,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            journeyTypeSpinner.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //switch statement controller for task type and journey type spinners
        //TODO add separate event handlers for all the spinners
        when(parent?.getItemAtPosition(position)){
            "JourneyTask" -> {
                d("olanDebug", "journey task selected")
                journeyTypeTextView.visibility = View.VISIBLE
                journeyTypeSpinner.visibility = View.VISIBLE
                originTextView.visibility = View.VISIBLE
                destinationTextView.visibility = View.VISIBLE
            } //TODO add cases for other task types
            "Bike Journey" ->{
                d("olanDebug", "bike journey selected")
                isElectricSwitch.visibility = View.VISIBLE

            }
            "Car Journey" ->{
                d("olanDebug", "car journey selected")
                carModelSpinner.visibility = View.VISIBLE
                carMakeSpinner.visibility = View.VISIBLE

            }
            "Transit Journey" ->{
                d("olanDebug", "transit journey selected")

            }
            "Walking Journey" ->{
                d("olanDebug", "walking journey selected")

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
