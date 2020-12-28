package tech.androidplay.sonali.todo.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import tech.androidplay.sonali.todo.R
import tech.androidplay.sonali.todo.utils.Constants.IS_FIRST_TIME
import javax.inject.Inject

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 24/Sep/2020
 * Email: ankush@androidplay.in
 */

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkDestination()
    }

    private fun checkDestination() {
        if (firebaseAuth.currentUser == null || sharedPreferences.getBoolean(IS_FIRST_TIME, true))
            goToIntroFragment()
        else if (firebaseAuth.currentUser != null) goToTaskFragment()
        else goToAuthFragment()
    }

    private fun goToIntroFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_introFragment)
    }

    private fun goToTaskFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_taskFragment)
    }

    private fun goToAuthFragment() {
        findNavController().navigate(R.id.action_splashFragment_to_authFragment)
    }
}