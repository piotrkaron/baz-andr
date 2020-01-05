package pl.pwr.bazdany.ui.register.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

}

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val birth_date: String, // Format dd-MM-yyyy
    val weight: Int?,
    val height: Int?
)

data class RegisterResponse(
    val registered: Boolean
)