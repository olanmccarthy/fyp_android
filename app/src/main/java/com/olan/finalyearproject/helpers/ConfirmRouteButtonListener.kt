package com.olan.finalyearproject.helpers

import android.view.View
import androidx.navigation.NavController
import com.olan.finalyearproject.R

class ConfirmRouteButtonListener(var navController: NavController?): View.OnClickListener {
    override fun onClick(v: View?) {
        if(navController != null){
            navController!!.navigate(R.id.action_mapFragment_to_addActivityFragment)

        }
    }

}