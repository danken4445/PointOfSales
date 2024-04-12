package com.example.pointofsales.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
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
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var layoutCartEmpty: ConstraintLayout
    private lateinit var buttonCheckout: AppCompatButton
    private lateinit var tvTotalPrice: TextView
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var paymentSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartRecyclerView = view.findViewById(R.id.rvCart)
        layoutCartEmpty = view.findViewById(R.id.layout_cart_empty)
        buttonCheckout = view.findViewById(R.id.buttonCheckout)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        paymentSpinner = view.findViewById(R.id.paymentSpinner)

        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(requireContext(), cartViewModel)
        cartRecyclerView.adapter = cartAdapter

        checkCartEmpty()
        updateTotalPrice()

        buttonCheckout.setOnClickListener {
            val selectedPaymentMethod = paymentSpinner.selectedItem.toString()
            if (selectedPaymentMethod == "Cash") {
                showCashCheckoutConfirmationDialog()
            } else if (selectedPaymentMethod == "Gcash") {
                showGcashCheckoutConfirmationDialog()
            }
        }

        val paymentMethods = arrayOf("Cash", "Gcash")
        val paymentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentMethods)
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentSpinner.adapter = paymentAdapter

        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle payment method selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        inventoryViewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.setItems(cartItems)
            checkCartEmpty()
            updateTotalPrice()
        }

        val employeeName = activity?.intent?.getStringExtra("employeeName")
        val employeeNameTextView: TextView = view.findViewById(R.id.employeeNamePlaceHolder)
        employeeNameTextView.text = "Employee: $employeeName"
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
        val formattedTotalPrice = String.format("Total: %.2f", totalPrice)
        tvTotalPrice.text = formattedTotalPrice
    }

    private fun showCashCheckoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Order?")
            .setMessage("Are you sure you want to place the order?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                val paymentMethod = "Cash"
                val referenceNumber = "" // For cash payment, no reference number is needed
                sendOrderToFirebase(inventoryViewModel, paymentMethod, referenceNumber)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showGcashCheckoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_checkout_confirmation, null)
        val referenceNumberEditText: EditText = dialogView.findViewById(R.id.referenceNumberEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Order?")
            .setView(dialogView)
            .setPositiveButton("Yes") { dialog, _ ->
                val paymentMethod = "Gcash"
                val referenceNumber = referenceNumberEditText.text.toString()
                sendOrderToFirebase(inventoryViewModel, paymentMethod, referenceNumber)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendOrderToFirebase(inventoryViewModel: InventoryViewModel, paymentMethod: String, referenceNumber: String) {
        cartViewModel.sendOrderToFirebase(requireContext(), inventoryViewModel, paymentMethod, referenceNumber)
    }
}
