package com.olan.kaizoku_con_app.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.olan.kaizoku_con_app.R
import com.olan.kaizoku_con_app.models.CarJourney
import com.olan.kaizoku_con_app.models.Task
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), View.OnClickListener {

    var navController: NavController? = null

    var tasks = listOf<Task>(
        CarJourney(
            1,
            arrayOf(1.1, 1.1),
            arrayOf(2.2, 2.2),
            "honda",
            "civic"
        ),
        CarJourney(
            2,
            arrayOf(4.44, 4.44),
            arrayOf(6.66, 6.66),
            "ford",
            "smax",
            2
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        recylerView.apply {
            //TODO find some sort of context that will work for the layout manager
            layoutManager = LinearLayoutManager(activity)
            //TODO implement adapter that will display the views for the tasks on the recyclerView
            adapter = TaskAdapter(tasks)
        }
        view.findViewById<FloatingActionButton>(R.id.addActivityButton).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        //case for what id is pressed
        when(v!!.id){
            //navigate to the add activity fragment
            R.id.addActivityButton -> navController!!.navigate(R.id.action_mainFragment_to_addActivityFragment)
        }
    }

}
