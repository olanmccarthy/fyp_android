package com.olan.finalyearproject.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olan.finalyearproject.R
import com.olan.finalyearproject.models.*
import kotlinx.android.synthetic.main.row_task.view.*

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    val carDrawable = R.drawable.ic_car_journey
    val bikeDrawable = R.drawable.ic_bike_journey
    val busDrawable = R.drawable.ic_bus_journey
    val walkDrawable = R.drawable.ic_walk_journey

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //perhaps this could be surrounded in an if statement so that different rows get inflated
        //depending on what type of task is in the list?
            //not 100% sure as it doesn't loop through the list, may have to just make a robust row_task
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.row_task, parent, false)
        return ViewHolder(layoutView)
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        //switch statement to alter depending on what type of task is in list
        when (task){
            is CarJourney -> {
                holder.view.task_title.text = task.carModel
                holder.view.task_icon.setImageDrawable(holder.view.context.getDrawable(carDrawable))
            }
            is BikeJourney -> {
                holder.view.task_title.text = "Bike Journey"
                holder.view.task_icon.setImageDrawable(holder.view.context.getDrawable(bikeDrawable))
            }
            is TransitJourney -> {
                holder.view.task_title.text = task.transitMode
                holder.view.task_icon.setImageDrawable(holder.view.context.getDrawable(busDrawable))
            }
            is WalkingJourney -> {
                holder.view.task_icon.setImageDrawable(holder.view.context.getDrawable(walkDrawable))
            }

        }
        holder.view.task_cost.text = task.carbonCost.toString()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}

