package pl.pwr.bazdany.ui.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.Effect

class GroupCreateViewModel(
    private val repo: GroupRepository
)  : ViewModel() {

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    private val _ok = MutableLiveData<String?>(null)
    val ok: LiveData<String?> get() = _ok

    private val _state = MutableLiveData<GroupState>(GroupState())
    val state: LiveData<GroupState> get() = _state
    
    fun createGroup(name: String, city: String){
        if(!isValid(name, city)) return

        viewModelScope.launch {
            val res = repo.createGroup(name, city)

            when(res){
                is Effect.Success -> _ok.postValue(res.data)
                is Effect.Error -> _error.postValue(res.message)
            }
        }
    }

    private fun isValid(name: String, city: String): Boolean{
        val state = GroupState()
        if(name.isBlank()) state.nameError = "Nie może być puste"
        if(city.isBlank()) state.cityError = "Nie może być puste"

        _state.postValue(state)

        return (state.nameError == null && state.cityError == null)
    }
}

data class GroupState(
    var nameError: String? = null,
    var cityError: String? = null
)
