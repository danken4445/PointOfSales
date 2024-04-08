package com.example.pointofsales.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.pointofsales.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateAccountFormFragment : Fragment() {

    private lateinit var empIdEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var createAccountButton: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_account_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        empIdEditText = view.findViewById(R.id.empIdEditTextSettings)
        nameEditText = view.findViewById(R.id.nameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditTextSettings)
        roleSpinner = view.findViewById(R.id.roleSpinner)
        createAccountButton = view.findViewById(R.id.createAccountButton)

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PosUsers")

        // Set up role spinner
        val roles = arrayOf("Manager", "Cashier") // Add other roles if needed
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        // Set click listener for create account button
        createAccountButton.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val empId = empIdEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val role = roleSpinner.selectedItem.toString()

        // Validate input
        if (empId.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the employee ID already exists
        databaseReference.child(empId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(requireContext(), "Employee ID already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Save employee details to the database
                    val user = HashMap<String, Any>()
                    user["emp_id"] = empId
                    user["name"] = name
                    user["password"] = password
                    user["role"] = role

                    databaseReference.child(empId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                            // Clear input fields after successful creation
                            empIdEditText.text.clear()
                            nameEditText.text.clear()
                            passwordEditText.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to create account", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
