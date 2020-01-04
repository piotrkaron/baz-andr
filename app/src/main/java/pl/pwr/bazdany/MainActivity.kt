package pl.pwr.bazdany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.pwr.bazdany.ui.login.data.LoginDataSource
import pl.pwr.bazdany.ui.login.data.LoginRepository
import pl.pwr.bazdany.ui.register.data.RegisterDataSource
import pl.pwr.bazdany.ui.register.data.RegisterRepository

class MainActivity : AppCompatActivity() {

    val registerRepo: RegisterRepository = RegisterRepository(RegisterDataSource(RetroProvider.registerApi))
    val loginRepo: LoginRepository = LoginRepository(LoginDataSource(RetroProvider.loginApi))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
