package com.example.pointofsales.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.pointofsales.R
import com.example.pointofsales.fragments.CartFragment
import com.example.pointofsales.fragments.CashierSettingsFragment
import com.example.pointofsales.fragments.FullHistoryFragment
import com.example.pointofsales.fragments.RecentHistoryFragment
import com.example.pointofsales.fragments.SalesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class CashierActivity : AppCompatActivity() {
    private lateinit var logoutButton: ImageButton // Assuming you have a Button for logout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier)
        hideSystemUI()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val cartFragment = CartFragment()
        fragmentTransaction.replace(R.id.fragmentContainer2, cartFragment)
        fragmentTransaction.commit()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationViewCashier)
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Animate the selected item
            animateSpin(bottomNavigationView.findViewById(item.itemId))

            when (item.itemId) {

                R.id.action_sale -> {
                    // Navigate to the com.example.postest.com.example.postest.adapters.com.example.postest.fragments.com.example.pointofsales.fragments.com.example.pointofsales.fragments.SalesFragment
                    navigateToFragment(SalesFragment())
                    true
                }
                R.id.action_logout -> {
                    // Navigate to the com.example.postest.com.example.postest.adapters.com.example.postest.fragments.com.example.pointofsales.fragments.com.example.pointofsales.fragments.SalesFragment
                    logoutUser()
                    true
                }
                R.id.action_recent_history -> {
                    // Navigate to the com.example.postest.com.example.postest.adapters.com.example.postest.fragments.com.example.pointofsales.fragments.com.example.pointofsales.fragments.SalesFragment
                    navigateToFragment(RecentHistoryFragment())
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

    private fun hideSystemUI() {
        // Enables regular immersive mode
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
        // If the Android version is newer than API 19, use the flag SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
    private fun logoutUser() {
        // Clear any saved user session or authentication state
        // For example, clear SharedPreferences or Firebase Authentication session

        // After clearing session, navigate back to login activity
        val intent = Intent(this, LoginPageActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent user from returning using back button
    }
}