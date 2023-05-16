package com.goodlucky.finance.statistics

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityStatisticBinding
import com.goodlucky.finance.items.MyAccount
import com.goodlucky.finance.items.MyCategoryWithSum
import com.goodlucky.finance.items.MyCurrency
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class StatisticActivity : AppCompatActivity(), CategoryStatisticAdapter.Listener {

    private lateinit var initialDate : String
    private lateinit var finalDate : String
    private var dateAndTime = Calendar.getInstance()

    private var sumCosts : Double = 0.0
    private var sumIncomes : Double = 0.0

    private lateinit var myDbManager: MyDbManager
    private val adapter = CategoryStatisticAdapter(this,this)

    private lateinit var binding : ActivityStatisticBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        // ResycleView
        binding.statisticRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.statisticRecyclerView.adapter = adapter

        // Создание слушателя нажатий
        val onEditTextClickListener = View.OnClickListener { view ->
            when (view.id) {
                binding.statisticsInitialDate.id -> {
                    DatePickerDialog(
                        this@StatisticActivity, dateListenerInitialDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                binding.statisticsFinalDate.id -> {
                    DatePickerDialog(
                        this@StatisticActivity, dateListenerFinalDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
        //Назначение слушателя для текстовых полей
        binding.statisticsInitialDate.setOnClickListener(onEditTextClickListener)
        binding.statisticsFinalDate.setOnClickListener(onEditTextClickListener)

        binding.statisticsSpinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createAdapterCategoryWithSum()
                setSumCostsAndIncome()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.statisticsSpinnerCurrencies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createAccountAdapter()
               // createAdapterCategoryWithSum()
                //setSumCostsAndIncome()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        //  Установка текущей даты
        setDateTime(calendar.timeInMillis, binding.statisticsFinalDate, false, isNeedToUpdateStatistic = false)
        // Установка в календарь первого дня текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Установка первого числа текущего месяца
        setDateTime(calendar.timeInMillis, binding.statisticsInitialDate, true, isNeedToUpdateStatistic = false)

        createCurrencyAdapter()
        createAccountAdapter()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){android.R.id.home -> finish()}
        return super.onOptionsItemSelected(item)
    }

    override fun onCategoryClick(myCategory: MyCategoryWithSum) {

    }

    // Обработчик события для выбора начальной даты
    private var dateListenerInitialDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, binding.statisticsInitialDate, true)
    }

    // Обработчик события для выбора конечной даты
    private var dateListenerFinalDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, binding.statisticsFinalDate, false)
    }

    /*Метод устанавливает дату в текстовое поле, где milliseconds - время в миллисекундах,
    editText - текстовое поле,
    isInitial - определяет какую переменную необходимо обновить
    isNeedToUpdateStatistic - определяет нужно ли пересоздать адаптер списка и статистику*/
    private fun setDateTime(milliseconds : Long = 0, editText : EditText, isInitial : Boolean, isNeedToUpdateStatistic : Boolean = true) {
        editText.setText(DateUtils.formatDateTime(
            this,
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

        if (isNeedToUpdateStatistic){
            createAdapterCategoryWithSum()
            setSumCostsAndIncome()
        }
    }

    // Создание адаптера списка валют
    private fun createCurrencyAdapter(){
        val adapterCurrency = ArrayAdapter(this@StatisticActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        binding.statisticsSpinnerCurrencies.adapter = adapterCurrency
    }
    // Создание адаптера списка счетов
    private fun createAccountAdapter(){
        val selectedCurrency = binding.statisticsSpinnerCurrencies.selectedItem as MyCurrency
        val accountList: java.util.ArrayList<MyAccount> = arrayListOf(MyAccount(0, this.resources.getString(R.string.allAccounts), 0, selectedCurrency._id))
        accountList.addAll(myDbManager.fromAccountsByCurrency(selectedCurrency._id))

        val adapterAccounts = ArrayAdapter(
            this@StatisticActivity,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            accountList
        )
        binding.statisticsSpinnerAccounts.adapter = adapterAccounts
    }

    // Создать адаптер для списка категорий с суммой
    private fun createAdapterCategoryWithSum(){
        val listCategoriesWithSum = ArrayList<MyCategoryWithSum>()

        //***Расходы***
        val listCategoriesCost = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST)

        val selectedAccount = binding.statisticsSpinnerAccounts.selectedItem as MyAccount
        for(category in listCategoriesCost){
            //Если выбраны всё счета, иначе какой-то конкретно
            var sum = 0.0
            if (selectedAccount._id == (0).toLong()) {
                val selectedCurrency = binding.statisticsSpinnerCurrencies.selectedItem as MyCurrency
                val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
                for (account in listAccounts){
                    sum += myDbManager.getSumCostByCategory(category._id, account._id, initialDate, finalDate).roundToInt()
                }
            }
            else{
                sum= myDbManager.getSumCostByCategory(category._id, selectedAccount._id, initialDate, finalDate)
            }

            if(sum != 0.0) {
                val myCategoryWithSum = MyCategoryWithSum(category._id, category._name, category._color,
                    category._image, sum, category._type)
                listCategoriesWithSum.add(myCategoryWithSum)
            }
        }

        //***Доходы***
        val listCategoriesIncome = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_INCOME)
        for(category in listCategoriesIncome){
            //Если выбраны всё счета, иначе какой-то конкретно
            var sum = 0.0
            if (selectedAccount._id == (0).toLong()){
                val selectedCurrency = binding.statisticsSpinnerCurrencies.selectedItem as MyCurrency
                val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
                for (account in listAccounts){
                    sum += myDbManager.getSumIncomeByCategory(category._id, account._id, initialDate, finalDate).roundToInt()
                }
            }
            else{
                sum = myDbManager.getSumIncomeByCategory(category._id, selectedAccount._id, initialDate, finalDate)
            }

            if (sum != 0.0) {
                val myCategoryWithSum = MyCategoryWithSum(category._id, category._name, category._color,
                    category._image, sum, category._type)
                listCategoriesWithSum.add(myCategoryWithSum)
            }
        }
        listCategoriesWithSum.sortedByDescending { it._sum  }
        adapter.addAllCategoryList(listCategoriesWithSum)
    }

    // Установить сумму расходов и доходов
    private fun setSumCostsAndIncome(){
        val account = findViewById<Spinner>(R.id.statisticsSpinnerAccounts) .selectedItem as MyAccount
        sumCosts = 0.0
            if (account._id == (0).toLong()){
                val selectedCurrency = binding.statisticsSpinnerCurrencies.selectedItem as MyCurrency
                val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
                for (myAccount in listAccounts){
                    sumCosts += myDbManager.getSumCostByAccount(myAccount._id, initialDate, finalDate)
                }
            }
            else{
                sumCosts = myDbManager.getSumCostByAccount(account._id, initialDate, finalDate)
            }
        sumIncomes = 0.0
            if (account._id == (0).toLong()){
                val selectedCurrency = binding.statisticsSpinnerCurrencies.selectedItem as MyCurrency
                val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
                for (myAccount in listAccounts){
                    sumIncomes += myDbManager.getSumIncomeByAccount(myAccount._id, initialDate, finalDate)
                }
            }
            else {
                sumIncomes = myDbManager.getSumIncomeByAccount(account._id, initialDate, finalDate)
            }

        val decimalFormat = DecimalFormat("##0.00")
        val strSumCosts: String = decimalFormat.format(sumCosts)
        val strSumIncomes: String = decimalFormat.format(sumIncomes)
        val strResult : String = decimalFormat.format(sumIncomes - sumCosts)

        binding.statisticsMainResultText.text = ""
        binding.statisticsMainResultText.append("Расходы: - $strSumCosts \n")
        binding.statisticsMainResultText.append("Доходы: + $strSumIncomes \n")
        binding.statisticsMainResultText.append("Итого: $strResult \n")
    }
}