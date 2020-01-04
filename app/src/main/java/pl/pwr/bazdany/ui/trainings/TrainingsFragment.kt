package pl.pwr.bazdany.ui.trainings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pl.pwr.bazdany.R

class TrainingsFragment : Fragment() {

    private lateinit var trainingsViewModel: TrainingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trainingsViewModel =
                ViewModelProviders.of(this).get(TrainingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        trainingsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}