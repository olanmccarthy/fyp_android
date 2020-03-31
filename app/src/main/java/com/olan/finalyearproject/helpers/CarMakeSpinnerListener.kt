package com.olan.finalyearproject.helpers

import android.app.Activity
import android.util.Log.d
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.gson.GsonBuilder
import com.olan.finalyearproject.models.Car
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException

class CarMakeSpinnerListener(
    var carModels: ArrayList<Car>, var carModelAdapter: CarModelArrayAdapter, var activity: Activity
) : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent?.getItemAtPosition(position) != null){
            val url = "http://leela.netsoc.co:5001/car_models/${parent.getItemAtPosition(position)}"
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(request: Request?, e: IOException?) {
                    d("olanDebug", "error occured connecting to rest $e")
                }

                override fun onResponse(response: Response?) {
                    val body = response?.body()?.string()
                    val gson = GsonBuilder().create()
                    val holder = gson.fromJson(body, ModelHolder::class.java)
                    carModels.clear()
                    holder.models.forEach { item -> carModels.add(item) }
                    d("olanDebug", "************ rest response for carmakespinner $body*************")
                    activity.runOnUiThread { carModelAdapter.notifyDataSetChanged() }

                }

            })
        }
    }
}

class ModelHolder(val models: List<Car>)