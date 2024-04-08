package com.example.pointofsales.fragments

import com.example.pointofsales.adapter.InventoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.decoration.GridSpacingItemDecoration
import com.example.pointofsales.viewmodel.InventoryViewModel

class InventoryFragment : Fragment() {

    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var addItemButton: Button
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = 3 // Number of columns in the grid
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing) // Set your desired spacing dimension here
        val includeEdge = true // Set whether to include spacing at the edges

        // Initialize RecyclerView
        inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView)
        inventoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        inventoryRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))

        // Initialize ViewModel
        inventoryViewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // Initialize Adapter
        inventoryAdapter = InventoryAdapter(requireContext(), inventoryViewModel)
        inventoryRecyclerView.adapter = inventoryAdapter

        // Observe inventory items from ViewModel
        inventoryViewModel.inventoryItems.observe(viewLifecycleOwner, Observer { items ->
            inventoryAdapter.setItems(items)
        })

        // Fetch inventory items from ViewModel
        inventoryViewModel.fetchInventoryItems()

        // Initialize addItemButton and set OnClickListener
        addItemButton = view.findViewById(R.id.addItemButton)
        addItemButton.setOnClickListener {
            // Replace the current FrameLayout with AddItemFormFragment
            replaceWithAddItemFormFragment()
        }

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView2)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                inventoryAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun replaceWithAddItemFormFragment() {
        val fragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(
            R.id.fragmentContainer,
            AddItemFormFragment()
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
