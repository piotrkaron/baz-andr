package pl.pwr.bazdany.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.register_fragment.*
import pl.pwr.bazdany.MainActivity

import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel
import pl.pwr.bazdany.ui.login.ui.LoggedInUserView
import pl.pwr.bazdany.ui.login.ui.afterTextChanged

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel = getViewModel {
        RegisterViewModel(
            (activity as MainActivity).registerRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.registerFormState.observe(this@RegisterFragment, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = registerState.isDataValid

            if (registerState.usernameError != null) {
                username.error = getString(registerState.usernameError)
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

        username.afterTextChanged {
            viewModel.registerDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                viewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }




        }

        register.setOnClickListener {
            loading.visibility = View.VISIBLE
            viewModel.register(
                username.text.toString(),
                password.text.toString(),
                name.text.toString(),
                surname.text.toString(),
                date.text.toString(),
                weight.text.toStrin(),
                height.text.toString())
        }
    }

    private fun registerSuccessful() {
        // TODO : initiate successful logged in experience
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

}
