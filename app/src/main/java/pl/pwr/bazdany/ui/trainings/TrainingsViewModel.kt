package pl.pwr.bazdany.ui.trainings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.Effect

class TrainingsViewModel(
    private val repo: TrainingRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _info = MutableLiveData<String?>(null)
    val info: LiveData<String?>
        get() = _info

    private val _trainings = MutableLiveData<List<TrainingDomain>>(emptyList())
    val trainings: LiveData<List<TrainingDomain>>
        get() = _trainings

    init {
        refresh()
    }

    fun refresh(){
        _isLoading.postValue(false)

        viewModelScope.launch {
            val trainings = repo.getTrainings()

            when(trainings){
                is Effect.Success -> _trainings.postValue(trainings.data)
                is Effect.Error ->{
                    _info.postValue(trainings.message)
                    _info.postValue(null)
                }
            }

            _isLoading.postValue(false)
        }
    }
}
