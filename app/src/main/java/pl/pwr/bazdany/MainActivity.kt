package pl.pwr.bazdany

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.pwr.bazdany.ui.groups.GroupRepository
import pl.pwr.bazdany.ui.login.data.LoginDataSource
import pl.pwr.bazdany.ui.login.data.LoginRepository
import pl.pwr.bazdany.ui.login.data.model.LoginResponse
import pl.pwr.bazdany.ui.login.ui.LoginFragment
import pl.pwr.bazdany.ui.register.data.RegisterDataSource
import pl.pwr.bazdany.ui.register.data.RegisterRepository
import pl.pwr.bazdany.ui.trainings.TrainingRepository
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

const val USER_PREFS = "USER_PREFS"
const val USER_DATA = "USER_DATA"

class MainActivity : AppCompatActivity() {

    val groupRepo: GroupRepository = GroupRepository(RetroProvider.groupApi)
    val trainingRepo: TrainingRepository = TrainingRepository(RetroProvider.trainingApi)
    val registerRepo: RegisterRepository = RegisterRepository(RegisterDataSource(RetroProvider.registerApi))
    val loginRepo: LoginRepository = LoginRepository(LoginDataSource(RetroProvider.loginApi))

    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_trainings, R.id.navigation_groups, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.visibility = View.GONE
    }

    fun userIsLoggedIn(): Boolean {
        with(Session){
            if(token != null && (expiryDate != null && expiryDate!!.after(Date.from(Instant.now())))) return true
        }

        val user = readUserData() ?: return false

        val dtf = SimpleDateFormat("dd-MM-yyyy")
        val date = dtf.parse(user.expireAt)

        if(date.before(Date.from(Instant.now()))) return false

        Session.setup(user)
        navView.visibility = View.VISIBLE
        return true
    }

    fun saveUserData(data: LoginResponse){
        val adapter = RetroProvider.moshi.adapter(LoginResponse::class.java)
        val string = adapter.toJson(data)

        getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_DATA, string)
            .apply()
    }

    private fun readUserData(): LoginResponse? {
        val string = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            .getString(USER_DATA, "")

        if(string == null || string.isBlank()) return null

        val adapter = RetroProvider.moshi.adapter(LoginResponse::class.java)

        return adapter.fromJson(string)
    }

}
