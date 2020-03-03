package com.olan.finalyearproject.helpers

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.GsonBuilder
import com.olan.finalyearproject.ui.fragments.MakeHolder
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException

class JourneyTypeSpinnerListener(
    var carViews: Array<View>, var bikeViews: Array<View>, var carMakes: ArrayList<String>,
    var carMakeAdapter: ArrayAdapter<String>, var activity: Activity
    ) : AdapterView.OnItemSelectedListener{
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.getItemAtPosition(position)){
            "Bike Journey" -> {
                Log.d("olanDebug", "bike journey selected")
                carViews.forEach { item -> item.visibility = View.INVISIBLE }
                bikeViews.forEach { item -> item.visibility = View.VISIBLE }
            }
            "Car Journey" -> {
                Log.d("olanDebug", "car journey selected")
                bikeViews.forEach { item -> item.visibility = View.INVISIBLE }
                carViews.forEach { item -> item.visibility = View.VISIBLE }
                //call getjson(), update array for car make adapter, call carMakeAdapter.notifyDataSetChanged
                fetchCarMakes()
            }
            "Transit Journey" -> {
                Log.d("olanDebug", "transit journey selected")
            }
            "Walking Journey" -> {
                Log.d("olanDebug", "walking journey selected")
            }
        }
    }

    fun fetchCarMakes(){
        Log.d("olanDebug", "running fetch json")
        val url = "http://leela.netsoc.co:5001/car_makes"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
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
                Log.d(
                    "olanDebug",
                    "*********************** carMakes updated $carMakes ****************"
                )
                activity.runOnUiThread { carMakeAdapter.notifyDataSetChanged() }
            }

            override fun onFailure(request: Request?, e: IOException?) {
                Log.d("olanDebug", "failed rest api call")
                Log.d("olanDebug", e.toString())
                carMakes.add("Failed to connect to rest")
                activity.runOnUiThread { carMakeAdapter.notifyDataSetChanged() }
            }

        })
    }
}