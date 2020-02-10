package com.olan.kaizoku_con_app.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olan.kaizoku_con_app.R
import com.olan.kaizoku_con_app.models.BikeJourney
import com.olan.kaizoku_con_app.models.CarJourney
import com.olan.kaizoku_con_app.models.Task
import kotlinx.android.synthetic.main.row_task.view.*

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    val carDrawable = R.drawable.ic_car_journey
    val bikeDrawable = R.drawable.ic_bike_journey

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

        }
        holder.view.task_cost.text = task.carbonCost.toString()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}

