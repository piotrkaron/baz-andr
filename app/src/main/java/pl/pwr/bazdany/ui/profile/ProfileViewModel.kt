package pl.pwr.bazdany.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.ui.login.data.LoginRepository

class ProfileViewModel(
    val repo: LoginRepository
) : ViewModel() {

    fun logout(){
        viewModelScope.launch {
            repo.logout()
        }
    }
}