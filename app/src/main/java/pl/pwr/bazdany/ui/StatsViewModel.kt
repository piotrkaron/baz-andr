package pl.pwr.bazdany.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.Effect
import pl.pwr.bazdany.ui.trainings.TrainingRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(
    private val repo: TrainingRepository
) : ViewModel() {

    val _state = MutableLiveData<StatsState>(StatsState())
    val stats = MutableLiveData<List<StatDomain>>(listOf())
    val error = MutableLiveData<String?>(null)

    fun refresh(start: String, end: String) {
        if (!isValid(start, end)) return

        viewModelScope.launch {
            val stat = repo.getStats(start, end)

            when(stat){
                is Effect.Success -> stats.value = stat.data
                is Effect.Error -> error.value = "Błąd pobierania statystyk"
            }
        }


    }

    private fun isValid(start: String, end: String): Boolean {
        val state = StatsState()

        if(start.isBlank() || end.isBlank()){
            if(start.isBlank()) state.startError = "Data nie może być pusta"
            if(end.isBlank()) state.endError = "Data nie może być pusta"
            _state.postValue(state)
            return false
        }

        val format = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var endDate: LocalDate? = null
        var startDate: LocalDate? = null

        try {
            startDate = LocalDate.from(format.parse(start))
        }catch (e: Throwable){
            state.startError = "Błędny format daty"
        }

        try {
            endDate = LocalDate.from(format.parse(end))
        }catch (e: Throwable){
            state.endError = "Błędny format daty"
        }

        if(state.hasErrors()){
            _state.postValue(state)
            return false
        }

        if(startDate != null && endDate != null)
            if(endDate.isBefore(startDate)){
                state.endError = "Błędna data"
                _state.postValue(state)
                return false
            }

        _state.postValue(state)
        return true
    }
}

data class StatsState(
    var startError: String? = null,
    var endError: String? = null
){
    fun hasErrors() = startError != null || endError != null
}
