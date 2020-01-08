package pl.pwr.bazdany.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.stats_fragment.*
import pl.pwr.bazdany.MainActivity

import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class StatsFragment : Fragment() {

    private lateinit var viewModel: StatsViewModel
    private lateinit var adapt: StatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stats_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel { StatsViewModel((activity as MainActivity).trainingRepo)}
        adapt = StatAdapter()

        stats_recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapt
        }

        date_start_edit.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            // date picker dialog
            // date picker dialog
            val picker = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val locDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    val format = locDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                    date_start_edit.setText(format)
                },
                year,
                month,
                day
            )
            picker.show()
        }

        date_end_edit.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            // date picker dialog
            // date picker dialog
            val picker = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val locDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    val format = locDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                    date_end_edit.setText(format)
                },
                year,
                month,
                day
            )
            picker.show()
        }

        val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        date_start_edit.setText(LocalDate.now().minusDays(30).format(format))
        date_end_edit.setText(LocalDate.now().format(format))

        button_get_stats.setOnClickListener { viewModel.refresh(date_start_edit.text.toString(), date_end_edit.text.toString()) }

        with(viewModel){
            error.observe(viewLifecycleOwner, androidx.lifecycle.Observer { onError(it) })
            stats.observe(viewLifecycleOwner, androidx.lifecycle.Observer { onStats(it) })
            _state.observe(viewLifecycleOwner, androidx.lifecycle.Observer { onState(it) })
        }
    }

    private fun onState(state: StatsState){
        date_end_edit.error = state.endError
        date_start_edit.error = state.startError
    }

    private fun onError(it: String?){
        it?.let {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onStats(stats: List<StatDomain>){
        adapt.update(stats)
    }
}


data class StatDomain(
    val discipline: String,
    val totalDuration: String,
    val burntCalories: Double,
    val numOfTrainigs: Int
)

class StatAdapter: RecyclerView.Adapter<StatAdapter.ViewHolder>(){

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){

        private val totalCalories = v.findViewById<TextView>(R.id.calories)
        private val durationTotal = v.findViewById<TextView>(R.id.time)
        private val discipline = v.findViewById<TextView>(R.id.discipline)
        private val trCount = v.findViewById<TextView>(R.id.training_count)

        fun bind(obj: StatDomain){
            totalCalories.text = obj.burntCalories.toString()
            durationTotal.text = obj.totalDuration
            discipline.text = obj.discipline
            trCount.text = obj.numOfTrainigs.toString()
        }
    }

    private val stats: MutableList<StatDomain> = mutableListOf()

    fun update(stat: List<StatDomain>){
        stats.clear()
        stats.addAll(stat)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.car_stats, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stats[position])
    }
}