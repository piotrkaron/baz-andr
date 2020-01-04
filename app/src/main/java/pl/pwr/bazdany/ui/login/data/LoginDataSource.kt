package pl.pwr.bazdany.ui.login.data

import pl.pwr.bazdany.BaseSource
import pl.pwr.bazdany.Effect
import pl.pwr.bazdany.Effect.Success
import pl.pwr.bazdany.Effect.Error
import pl.pwr.bazdany.ui.login.data.model.LoggedInUser
import pl.pwr.bazdany.ui.login.data.model.LoginApi
import pl.pwr.bazdany.ui.login.data.model.LoginRequest

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(val api: LoginApi) : BaseSource() {

    suspend fun login(email: String, password: String): Effect<LoggedInUser> {

        return when(val resp = executeRequest { api.login(LoginRequest(email, password)) }){
            is Success -> {
                val data = resp.data

                Success(LoggedInUser(data.userId.toString(), "",data.token))
            }
            is Error -> {
                resp
            }
        }
    }


    fun logout() {
        // TODO: revoke authentication
    }
}

