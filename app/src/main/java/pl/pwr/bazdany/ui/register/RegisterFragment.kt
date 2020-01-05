package pl.pwr.bazdany.ui.register

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.register_fragment.*
import pl.pwr.bazdany.MainActivity
import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel
import pl.pwr.bazdany.ui.login.ui.afterTextChanged
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class RegisterFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loading.visibility = View.GONE

        viewModel = getViewModel {
            RegisterViewModel(
                (activity as MainActivity).registerRepo
            )
        }

        viewModel.registerFormState.observe(this@RegisterFragment, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = registerState.isDataValid

            if (registerState.usernameError != null) {
                email.error = getString(registerState.usernameError)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
            if (registerState.dateError != null) {
                date.error = getString(registerState.dateError)
            }
            if (registerState.nameError != null) {
                name.error = getString(registerState.nameError)
            }
            if (registerState.surnameError != null) {
                surname.error = getString(registerState.surnameError)
            }
        })

        viewModel.registerResult.observe(this@RegisterFragment, Observer {
            val registerResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (registerResult.error != null) {
                showRegisterFailed(registerResult.error)
            }
            if (registerResult.success != null) {
                registerSuccessful()
            }
        })

        email.afterTextChanged {notifyVmTextChanged()}
        password.afterTextChanged {notifyVmTextChanged()}
        date.afterTextChanged {notifyVmTextChanged()}
        name.afterTextChanged {notifyVmTextChanged()}
        surname.afterTextChanged {notifyVmTextChanged()}
        height.afterTextChanged {notifyVmTextChanged()}
        weight.afterTextChanged {notifyVmTextChanged()}



        date.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr[Calendar.DAY_OF_MONTH]
            val month = cldr[Calendar.MONTH]
            val year = cldr[Calendar.YEAR]
            // date picker dialog
            // date picker dialog
            val picker = DatePickerDialog(
                this.requireContext(),
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val locDate = LocalDate.of(year, monthOfYear+1, dayOfMonth)
                    val format = locDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                    date.setText(format)
                },
                year,
                month,
                day
            )
            picker.show()
        }

        register.setOnClickListener {
            loading.visibility = View.VISIBLE
            viewModel.register(
                email.text.toString(),
                password.text.toString(),
                name.text.toString(),
                surname.text.toString(),
                date.text.toString(),
                weight.text.toString().toIntOrNull(),
                height.text.toString().toIntOrNull())
        }
    }

    private fun notifyVmTextChanged() {
        val calendar = Calendar.DATE

        viewModel.registerDataChanged(
            email.text.toString(),
            password.text.toString(),
            name.text.toString(),
            surname.text.toString(),
            date.text.toString(),
            weight.text.toString().toIntOrNull(),
            height.text.toString().toIntOrNull()
        )
    }


    private fun registerSuccessful() {
        navController.popBackStack()
        Toast.makeText(
            activity?.applicationContext,
            "Zarejestrowano",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showRegisterFailed(@StringRes errorString: Int) {
        Toast.makeText(activity?.applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}


