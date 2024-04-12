package com.example.pointofsales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

class SalesReportViewModel : ViewModel() {

    private val databaseRef = FirebaseDatabase.getInstance().getReference("POSorders")

    // Map to store monthly income data (month-year string as key, total sales as value)
    private val _monthlyIncome = MutableLiveData<Map<String, Double>>()
    val monthlyIncome: LiveData<Map<String, Double>> = _monthlyIncome

    fun fetchMonthlyIncome() {
        val monthlyIncomeMap = mutableMapOf<String, Double>()

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderTime = orderSnapshot.child("dateAndTime").getValue(String::class.java)
                        ?.let { convertDateStringToMonthYear(it) } ?: continue
                    val totalPrice =
                        orderSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.00

                    monthlyIncomeMap[orderTime] = (monthlyIncomeMap[orderTime] ?: 0.00) + totalPrice
                }

                _monthlyIncome.value = monthlyIncomeMap
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error (consider logging or displaying a user-friendly message)
            }
        })
    }

    private fun convertDateStringToMonthYear(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return try {
            val date = formatter.parse(dateString)
            SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // Generic function to calculate sales within a date range (now used)
    private fun calculateSalesForDateRange(startDate: Long, endDate: Long): LiveData<String> {
        val sales = MutableLiveData<String>()

        databaseRef.orderByChild("dateAndTime")
            .startAt(convertMillisToDateString(startDate))
            .endAt(convertMillisToDateString(endDate))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalSales = 0.00
                    for (orderSnapshot in snapshot.children) {
                        val totalPrice =
                            orderSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.00
                        totalSales += totalPrice
                    }
                    sales.value = formatCurrency(totalSales)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error (consider logging or displaying a user-friendly message)
                }
            })

        return sales
    }

    // Function to fetch and calculate weekly sales (using calculateSalesForDateRange)
    fun fetchWeeklySales(): LiveData<String> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis // End date is today
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // Set to the first day of the week
        val startDate = calendar.timeInMillis // Start date is the first day of the week
        return calculateSalesForDateRange(startDate, endDate) // Call the generic function here
    }

    // Function to fetch and calculate monthly sales (using calculateSalesForDateRange)
    fun fetchMonthlySales(): LiveData<String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
        val startDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1) // Move to the next month
        val endDate = calendar.timeInMillis // End date is the first day of the next month
        return calculateSalesForDateRange(startDate, endDate) // Call the generic function here
    }

    fun fetchYearlySales(): LiveData<String> {
        val yearlySales = MutableLiveData<String>()

        // Calculate start and end date for the current year
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JANUARY) // Set to January
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of January
        val startDate = calendar.timeInMillis
        calendar.add(Calendar.YEAR, 1) // Move to the next year
        val endDate = calendar.timeInMillis // End date is the first day of January of the next year

        // Query database for orders within the current year
        databaseRef.orderByChild("dateAndTime")
            .startAt(convertMillisToDateString(startDate))
            .endAt(convertMillisToDateString(endDate))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalSales = 0.00
                    for (orderSnapshot in snapshot.children) {
                        val totalPrice =
                            orderSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.00
                        totalSales += totalPrice
                    }
                    yearlySales.value = formatCurrency(totalSales)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })

        return yearlySales
    }

        private fun convertDateStringToMillis(dateString: String): Long {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            return try {
                val date = formatter.parse(dateString)
                date?.time ?: 0L
            } catch (e: Exception) {
                e.printStackTrace()
                0L
            }
        }

        private fun convertMillisToDateString(millis: Long): String {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            return formatter.format(millis)
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
            return format.format(amount)
        }
    }
