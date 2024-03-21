package com.example.pointofsales

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LoginPageActivity : AppCompatActivity() {

    private lateinit var empIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

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

                    if (empId == empIdFromDB && password == passwordFromDB) {
                        loggedIn = true
                        // Redirect user based on role
                        if (role == "Manager") {
                            startActivity(Intent(this@LoginPageActivity, ManagerActivity::class.java))
                            finish()
                        } else if (role == "Cashier") {
                            startActivity(Intent(this@LoginPageActivity, CashierActivity::class.java))
                            finish()
                        }
                        break
                    }
                }
                if (!loggedIn) {
                    Toast.makeText(this@LoginPageActivity, "Invalid credentials", Toast.LENGTH_SHORT)
                        .show()
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
}