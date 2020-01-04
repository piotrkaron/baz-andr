package pl.pwr.bazdany.ui.register.data

class RegisterRepository(private val registerDataSource: RegisterDataSource) {

    suspend fun register(request: RegisterRequest) = registerDataSource.register(request)

}

