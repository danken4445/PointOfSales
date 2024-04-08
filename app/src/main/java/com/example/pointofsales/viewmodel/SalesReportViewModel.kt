package com.example.pointofsales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

class SalesReportViewModel : ViewModel() {

    private val databaseRef = FirebaseDatabase.getInstance().getReference("POSorders")

    private val _monthlyIncome = MutableLiveData<Map<String, Double>>() // Map to store monthly income
    val monthlyIncome: LiveData<Map<String, Double>> get() = _monthlyIncome

    init {
        calculateMonthlyIncome()
    }

    private fun calculateMonthlyIncome() {
        val monthlyIncomeMap = mutableMapOf<String, Double>() // Map to store monthly income

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderTime = orderSnapshot.child("dateAndTime").getValue(String::class.java)
                        ?.let { convertDateStringToMillis(it) } ?: continue
                    val totalPrice =
                        orderSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.0

                    // Extract month and year from orderTime
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = orderTime
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-indexed

                    val monthYear = "$year-$month"

                    monthlyIncomeMap[monthYear] = (monthlyIncomeMap[monthYear] ?: 0.0) + totalPrice
                }

                _monthlyIncome.value = monthlyIncomeMap
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun convertDateStringToMillis(dateString: String): Long {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            val date = formatter.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}
