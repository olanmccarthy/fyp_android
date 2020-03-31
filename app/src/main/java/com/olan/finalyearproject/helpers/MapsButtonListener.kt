package com.olan.finalyearproject.helpers

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import com.olan.finalyearproject.R

class MapsButtonListener(var formLayout: ConstraintLayout, var mapLayout: ConstraintLayout): View.OnClickListener {

    override fun onClick(v: View?) {
        mapLayout.visibility = View.VISIBLE
        formLayout.visibility = View.GONE
    }
}