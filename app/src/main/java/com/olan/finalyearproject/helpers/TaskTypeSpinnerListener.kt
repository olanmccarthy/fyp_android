package com.olan.finalyearproject.helpers

import android.util.Log
import android.view.View
import android.widget.AdapterView

class TaskTypeSpinnerListener(
    var journeyTaskViews: Array<View>
): AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.getItemAtPosition(position)){
            "JourneyTask" -> {
                Log.d("olanDebug", "journey task selected")
                journeyTaskViews.forEach { item -> item.visibility = View.VISIBLE }
            }
        }
    }
}