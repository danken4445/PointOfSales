package com.example.pointofsales.adapter.history

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.OrderItems
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter(private val employeeName: String) : ListAdapter<OrderItems, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderIdTextView: TextView = itemView.findViewById(R.id.textViewOrderId)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.textViewDateTime)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.textViewTotalPrice)
        private val productsRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerViewProducts)
        private val empNameHistoryTextView: TextView = itemView.findViewById(R.id.empNameHistory)
        private val paymentMethodTextView: TextView = itemView.findViewById(R.id.textViewPaymentMethod)
        private val referenceNo: TextView = itemView.findViewById(R.id.textViewReferenceNumber)

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH"))

        fun bind(order: OrderItems) {
            orderIdTextView.text = Html.fromHtml("Order ID: <b>${order.orderId}</b>")
            dateTimeTextView.text = Html.fromHtml("Date & Time: <b>${order.dateTime}</b>")

            // Format total price as currency
            val formattedPrice = currencyFormat.format(order.totalPrice)
            totalPriceTextView.text = Html.fromHtml("Total Price: <b>$formattedPrice</b>")

            empNameHistoryTextView.text = Html.fromHtml("Employee: <b>$employeeName</b>") // Set employee name
            paymentMethodTextView.text = Html.fromHtml("Payment Method: <b>${order.paymentMethod}</b>")
            referenceNo.text = Html.fromHtml("Reference Number: <b>${order.referenceNumber}</b>") // Set employee name

            val productsAdapter = ProductsAdapter()
            productsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            productsRecyclerView.adapter = productsAdapter
            productsAdapter.submitList(order.products)
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<OrderItems>() {
        override fun areItemsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem == newItem
        }
    }
}
