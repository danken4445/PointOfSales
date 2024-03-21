package com.example.pointofsales.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.adapter.InventoryAdapter
import com.example.pointofsales.viewmodel.InventoryViewModel

class InventoryFragment : Fragment() {

    private lateinit var inventoryRecyclerView: RecyclerView
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var addItemButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView)
        inventoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize ViewModel
        inventoryViewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)

        // Initialize Adapter
        inventoryAdapter = InventoryAdapter(requireContext())
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
