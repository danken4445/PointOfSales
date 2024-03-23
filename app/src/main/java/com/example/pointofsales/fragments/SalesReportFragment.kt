import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pointofsales.R
import com.example.pointofsales.viewmodel.SalesReportViewModel
import java.text.SimpleDateFormat
import java.util.*

class SalesReportFragment : Fragment() {

    private lateinit var viewModel: SalesReportViewModel
    private lateinit var monthlyIncomeTextView: TextView
    private lateinit var weeklyIncomeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SalesReportViewModel::class.java)

        monthlyIncomeTextView = view.findViewById(R.id.textViewMonthlyIncome)
        weeklyIncomeTextView = view.findViewById(R.id.textViewWeeklyIncome)

        viewModel.monthlyIncome.observe(viewLifecycleOwner) { monthlyIncome ->
            monthlyIncomeTextView.text = ("Monthly Income: " + monthlyIncome)
        }

        viewModel.weeklyIncome.observe(viewLifecycleOwner) { weeklyIncome ->
            weeklyIncomeTextView.text = ("Weekly Income" + weeklyIncome)
        }

    }
}
