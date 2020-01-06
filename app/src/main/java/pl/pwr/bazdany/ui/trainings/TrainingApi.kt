package pl.pwr.bazdany.ui.trainings

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TrainingApi {

    @GET("/api/trainings")
    suspend fun getUserTrainings(): Response<List<TrainingDto>>

    @POST("/api/training")
    suspend fun createTraining(@Body dto: NewTrainingDto): Response<UploadResponse>

}