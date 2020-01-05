package pl.pwr.bazdany.ui.login.data.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.LocalDate
import java.time.LocalDateTime

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
    val token: String,
    //@JsonFormat(pattern = "dd-MM-yyyy-HH:mm:ss")
    val expireAt: String,
    val userDto: UserDto
)

data class UserDto(
    val name: String,
    val surname: String,
    val email: String,
    //@JsonFormat(pattern = "dd-MM-yyyy")
    val birth_day: String,
    val weight: Int?,
    val height: Int?
)