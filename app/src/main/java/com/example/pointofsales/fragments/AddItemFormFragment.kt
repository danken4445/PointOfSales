package com.example.pointofsales.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pointofsales.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddItemFormFragment : Fragment() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var selectImageButton: Button
    private var selectedImageUri: Uri? = null
    private var itemIdCounter: Int = 0

    private val REQUEST_CODE_GALLERY = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_item_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("Items")

        // Initialize views
        itemNameEditText = view.findViewById(R.id.itemNameEditText)
        itemPriceEditText = view.findViewById(R.id.itemPriceEditText)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        selectImageButton = view.findViewById(R.id.selectImageButton)
        val addButton = view.findViewById<Button>(R.id.addButton)



        // Set click listener for image selection button
        selectImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        // Retrieve last used item ID from the database
        databaseRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapshot in dataSnapshot.children) {
                    // Update itemIdCounter to one greater than the last used item ID
                    itemIdCounter = itemSnapshot.key?.toIntOrNull()?.plus(1) ?: 0
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        // Set up button click listener
        addButton.setOnClickListener {
            addItemToDatabase(selectedImageUri)
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }
    companion object {
        private const val REQUEST_CODE_GALLERY = 123
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            // Handle the selected image URI here
            selectedImageUri = data.data
            // You can set the selected image URI to a ImageView or process it as needed
        }
    }
    private fun addItemToDatabase(imageUri: Uri?) {
        // Check if an image is selected
        if (imageUri != null) {
            val itemName = itemNameEditText.text.toString().trim()
            val itemPrice = itemPriceEditText.text.toString().trim()
            val quantity = quantityEditText.text.toString().toIntOrNull() ?: 0


            // Proceed if required fields are not empty
            if (itemName.isNotEmpty() && quantity > 0) {
                // Generate unique key for the new item
                val newItemId = itemIdCounter.toString()

                // Reference to Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference
                // Create reference to store the image with a unique filename
                val imageRef = storageRef.child("POSimages/$newItemId.jpg")

                // Upload image to Firebase Storage
                val uploadTask = imageRef.putFile(imageUri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Get the URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // Create map with item details including the image URL
                        val itemMap = mapOf(
                            "itemName" to itemName,
                            "itemQuantity" to quantity,
                            "imageResource" to imageUrl.toString(), // Store image URL
                            "itemPrice" to itemPrice
                            // Add other item details here if needed
                        )

                        // Add item to the database under the generated ID`
                        databaseRef.child(newItemId).setValue(itemMap)
                            .addOnSuccessListener {
                                // Increment the item ID counter
                                itemIdCounter++
                                // Show confirmation dialog
                                showConfirmationDialog()
                            }
                            .addOnFailureListener {
                                // Error occurred while adding item to database
                                // Handle error, display message, etc.
                                Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                uploadTask.addOnFailureListener {
                    // Handle image upload failure
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show error message if any field is empty or invalid
                Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show error message if no image is selected
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Item Added, Do you want to add another item?")
            .setPositiveButton("Yes") { _, _ ->
                // Clear input fields for new input
                itemNameEditText.text.clear()
                quantityEditText.text.clear()
                itemPriceEditText.text.clear()
            }
            .setNegativeButton("No") { _, _ ->
                // Navigate back to the inventory fragment
                navigateToFragment(InventoryFragment())
            }
            .setCancelable(false)
            .show()
    }
    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }



}
