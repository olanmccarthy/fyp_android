package com.olan.finalyearproject.ui.fragments

import android.app.Activity
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.util.Log.d
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.olan.finalyearproject.R
import com.olan.finalyearproject.helpers.DeleteButtonOnClickListener
import com.olan.finalyearproject.models.*
import kotlinx.android.synthetic.main.row_task.view.*
import kotlin.math.abs

class TaskAdapter(private val taskClasses: List<TaskClass>, val activity: Activity) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    val carDrawable = R.drawable.ic_car_journey
    val bikeDrawable = R.drawable.ic_bike_journey
    val busDrawable = R.drawable.ic_bus_journey
    val walkDrawable = R.drawable.ic_walk_journey

    lateinit var mParent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //perhaps this could be surrounded in an if statement so that different rows get inflated
        //depending on what type of task is in the list?
            //not 100% sure as it doesn't loop through the list, may have to just make a robust row_task
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.row_task, parent, false)
        mParent = parent

        return ViewHolder(layoutView)
    }

    override fun getItemCount() = taskClasses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskClasses[position]
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

        val mDetector = GestureDetectorCompat(activity, gestureDetector(holder.view))

        holder.view.setOnTouchListener{ v, event ->
            mDetector.onTouchEvent(event)
        }

        holder.view.deleteButton.setOnClickListener(DeleteButtonOnClickListener(activity, position))
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    inner class gestureDetector(val view: View): GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val transition = Fade().setDuration(300).addTarget(view.deleteButton)

            if(abs(e1!!.x) - abs(e2!!.x) < 0){
                TransitionManager.beginDelayedTransition(mParent, transition)
                view.deleteButton.visibility = View.INVISIBLE
            } else {
                TransitionManager.beginDelayedTransition(mParent, transition)
                view.deleteButton.visibility = View.VISIBLE
            }

            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
        }
    }

}

