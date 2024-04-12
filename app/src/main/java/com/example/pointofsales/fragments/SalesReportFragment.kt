package com.example.pointofsales.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pointofsales.R
import com.example.pointofsales.viewmodel.SalesReportViewModel

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class SalesReportFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var salesReportViewModel: SalesReportViewModel
    private lateinit var textViewWeekSales: TextView
    private lateinit var textViewMonthSales: TextView
    private lateinit var textViewYearSales: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_report, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.barChart)
        textViewWeekSales = view.findViewById(R.id.textViewWeekSales)
        textViewMonthSales = view.findViewById(R.id.textViewMonthSales)
        textViewYearSales = view.findViewById(R.id.textViewYearSales)

        // Initialize ViewModel
        salesReportViewModel = ViewModelProvider(this).get(SalesReportViewModel::class.java)

        // Observe monthly income data
        salesReportViewModel.monthlyIncome.observe(viewLifecycleOwner) { monthlyIncomeMap ->
            updateBarChart(monthlyIncomeMap)
        }

        // Fetch and display weekly, monthly, and yearly sales
        salesReportViewModel.fetchWeeklySales().observe(viewLifecycleOwner) { weeklySales ->
            textViewWeekSales.text = getString(R.string.current_week_sales, weeklySales)
        }

        salesReportViewModel.fetchMonthlySales().observe(viewLifecycleOwner) { monthlySales ->
            textViewMonthSales.text = getString(R.string.current_month_sales, monthlySales)
        }

        salesReportViewModel.fetchYearlySales().observe(viewLifecycleOwner) { yearlySales ->
            textViewYearSales.text = getString(R.string.current_year_sales, yearlySales)
        }

        // Fetch monthly income data
        salesReportViewModel.fetchMonthlyIncome()

        // Set up bar chart
        setUpBarChart()
    }


    private fun setUpBarChart() {
        // Customize bar chart appearance
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(months) // Provide month labels
        xAxis.granularity = 1f // Set interval to 1 (one bar per month)

        barChart.axisRight.isEnabled = true // Disable right axis
        barChart.description.isEnabled = false // Disable description
        barChart.legend.isEnabled = false // Disable legend
        barChart.setFitBars(true) // Set bars to fit screen width
    }

    private fun updateBarChart(monthlyIncomeMap: Map<String, Double>) {
        val barData = BarData()
        val entries = mutableListOf<BarEntry>()
        val monthLabels = mutableListOf<String>() // Store month labels for x-axis
        var index = 0

        // Iterate through monthly income map
        for ((month, totalPrice) in monthlyIncomeMap) {
            entries.add(BarEntry(index.toFloat(), totalPrice.toFloat()))
            monthLabels.add(month) // Add month label for x-axis
            index++
        }

        val dataSet = BarDataSet(entries, "Monthly Sales")
        barData.addDataSet(dataSet)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels.toTypedArray()) // Set x-axis labels
        barChart.data = barData
        barChart.invalidate() // Refresh chart
    }

    companion object {
        private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May") // Months labels
    }
}
