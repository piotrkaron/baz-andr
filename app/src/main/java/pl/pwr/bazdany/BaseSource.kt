package pl.pwr.bazdany

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseSource {
    protected suspend fun <T> executeRequest(request: suspend () -> Response<T>): Effect<T>
            = withContext(Dispatchers.IO) {
        try {
            val response = request()

            when {
                response.isSuccessful -> return@withContext Effect.Success(response.body()!!)
                else -> return@withContext response.toResultError()
            }
        } catch (e: Exception) {
            return@withContext Effect.Error(e.message?: "error")
        }
    }
}

fun Response<*>.toResultError(): Effect.Error {
    return try {
        val moshi = RetroProvider.moshi
        val adapter = moshi.adapter(Error::class.java)

        val err: Error? = adapter.fromJson(errorBody().toString())
        Effect.Error(err?.error)
    } catch (e: Exception) {
        Effect.Error("Błąd API")
    }
}

data class Error(
    val error: String?
)

