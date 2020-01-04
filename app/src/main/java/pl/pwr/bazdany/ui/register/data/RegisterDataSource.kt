package pl.pwr.bazdany.ui.register.data

import pl.pwr.bazdany.BaseSource
import pl.pwr.bazdany.Effect

class RegisterDataSource(val api: RegisterApi): BaseSource() {

    suspend fun register(request: RegisterRequest): Effect<Boolean> {
        return when (val register = executeRequest { api.register(request) }){
            is Effect.Success -> Effect.Success(register.data.registered)
            is Effect.Error -> register
        }
    }


}

