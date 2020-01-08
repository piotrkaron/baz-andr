package pl.pwr.bazdany.ui.trainings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_trainings.*
import pl.pwr.bazdany.MainActivity
import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel

class TrainingsFragment : Fragment() {

    private lateinit var vm: TrainingsViewModel
    private val trainingsAdapter = TrainingsAdapter()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trainings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trainingsAdapter
        }
        navController = Navigation.findNavController(view)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm = getViewModel { TrainingsViewModel((activity as MainActivity).trainingRepo) }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.navigation_trainings){
                vm.refresh()
            }
        }

        vm.isLoading.observe(viewLifecycleOwner, Observer { showLoading(it) })
        vm.info.observe(viewLifecycleOwner, Observer { it?.let { showInfo(it) } })
        vm.trainings.observe(viewLifecycleOwner, Observer { updateAdpter(it) })

        fab.setOnClickListener {
            navController.navigate(R.id.action_navigation_trainings_to_createTreningFragment)
        }

        (activity as MainActivity).showBar()

    }

    private fun showLoading(it: Boolean) {
        if (it) loading.visibility = View.VISIBLE
        else loading.visibility = View.GONE
    }

    private fun showInfo(it: String) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
    }

    private fun updateAdpter(it: List<TrainingDomain>) {
        trainingsAdapter.updateList(it)
        trainingsAdapter.notifyDataSetChanged()
    }

}

class TrainingsAdapter : RecyclerView.Adapter<TrainingsAdapter.ViewHolder>() {

    private val trainings: MutableList<TrainingDomain> = mutableListOf()

    fun updateList(it: List<TrainingDomain>) {
        trainings.clear()
        trainings.addAll(it)
        trainings.sortByDescending { it.date }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val name: TextView = v.findViewById(R.id.name)
        private val surname: TextView = v.findViewById(R.id.surname)
        private val date: TextView = v.findViewById(R.id.date)
        private val calories: TextView = v.findViewById(R.id.calories)
        private val time: TextView = v.findViewById(R.id.time)
        private val type: TextView = v.findViewById(R.id.type)

        fun bind(tr: TrainingDomain){
            name.text = tr.ownerName
            surname.text = tr.ownerSurname
            date.text = tr.date
            calories.text = tr.burntCalories.toString()
            time.text = tr.duration
            type.text = tr.typeName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_training, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trainings.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trainings[position])
    }
}