package pl.pwr.bazdany.ui.trainings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.bazdany.BaseSource
import pl.pwr.bazdany.Effect
import retrofit2.Response
import retrofit2.http.GET
import java.time.Duration
import java.time.format.DateTimeFormatter

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

interface TrainingApi {

    @GET("/api/trainings")
    suspend fun getUserTrainings(): Response<List<TrainingDto>>

}

class TrainingRepository(private val api: TrainingApi) : BaseSource() {

    suspend fun getTrainings(): Effect<List<TrainingDomain>> {
        return when (val list = executeRequest { api.getUserTrainings() }) {
            is Effect.Success -> Effect.Success(list.data.map { it.toDomain() })
            is Effect.Error -> Effect.Error("Błąd pobierania treningów.")
        }
    }

}

data class TypeDto(
    val name: String,
    val calories: Double
)

data class TrainingDto(
    val id: Long,
    val duration: Int,
    val date: String,
    val type: TypeDto,
    val owner: OwnerDto
)

data class OwnerDto(
    val name: String,
    val surname: String
)

data class TrainingDomain(
    val id: Long,
    val duration: String,
    val date: String,
    val typeName: String,
    val burntCalories: Double,
    val ownerName: String,
    val ownerSurname: String
)

fun TrainingDto.toDomain(): TrainingDomain{
    val format =  String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60))

    return TrainingDomain(
        id,format, date, type.name, type.calories * duration, owner.name, owner.surname
    )
}