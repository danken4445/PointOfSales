package com.example.pointofsales.fragments

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.barChart)

        // Initialize ViewModel
        salesReportViewModel = ViewModelProvider(this).get(SalesReportViewModel::class.java)

        // Observe monthly income data
        salesReportViewModel.monthlyIncome.observe(viewLifecycleOwner) { monthlyIncomeMap ->
            updateBarChart(monthlyIncomeMap)
        }

        // Set up bar chart
        setUpBarChart()
    }

    private fun setUpBarChart() {
        // Customize bar chart appearance
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(months) // Provide month labels
        xAxis.granularity = 1f // Set interval to 1 (one bar per month)

        barChart.axisRight.isEnabled = false // Disable right axis
        barChart.description.isEnabled = false // Disable description
        barChart.legend.isEnabled = false // Disable legend
        barChart.setFitBars(true) // Set bars to fit screen width
    }

    private fun updateBarChart(monthlyIncomeMap: Map<String, Double>) {
        val entries = mutableListOf<BarEntry>()
        months.forEachIndexed { index, month ->
            val income = monthlyIncomeMap[month] ?: 0.0
            entries.add(BarEntry(index.toFloat(), income.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Monthly Income")

        val data = BarData(dataSet)
        barChart.data = data

        barChart.invalidate() // Refresh chart
    }

    companion object {
        private val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May") // Months labels
    }
}

