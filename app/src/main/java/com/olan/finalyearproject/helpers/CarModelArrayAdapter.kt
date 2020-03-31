package com.olan.finalyearproject.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.olan.finalyearproject.models.Car

class CarModelArrayAdapter(context: Context, @LayoutRes private val layoutResource: Int, val carModels: List<Car>)
    : ArrayAdapter<Car>(context, layoutResource, carModels) {

    fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = carModels[position].model
        return view
    }
}