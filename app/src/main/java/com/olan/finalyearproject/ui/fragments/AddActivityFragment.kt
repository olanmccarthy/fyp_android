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
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.w3c.dom.Text
import java.io.IOException

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

    lateinit var carMakeAdapter: ArrayAdapter<String>
    lateinit var carModelAdapter: ArrayAdapter<String>

    var carMakes = arrayListOf("None")
    var carModels = arrayListOf("None")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fetchJson()
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
        taskTypeSpinner.onItemSelectedListener = this
        journeyTypeSpinner.onItemSelectedListener = this
        carMakeSpinner.onItemSelectedListener = CarMakeSpinnerListener(carModels, carModelAdapter, activity!!)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //switch statement controller for task type and journey type spinners
        //TODO add separate event handlers for all the spinners

        //create arrays pointing to each collection of views associated with journey type to simplify
        //altering visibility
        val carViews = arrayOf(carMakeSpinner, carModelSpinner)
        val bikeViews = arrayOf(isElectricSwitch)

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
                carViews.forEach { item -> item.visibility = View.INVISIBLE }
                bikeViews.forEach { item -> item.visibility = View.VISIBLE }
            }
            "Car Journey" ->{
                d("olanDebug", "car journey selected")
                carViews.forEach { item -> item.visibility = View.VISIBLE }
                //call getjson(), update array for car make adapter, call carMakeAdapter.notifyDataSetChanged

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

    fun fetchJson(){
        d("olanDebug","running fetch json")
        val url = "http://leela.netsoc.co:5001/car_makes"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{
            override fun onResponse(response: Response?) {
                //get json response and cast to string
                val body = response?.body()?.string()
                //convert json response to data class
                val gson = GsonBuilder().create()
                val holder = gson.fromJson(body, MakeHolder::class.java)
                //add items from data class to array carMake adapter uses
                holder.makes.forEach { item -> carMakes.add(item) }
                //make carMake Adapter aware of updates to list
                //this needs to be run on UiThread
                d("olanDebug", "*********************** carMakes updated $carMakes ****************")
                activity!!.runOnUiThread { carMakeAdapter.notifyDataSetChanged() }
            }

            override fun onFailure(request: Request?, e: IOException?) {
                d("olanDebug", "failed rest api call")
                d("olanDebug", e.toString())
                carMakes.add("Failed to connect to rest")
                activity!!.runOnUiThread { carMakeAdapter.notifyDataSetChanged() }
            }

        })
    }

}
//data class to hold json responses to getting car makes
class MakeHolder(val makes: List<String>)