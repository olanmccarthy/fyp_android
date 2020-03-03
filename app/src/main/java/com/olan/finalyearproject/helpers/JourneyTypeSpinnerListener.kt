package com.olan.finalyearproject.helpers

import android.util.Log
import android.view.View
import android.widget.AdapterView

class JourneyTypeSpinnerListener(
    var carViews: Array<View>, var bikeViews: Array<View>
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
            }
            "Transit Journey" -> {
                Log.d("olanDebug", "transit journey selected")
            }
            "Walking Journey" -> {
                Log.d("olanDebug", "walking journey selected")
            }
        }
    }
}