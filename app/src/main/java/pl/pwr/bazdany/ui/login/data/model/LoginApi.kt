package pl.pwr.bazdany.ui.login.data.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("/applogin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

}

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: Long,
    val token: String
)