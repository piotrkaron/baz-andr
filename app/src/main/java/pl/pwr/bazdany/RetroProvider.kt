package pl.pwr.bazdany

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import pl.pwr.bazdany.ui.groups.GroupApi
import pl.pwr.bazdany.ui.login.data.model.LoginApi
import pl.pwr.bazdany.ui.register.data.RegisterApi
import pl.pwr.bazdany.ui.trainings.TrainingApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetroProvider {

    private const val BASE_URL = "http://10.0.2.2:8080"

    val moshi = Moshi.Builder().build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = (HttpLoggingInterceptor.Level.BODY)
    }

    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("parameter", "value")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Cookie", "XDEBUG_SESSION=ANDROID; path=/;")
                .addHeader("Cookie", "XDEBUG_SESSION=ANDROID; path=/;")

            chain.proceed(request.build())
        }
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val trainingApi: TrainingApi = retrofit.create(TrainingApi::class.java)
    val loginApi: LoginApi = retrofit.create(LoginApi::class.java)
    val registerApi: RegisterApi = retrofit.create(RegisterApi::class.java)
    val groupApi: GroupApi = retrofit.create(GroupApi::class.java)
}

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${Session.token ?: "invalid"}")
            .build()

        return chain.proceed(request)
    }

}
