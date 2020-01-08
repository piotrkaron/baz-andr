package pl.pwr.bazdany.ui.groups

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.group_create_fragment.*
import pl.pwr.bazdany.MainActivity

import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel

class GroupCreateFragment: DialogFragment() {

    private lateinit var vm: GroupCreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.group_create_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = getViewModel { GroupCreateViewModel((activity as MainActivity).groupRepo) }

        with(vm){
            error.observe(viewLifecycleOwner, Observer { onError(it) })
            ok.observe(viewLifecycleOwner, Observer { onInfo(it) })
            state.observe(viewLifecycleOwner, Observer { onState(it) })
        }

        create_button.setOnClickListener { vm.createGroup(name_field.text.toString(), city_field.text.toString()) }
        cancel_button.setOnClickListener { this.dismiss() }
    }

    private fun onError(it: String?) {
        it?.let {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onInfo(it: String?) {
        it?.let {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            this.dismiss()
        }
    }

    private fun onState(state: GroupState){
        city_field.error = state.cityError
        name_field.error = state.nameError
    }
}
