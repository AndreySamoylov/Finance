package com.goodlucky.finance.costUtilities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.goodlucky.finance.R
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.items.MyAccount
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.goodlucky.finance.CategoryListActivity
import com.goodlucky.finance.items.MyCurrency
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class CostFragment : Fragment() {

    private lateinit var buttonGoToEditCosts: Button
    private lateinit var buttonGoToAllCostOperations: Button
    private lateinit var pieChart: PieChart
    private lateinit var spinnerAccounts : Spinner
    private lateinit var spinnerCurrency : Spinner
    private lateinit var editTextInitialDateCost : EditText
    private lateinit var initialDate : String
    private lateinit var editTextFinalDateCost : EditText
    private lateinit var finalDate : String

    private var dateAndTime = Calendar.getInstance()

        private lateinit var myDbManager: MyDbManager

    private lateinit var currentContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myDbManager = MyDbManager(inflater.context)

        currentContext = inflater.context

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cost, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonGoToEditCosts = view.findViewById(R.id.buttonGoToEditCosts)
        buttonGoToAllCostOperations = view.findViewById(R.id.buttonGoToAllCostOperations)
        pieChart = view.findViewById(R.id.pieChartCosts)
        spinnerAccounts = view.findViewById(R.id.fragmentCostSpinnerAccount)
        spinnerCurrency = view.findViewById(R.id.fragmentCostSpinnerCurrencies)
        editTextInitialDateCost = view.findViewById(R.id.fragmentCostInitialDate)
        editTextFinalDateCost = view.findViewById(R.id.fragmentCostFinalDate)

        buttonGoToEditCosts.setOnClickListener {
            val intent = Intent(currentContext, CostEditActivity::class.java)
            intent.putExtra(MyConstants.STATE, MyConstants.STATE_ADD)
            startActivity(intent)
        }

        buttonGoToAllCostOperations.setOnClickListener {
            val intent = Intent(currentContext, ListCostActivity::class.java)
            startActivity(intent)
        }

        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createPieChart()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createAccountAdapter()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        // Создание слушателя нажатий на текстовые поля
        val onEditTextClickListener = View.OnClickListener { mainView ->
            when (mainView.id) {
                R.id.fragmentCostInitialDate -> {
                    DatePickerDialog(
                        currentContext, dateListenerInitialDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.fragmentCostFinalDate -> {
                    DatePickerDialog(
                        currentContext, dateListenerFinalDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
        // Назначение слушателя для текстовых полей
        editTextInitialDateCost.setOnClickListener(onEditTextClickListener)
        editTextFinalDateCost.setOnClickListener(onEditTextClickListener)

        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener{
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pieEntry = e as PieEntry
                val label = pieEntry.label
                val value = pieEntry.value.toDouble()
                val selectedAccount = spinnerAccounts.selectedItem as MyAccount
                val selectedCurrency = spinnerCurrency.selectedItem as MyCurrency

                val intent = Intent(currentContext, CategoryListActivity::class.java)
                intent.putExtra(MyConstants.STATE, MyConstants.COST)
                intent.putExtra(MyConstants.INITIAL_DATE, initialDate)
                intent.putExtra(MyConstants.FINAL_DATE, finalDate)
                intent.putExtra(MyConstants.VALUE, value)
                intent.putExtra(MyConstants.ID_ACCOUNT, selectedAccount._id)
                intent.putExtra(MyConstants.ID_CURRENCY, selectedCurrency   ._id)
                intent.putExtra(MyConstants.NAME_CATEGORY, label)
                startActivity(intent)
            }

            override fun onNothingSelected() {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds

        // Установка текущей даты
        setDateTime(calendar.timeInMillis, editTextFinalDateCost, false, isCreateChart = false)
        // Установка в календарь первого дня текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Установка первого числа текущего месяца
        setDateTime(calendar.timeInMillis, editTextInitialDateCost, true, isCreateChart = false)

        createCurrencyAdapter()
        createAccountAdapter()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    companion object{
        @JvmStatic
        fun newInstance() = CostFragment()
    }

    // Метод создаёт круговую диаграмму
    private fun createPieChart(){
        lateinit var selectedAccount : MyAccount
        try{
            selectedAccount = spinnerAccounts.selectedItem as MyAccount // Выбранный счёт
        }catch (exception : NullPointerException){
            Log.d("CostFragment", exception.toString())
            return
        }

        val pieEntriesAll = ArrayList<PieEntry>()
        var allSum = 0f // Сумма всех расходов
        val categoryList = myDbManager.fromCategories // Список категорий
        for (category in categoryList){
            // Если выбран 1-ый элемент волчка, то есть нужно делать выборку из всех счетов в списке
            // Иначе сделать выборку из одного выбранного счёта
            var sum = 0
            if (selectedAccount._id == (0).toLong()) {
                val selectedCurrency = spinnerCurrency.selectedItem as MyCurrency
                val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
                for (account in listAccounts){
                    sum += myDbManager.getSumCostByCategory(category._id, account._id, initialDate, finalDate).roundToInt()
                }
            }
            else{
                sum +=  myDbManager.getSumCostByCategory(category._id, selectedAccount._id, initialDate, finalDate).roundToInt()
            }

            if(sum > 0) { // Если сумма больше нуля добавить в список
                val pieEntry = PieEntry(sum.toFloat(), category._name)
                pieEntriesAll.add(pieEntry)
            }
            allSum += sum
        }

        val pieEntriesSelective = ArrayList<PieEntry>() // Список который будет добавлен в диаграмму
        var otherSum = 0f // Сумма остальных категорий
        for (pieEntry in pieEntriesAll){
            // Процент суммы одной категории от общей суммы
            val percent : Float = (100 * pieEntry.value) / allSum
            if (percent > 4){ // Если этот процент больше Х
                pieEntriesSelective.add(pieEntry) // Добавить в список
            }else{
                otherSum += pieEntry.value //
            }
        }
        // Добавить остальную сумму в виде последней записи
        if (otherSum != 0f) pieEntriesSelective.add(PieEntry(otherSum, currentContext.resources.getString(R.string.other)))

        // Сортировка списка по убыванию, по сумме
        pieEntriesSelective.sortedByDescending { it.value}

        val pieDataSet = PieDataSet(pieEntriesSelective, currentContext.resources.getString(R.string.categories))
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 255)
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        pieChart.legend.form = Legend.LegendForm.CIRCLE
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK){
            Configuration.UI_MODE_NIGHT_NO -> pieChart.legend.textColor = Color.BLACK
            Configuration.UI_MODE_NIGHT_YES -> pieChart.legend.textColor = Color.WHITE
        }
        pieChart.description.isEnabled = false
        pieChart.centerText = currentContext.resources.getString(R.string.cost)
        pieChart.animateX(500)
        pieChart.animateX(500)
    }

    // Обработчик события для выбора начальной даты
    private var dateListenerInitialDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, editTextInitialDateCost, true)
    }

    // Обработчик события для выбора конечной даты
    private var dateListenerFinalDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, editTextFinalDateCost, false)
    }

    /*Метод устанавливает дату в текстовое поле, где milliseconds - время в миллисекундах,
    editText - текстовое поле,
    isInitial - определяет какую переменную необходимо обновить
    isCreateChart - определяет нужно ли пересоздать круговую диаграмму*/
    private fun setDateTime(milliseconds : Long = 0, editText : EditText, isInitial : Boolean,  isCreateChart : Boolean = true) {
        editText.setText(DateUtils.formatDateTime(
                currentContext,
                milliseconds,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))


        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        if (isInitial){
            initialDate = if (month < 10) {//Приведение даты в формат YYYY-MM-DD
                if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
                else "${year}-0${month}-${dayOfMonth}"
            } else {
                if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
                else "${year}-${month}-${dayOfMonth}"
            }
        }
        else{
            finalDate = if (month < 10) {
                if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
                else "${year}-0${month}-${dayOfMonth}"
            } else {
                if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
                else "${year}-${month}-${dayOfMonth}"
            }
        }

        if (isCreateChart) createPieChart()
    }

    // Создание адаптера списка валют
    private fun createCurrencyAdapter(){
        val adapterCurrency = ArrayAdapter(currentContext, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        spinnerCurrency.adapter = adapterCurrency
    }

    // Создание адаптера списка счетов
    private fun createAccountAdapter(){
        val selectedCurrency = spinnerCurrency.selectedItem as MyCurrency
        val accountList: java.util.ArrayList<MyAccount> = arrayListOf(MyAccount(0, currentContext.resources.getString(R.string.allAccounts), 0, selectedCurrency._id))
        accountList.addAll(myDbManager.fromAccountsByCurrency(selectedCurrency._id))

        val adapterAccounts = ArrayAdapter(
            currentContext,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            accountList
        )
        spinnerAccounts.adapter = adapterAccounts
    }
}