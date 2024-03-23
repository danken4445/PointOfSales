package com.example.pointofsales.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.adapter.CartAdapter
import com.example.pointofsales.viewmodel.CartViewModel
import com.example.pointofsales.viewmodel.InventoryViewModel

class CartFragment : Fragment() {

    private val cartViewModel: CartViewModel by activityViewModels()
    private val activityViewModel: InventoryViewModel by activityViewModels()
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var layoutCartEmpty: ConstraintLayout
    private lateinit var buttonCheckout: AppCompatButton
    private lateinit var tvTotalPrice: TextView
    private lateinit var inventoryViewModel: InventoryViewModel // Declare inventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        cartRecyclerView = view.findViewById(R.id.rvCart)
        layoutCartEmpty = view.findViewById(R.id.layout_cart_empty)
        buttonCheckout = view.findViewById(R.id.buttonCheckout)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)

        // Initialize RecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        cartAdapter = CartAdapter()
        cartRecyclerView.adapter = cartAdapter

        // Set visibility of cart empty layout
        checkCartEmpty()

        // Calculate and display total price
        updateTotalPrice()

        // Set onClickListener for checkout button
        buttonCheckout.setOnClickListener {
            showCheckoutConfirmationDialog()
        }

        // Initialize inventoryViewModel
        inventoryViewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // Observe cart items LiveData and update the adapter accordingly
        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.setItems(cartItems)
            checkCartEmpty() // Check cart empty after updating items
            updateTotalPrice() // Update total price after cart items change
        }
    }

    private fun checkCartEmpty() {
        if (cartAdapter.itemCount == 0) {
            cartRecyclerView.visibility = View.GONE
            layoutCartEmpty.visibility = View.VISIBLE
        } else {
            cartRecyclerView.visibility = View.VISIBLE
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = cartAdapter.calculateTotalPrice()
        val formattedTotalPrice = String.format("Total: %.2f", totalPrice) // Format to display with two decimal places
        tvTotalPrice.text = formattedTotalPrice
    }

    private fun showCheckoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Do you want to print a Receipt?")
            .setPositiveButton("Yes") { dialog, _ ->
                sendOrderToFirebase(inventoryViewModel) // Pass inventoryViewModel to sendOrderToFirebase
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendOrderToFirebase(inventoryViewModel: InventoryViewModel) {
        cartViewModel.sendOrderToFirebase(requireContext(), inventoryViewModel)
    }

}
