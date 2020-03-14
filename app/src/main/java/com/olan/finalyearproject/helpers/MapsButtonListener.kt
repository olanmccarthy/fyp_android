package com.olan.finalyearproject.helpers

import android.view.View
import androidx.navigation.NavController
import com.olan.finalyearproject.R

class MapsButtonListener(val navController: NavController): View.OnClickListener {

    override fun onClick(v: View?) {
        navController.navigate(R.id.action_addActivityFragment_to_mapFragment)
    }
}