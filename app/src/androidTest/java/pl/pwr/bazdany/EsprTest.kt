package pl.pwr.bazdany

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith
import pl.pwr.bazdany.ui.login.ui.LoginFragment


@RunWith(AndroidJUnit4ClassRunner::class)
class EsprTest {


    @Test
    fun xd(){
        val scen = launchFragmentInContainer<LoginFragment> {  }
    }
}

