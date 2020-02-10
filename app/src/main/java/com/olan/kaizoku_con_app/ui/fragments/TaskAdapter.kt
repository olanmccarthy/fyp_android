package com.olan.kaizoku_con_app.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olan.kaizoku_con_app.R
import com.olan.kaizoku_con_app.models.Task

class TaskAdapter(val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //perhaps this could be surrounded in an if statement so that different rows get inflated
        //depending on what type of task is in the list?
            //not 100% sure as it doesn't loop through the list, may have to just make a robust row_task
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.row_task, parent, false)
        return ViewHolder(layoutView)
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //TODO("not implemented") after view has been implemented this will say what values one must get from the task class to put in
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}

