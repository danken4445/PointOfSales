package com.example.pointofsales.fragments.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.pointofsales.R
import com.example.pointofsales.activities.LoginPageActivity
import com.example.pointofsales.fragments.AddItemFormFragment

class SettingsFragment : Fragment() {

    private lateinit var createAccountCard: CardView
    private lateinit var logoutSettingsCard: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize CardViews
        createAccountCard = view.findViewById(R.id.createAccount)
        logoutSettingsCard = view.findViewById(R.id.logoutSettingsButton)


        // Set click listeners
        createAccountCard.setOnClickListener {
            replaceWithCreateAccountFormFragment()
            createEmployeeAccount()
        }


        logoutSettingsCard.setOnClickListener {
            // Navigate back to the login page
            val intent = Intent(requireContext(), LoginPageActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Finish the current activity (SettingsActivity)
        }
    }

    private fun createEmployeeAccount() {
        // Implement create account functionality here
        // For example, navigate to create account screen
    }

    private fun replaceWithCreateAccountFormFragment() {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(
            R.id.fragmentContainer,
            CreateAccountFormFragment()
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
