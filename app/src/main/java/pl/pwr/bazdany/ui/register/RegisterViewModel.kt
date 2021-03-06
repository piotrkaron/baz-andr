package pl.pwr.bazdany.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.Effect
import pl.pwr.bazdany.R
import pl.pwr.bazdany.ui.register.data.RegisterRepository
import pl.pwr.bazdany.ui.register.data.RegisterRequest
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*



class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(username: String, password: String, name: String, surname: String,  date: String, weight: Int?, height: Int?) {
        viewModelScope.launch{
            val request = RegisterRequest(username, password, name, surname, date, weight, height)
            val result = registerRepository.register(request)

            if (result is Effect.Success) {
                _registerResult.value =
                    RegisterResult(success = true)
            } else {
                _registerResult.value = RegisterResult(error = R.string.register_failed)
            }
        }
    }

    fun registerDataChanged(username: String, password: String, name: String, surname: String, calendar: String, height: Int?, weight: Int?) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!isNameValid(name)) {
            _registerForm.value = RegisterFormState(nameError = R.string.invalid_name)
        } else if (!isSurnameValid(surname)) {
            _registerForm.value = RegisterFormState(surnameError = R.string.invalid_surname)
        } else if (!isDateValid(calendar)) {
            _registerForm.value = RegisterFormState(dateError = R.string.invalid_date)
        } else if (height != null && !isHeightValid(height)) {
            _registerForm.value = RegisterFormState(heightError = R.string.invalid_height)
        } else if (weight != null && !isWeightValid(weight)) {
            _registerForm.value = RegisterFormState(weightError = R.string.invalid_weight)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isNameValid(name: String) = name.isNotBlank()
    private fun isSurnameValid(name: String) = name.isNotBlank()
    private fun isDateValid(date: String): Boolean {
        val format = SimpleDateFormat("dd-MM-yyyy")
        val date = format.parse(date)

        return date.before(Date.from(Instant.now()))
    }
    private fun isHeightValid(height: Int) = height < 245
    private fun isWeightValid(weight: Int) = weight < 200
}

data class RegisterFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val surnameError: Int? = null,
    val nameError: Int? = null,
    val dateError: Int? = null,
    val heightError: Int? = null,
    val weightError: Int? = null,
    val isDataValid: Boolean = false
)

data class RegisterResult(
    val success: Boolean? = null,
    val error: Int? = null
)
