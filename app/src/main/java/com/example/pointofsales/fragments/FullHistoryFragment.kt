package com.example.pointofsales.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.adapter.history.HistoryAdapter
import com.example.pointofsales.viewmodel.HistoryViewModel

class FullHistoryFragment : Fragment(R.layout.fragment_full_history) {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        recyclerView = view.findViewById(R.id.historyRecyclerView)

        val employeeName = requireActivity().intent.getStringExtra("employeeName") ?: "Employee Name"
        val historyAdapter = HistoryAdapter(employeeName)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = historyAdapter
        recyclerView.addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.item_spacing)))

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            historyAdapter.submitList(orders) // Use historyAdapter instead of adapter
        }

        viewModel.fetchOrders()
    }

    private class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom = space
        }
    }
}
