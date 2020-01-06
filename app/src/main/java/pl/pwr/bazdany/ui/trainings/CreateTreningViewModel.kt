package pl.pwr.bazdany.ui.trainings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.Effect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateTreningViewModel(private val repo: TrainingRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _state = MutableLiveData<CreationState?>(null)
    val state: LiveData<CreationState?> get() = _state

    private val _ok = MutableLiveData<String?>(null)
    val ok: LiveData<String?> get() = _ok

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error
    
    
    fun createTraining(duration: Int, date: String, time: String, type: String){

        if(!isDataValid(duration, date, time, type)){
            return
        }

        val types2 = TrainingTypes.find(type)
        val dateFormat = "$date-$time"

        _isLoading.postValue(true)
        viewModelScope.launch {

            when(repo.createTrening(duration,types2,dateFormat)){
                is Effect.Success -> _ok.postValue("Utworzono trening")
                is Effect.Error -> _error.postValue("Błąd tworzenia treningu")
            }
            _isLoading.postValue(false)
        }
    }

    private fun isDataValid(duration: Int, date: String, time: String, type: String): Boolean {
        val state = CreationState()

        _state.postValue(state)

        if(duration < 0) state.durationError = "Czas trwania nie może być ujemny"
        if(!isDateOk(date)) state.dateError = "Podana data jest błędna"
        if(time.isBlank()) state.timeError = "Podana godzina jest błędna"
        with(state){
            return (dateError == null && timeError ==null /*&& typeError == null*/ && durationError == null)
        }
    }

    private fun isDateOk(date: String): Boolean{
        if(date.isBlank()) return false

        val ac = DateTimeFormatter.ofPattern("dd-MM-yyyy").parse(date)
        val given = LocalDate.from(ac)
        return LocalDate.now().plusDays(1).isAfter(given)
    }
}

data class CreationState(
    var dateError: String? = null,
    var timeError: String? = null,

    var durationError: String? = null
)
