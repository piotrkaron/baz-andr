package pl.pwr.bazdany.ui.trainings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.create_trening_fragment.*
import pl.pwr.bazdany.MainActivity
import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CreateTreningFragment : Fragment() {

    private lateinit var viewModel: CreateTreningViewModel

    private val types = TrainingTypes.values().map { it.value }

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_trening_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        time_tv.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        date_tv.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
    }

    private fun showLoading(it: Boolean) {
        if(it) loading.visibility = View.VISIBLE
        else loading.visibility = View.GONE
    }

    private fun onState(it: CreationState?) {
        it?.let {

            if(it.dateError != null) date_tv.error = it.dateError
            else date_tv.error = null

            if(it.timeError != null) time_tv.error = it.timeError
            else time_tv.error = null

        }
    }

    private fun onError(it: String) {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    private fun onTrainingCreated(it: String) {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

        navController.popBackStack()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getViewModel{ CreateTreningViewModel((activity as MainActivity).trainingRepo)}

        numpicker_minutes.maxValue = 60
        numpicker_seconds.maxValue = 60
        numpicker_hours.maxValue = 100

        viewModel.ok.observe(viewLifecycleOwner, Observer { it?.let { onTrainingCreated(it) } })
        viewModel.error.observe(viewLifecycleOwner, Observer { it?.let { onError(it) } })
        viewModel.state.observe(viewLifecycleOwner, Observer { onState(it) })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { showLoading(it) })

        val adapt = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )
        type_spinner.adapter = adapt

        date_tv.setOnClickListener {
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

                    date_tv.setText(format)
                },
                year,
                month,
                day
            )
            picker.show()
        }

        time_tv.setOnClickListener {
            TimePickerDialog(
                this.requireContext(),
                TimePickerDialog.OnTimeSetListener { _, hour, minutes ->
                    val time = LocalTime.of(hour, minutes, 0)
                    val format = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                    time_tv.setText(format)
                },
                12,
                0,
                true
            ).show()
        }


        create_button.setOnClickListener {
            val duration = numpicker_hours.value * 3600 + numpicker_minutes.value * 60 + numpicker_seconds.value
            val date = date_tv.text.toString().trim()
            val time = time_tv.text.toString().trim()
            val type = type_spinner.selectedItem as String

            viewModel.createTraining(duration, date, time, type)
        }

    }


}
