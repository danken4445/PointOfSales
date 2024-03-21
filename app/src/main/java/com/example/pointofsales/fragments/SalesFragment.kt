package com.example.pointofsales.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.adapter.SalesAdapter
import com.example.pointofsales.data.SalesItem
import com.example.pointofsales.viewmodel.CartViewModel
import com.example.pointofsales.viewmodel.SalesViewModel

class SalesFragment : Fragment(), SalesAdapter.OnAddToCartClickListener {
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var salesRecyclerView: RecyclerView
    private lateinit var salesViewModel: SalesViewModel
    private lateinit var salesAdapter: SalesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        salesRecyclerView = view.findViewById(R.id.salesRecyclerView)
        salesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewModel
        salesViewModel = ViewModelProvider(this).get(SalesViewModel::class.java)

        // Initialize Adapter
        salesAdapter = SalesAdapter(requireContext(), this) // Pass the listener
        salesRecyclerView.adapter = salesAdapter

        // Observe sales items from ViewModel
        salesViewModel.salesItems.observe(viewLifecycleOwner, Observer { items ->
            salesAdapter.setItems(items)
        })

        // Fetch sales items from ViewModel
        salesViewModel.fetchSalesItems()
    }

    override fun onAddToCartClick(item: SalesItem) {
        // Handle adding item to cart here
        cartViewModel.addItemToCart(item)
        val itemName = item.itemName
        Toast.makeText(requireContext(), "$itemName added to cart", Toast.LENGTH_SHORT).show()
    }
}
