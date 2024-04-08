package com.example.pointofsales.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointofsales.R
import com.google.firebase.database.*

class LoginPageActivity : AppCompatActivity() {

    private lateinit var empIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        hideSystemUI()

        empIdEditText = findViewById(R.id.empIdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        databaseReference = FirebaseDatabase.getInstance().getReference("PosUsers")

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val empId = empIdEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var loggedIn = false
                for (userSnapshot in dataSnapshot.children) {
                    val empIdFromDB =
                        userSnapshot.child("emp_id").value?.toString() // Cast to String
                    val passwordFromDB =
                        userSnapshot.child("password").value?.toString() // Cast to String
                    val role = userSnapshot.child("role").value?.toString() // Cast to String

                    // Inside loginUser() function, after checking the user's credentials and before starting the activity
                    if (empId == empIdFromDB && password == passwordFromDB) {
                        loggedIn = true
                        val employeeName = userSnapshot.child("name").value?.toString() // Get employee's name

                        val intent = when (role) {
                            "Manager" -> Intent(this@LoginPageActivity, ManagerActivity::class.java)
                            "Cashier" -> Intent(this@LoginPageActivity, CashierActivity::class.java)
                            else -> null
                        }

                        // Pass employee's name as extra
                        intent?.putExtra("employeeName", employeeName)
                        startActivity(intent)
                        finish()
                    }


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@LoginPageActivity,
                    "Database error: " + databaseError.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

}