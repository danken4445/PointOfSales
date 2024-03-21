package com.example.pointofsales

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pointofsales.fragments.CartFragment
import com.example.pointofsales.fragments.FullHistoryFragment
import com.example.pointofsales.R
import com.example.pointofsales.fragments.SalesReportFragment
import com.example.pointofsales.fragments.InventoryFragment
import com.example.pointofsales.fragments.SalesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val cartFragment = CartFragment()
        fragmentTransaction.replace(R.id.fragmentContainer2, cartFragment)
        fragmentTransaction.commit()
        var settingsButton: ImageButton = findViewById(R.id.settingsButton)

        val button = findViewById<ImageButton>(R.id.settingsButton)
        settingsButton.setBackgroundColor(resources.getColor(android.R.color.transparent))

        button.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.spin_button)
            it.startAnimation(animation)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Animate the selected item
            animateSpin(bottomNavigationView.findViewById(item.itemId))

            when (item.itemId) {
                R.id.action_salesReport -> {
                    // Navigate to the SalesReportFragment
                    navigateToFragment(SalesReportFragment())
                    true
                }
                R.id.action_sale -> {
                    // Navigate to the com.example.postest.com.example.postest.adapters.com.example.postest.fragments.com.example.pointofsales.fragments.com.example.pointofsales.fragments.SalesFragment
                    navigateToFragment(SalesFragment())
                    true
                }
                R.id.action_history -> {
                    // Navigate to the FullHistoryFragment
                    navigateToFragment(FullHistoryFragment())
                    true
                }
                R.id.action_inventory -> {
                    // Navigate to the com.example.postest.com.example.postest.com.example.postest.fragments.com.example.pointofsales.fragments.com.example.pointofsales.fragments.InventoryFragment
                    navigateToFragment(InventoryFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun animateSpin(view: View) {
        val rotationAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        rotationAnimator.duration = 500 // Adjust the duration as needed

        rotationAnimator.start()
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}
