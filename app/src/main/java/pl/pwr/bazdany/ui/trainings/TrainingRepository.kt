package pl.pwr.bazdany.ui.trainings

import pl.pwr.bazdany.BaseSource
import pl.pwr.bazdany.Effect
import pl.pwr.bazdany.ui.StatDomain
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TrainingRepository(private val api: TrainingApi) : BaseSource() {

    suspend fun getTrainings(): Effect<List<TrainingDomain>> {
        return when (val list = executeRequest { api.getUserTrainings() }) {
            is Effect.Success -> Effect.Success(
                list.data.map { it.toDomain() })
            is Effect.Error -> Effect.Error(
                "Błąd pobierania treningów."
            )
        }
    }

    suspend fun createTrening(duration: Int, type: TrainingTypes, date: String): Effect<UploadResponse> {

        val formatted = date.trim().replace(":", "-")

        val dto = NewTrainingDto(duration, formatted , type.value, UUID.randomUUID().toString())

        return when (val response = executeRequest { api.createTraining(dto) }) {
            is Effect.Success -> Effect.Success(response.data)
            is Effect.Error -> Effect.Error(
                "Błąd tworzenia treningu."
            )
        }

    }

    suspend fun getStats(start: String, end: String): Effect<List<StatDomain>> {

        return when (val response = executeRequest { api.getStats(DateRangeDto(start, end)) }) {
            is Effect.Success -> Effect.Success(response.data.map{it.toDomain()})
            is Effect.Error -> Effect.Error(
                "Błąd tworzenia treningu."
            )
        }

    }

}

data class NewTrainingDto(
    val duration: Int,
   // @DateTimeFormat(pattern = "dd-MM-yyy-HH-mm-ss")
  //  @JsonFormat(pattern = "dd-MM-yyyy-HH-mm-ss")
    val date: String,
    val type: String,
    val rawData: String
)

data class UploadResponse(
    val trainingId: Long
)

enum class TrainingTypes(val value: String){

    BIKE("Jazda na rowerze"),
    RUNNING("Bieganie"),
    SKIING("Narciarstwo"),
    SWIMMING("Pływanie"),
    OTHER("Inne");

    companion object{

        fun find(string: String): TrainingTypes = values().find { it.value == string } ?: OTHER

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

data class DateRangeDto(
    val start: String,
    val end: String
)

data class StatsDto(
    val name: String,
    val durationSum: String,
    val burntCaloriesSum: Double,
    val trainingCount: Int
)

fun StatsDto.toDomain() = StatDomain(
    this.name, this.durationSum, this.burntCaloriesSum, this.trainingCount
)