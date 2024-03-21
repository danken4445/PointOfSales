package com.example.pointofsales.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.adapter.CartAdapter
import com.example.pointofsales.data.CartItem
import com.example.pointofsales.data.SalesItem
import com.example.pointofsales.viewmodel.CartViewModel

class CartFragment : Fragment() {

    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var layoutCartEmpty: ConstraintLayout
    private lateinit var buttonCheckout: AppCompatButton

    private var salesItem: SalesItem? = null

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

        // Initialize RecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Adapter
        cartAdapter = CartAdapter()
        cartRecyclerView.adapter = cartAdapter

        // Set visibility of cart empty layout
        checkCartEmpty()

        // Observe cart items LiveData and update the adapter accordingly
        cartViewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.setItems(cartItems)
            checkCartEmpty() // Check cart empty after updating items
        }

        // Add the sales item to the cart
        salesItem?.let {
            cartViewModel.addItemToCart(it)
        }

        // Set onClickListener for checkout button
        buttonCheckout.setOnClickListener {
            Toast.makeText(requireContext(), "Item Checked Out", Toast.LENGTH_SHORT).show()
        }
    }

    fun setSalesItem(item: SalesItem) {
        salesItem = item
        // Add the sales item to the cart when it is set
        cartViewModel.addItemToCart(item)
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
}

