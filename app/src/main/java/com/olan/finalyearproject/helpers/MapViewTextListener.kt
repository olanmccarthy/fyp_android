package com.olan.finalyearproject.helpers

import android.util.Log
import android.view.View
import android.widget.TextView
import com.olan.finalyearproject.R
import com.olan.finalyearproject.R.color.textBoxNotSelected

class MapViewTextListener(var originText: TextView, var destinationText: TextView, var originTextSelected: Boolean): View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.destinationTextView -> {
                Log.d("olanDebug", "destination clicked")
                originTextSelected = false
                //originText.setBackgroundColor(textBoxNotSelected)
                //destinationText.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
            }
            R.id.originTextView -> {
                Log.d("olanDebug", "origin clicked")
                originTextSelected = true
                //originText.setBackgroundColor(resources.getColor(R.color.textBoxSelected))
                //destinationText.setBackgroundColor(resources.getColor(textBoxNotSelected))
            }
        }
    }

}