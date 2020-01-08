package pl.pwr.bazdany.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_profile.*
import pl.pwr.bazdany.MainActivity
import pl.pwr.bazdany.R
import pl.pwr.bazdany.getViewModel

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileViewModel = getViewModel{ ProfileViewModel((activity as MainActivity).loginRepo)}
        logout_button.setOnClickListener {
            profileViewModel.logout()
            (activity as MainActivity).logout()
        }
    }
}