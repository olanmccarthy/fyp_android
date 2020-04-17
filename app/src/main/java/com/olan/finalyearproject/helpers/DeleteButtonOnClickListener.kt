package com.olan.finalyearproject.helpers

import android.app.Activity
import android.util.Log.d
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.olan.finalyearproject.UserClient


class DeleteButtonOnClickListener(activity: Activity, val pos: Int): View.OnClickListener {
    var user = ((activity.applicationContext) as UserClient).user!!
    val db = FirebaseFirestore.getInstance()

    override fun onClick(v: View?) {
        d("olanDebug", "delete button clicked")
        //get the object at position 'pos' and delete it from current plan array and firebase
        val task = user.curentPlan[pos]
        user.curentPlan.removeAt(pos)
        db.collection("users").document(user.userId).collection("currentPlan").document(task.taskId).delete()
        v!!.visibility = View.GONE
    }
}