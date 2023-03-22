package com.goodlucky.finance.statistics

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityStatisticBinding
import com.goodlucky.finance.items.MyAccount
import com.goodlucky.finance.items.MyCategoryWithSum
import java.util.*
import kotlin.collections.ArrayList
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
                        this@StatisticActivity, dateListenerDateFrom,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                binding.statisticsFinalDate.id -> {
                    DatePickerDialog(
                        this@StatisticActivity, dateListenerDateBefore,
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
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        // Установить сегодняшную дату в переменную
        initialDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }
        // Установить первое число текущего месяца
        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-01"
            else "${year}-0${month}-01"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-01"
            else "${year}-${month}-01"
        }

        createAccountAdapter()
        // createAdapterCategoryWithSum()

        //setSumCostsAndIncome()

        setDateTimeFinal(calendar.timeInMillis)  //  Установка текущей даты

        calendar.set(Calendar.DAY_OF_MONTH, 1)  // Установка в календарь первого дня текущего месяца
        setDateTimeInitial(calendar.timeInMillis)  // Установка первого числа текущего месяца

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

    // Обработчик события для выбора даты "от которой нужно считать"
    private var dateListenerDateFrom = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTimeInitial(dateAndTime.timeInMillis)
    }

    // Обработчик события для выбора даты "до которой нужно считать"
    private var dateListenerDateBefore = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTimeFinal(dateAndTime.timeInMillis)
    }

    private fun setDateTimeInitial(milliseconds : Long = 0) {
        binding.statisticsInitialDate.setText(
            DateUtils.formatDateTime(
                this@StatisticActivity,
                milliseconds,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        initialDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }

        createAdapterCategoryWithSum()
        setSumCostsAndIncome()
    }

    private fun setDateTimeFinal(milliseconds : Long = 0) {
        binding.statisticsFinalDate.setText(
            DateUtils.formatDateTime(
                this@StatisticActivity,
                milliseconds,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
            )
        )

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        //Приведение даты в формат YYYY-MM-DD
        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }

        createAdapterCategoryWithSum()
        setSumCostsAndIncome()
    }

    // Метод создат адаптер для списка счетов
    private fun createAccountAdapter(){
        val accountList: ArrayList<MyAccount> = arrayListOf(MyAccount(0, "Все счета", 0, 0))
        accountList.addAll(myDbManager.fromAccounts)

        val adapterAccounts = ArrayAdapter(this@StatisticActivity,R.layout.account_item,
            R.id.textViewItemAccountName,accountList)
        binding.statisticsSpinnerAccounts.adapter = adapterAccounts
    }

    // Создать адаптер для списка категорий с суммой
    private fun createAdapterCategoryWithSum(){
        val listCategoriesWithSum = ArrayList<MyCategoryWithSum>()

        val listCategoriesCost = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST)

        val selectedAccount = binding.statisticsSpinnerAccounts.selectedItem as MyAccount
        for(category in listCategoriesCost){
            //Если выбраны всё счета, иначе какой-то конкретно
            val sum = if (selectedAccount._id == (0).toLong()) myDbManager.getSumCostByCategory(category._id,  initialDate, finalDate)
            else myDbManager.getSumCostByCategory(category._id, selectedAccount._id, initialDate, finalDate)

            if(sum != 0.0) {
                val myCategoryWithSum = MyCategoryWithSum(category._id, category._name, category._color,
                    category._image, sum, category._type)
                listCategoriesWithSum.add(myCategoryWithSum)
            }
        }

        val listCategoriesIncome = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_INCOME)
        for(category in listCategoriesIncome){
            //Если выбраны всё счета, иначе какой-то конкретно
            val sum = if (selectedAccount._id == (0).toLong()) myDbManager.getSumIncomeByCategory(category._id,  initialDate, finalDate)
            else myDbManager.getSumIncomeByCategory(category._id, selectedAccount._id, initialDate, finalDate)

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
        sumCosts =
            if (account._id == (0).toLong()) myDbManager.getSumCostByAccount(initialDate, finalDate)
            else myDbManager.getSumCostByAccount(account._id, initialDate, finalDate)
        sumIncomes =
            if (account._id == (0).toLong()) myDbManager.getSumIncomeByAccount(initialDate, finalDate)
            else myDbManager.getSumIncomeByAccount(account._id, initialDate, finalDate)

        binding.statisticsMainResultText.text = ""
        binding.statisticsMainResultText.append("Расходы: - $sumCosts \n")
        binding.statisticsMainResultText.append("Доходы: + $sumIncomes \n")
        binding.statisticsMainResultText.append("Итого: ${sumIncomes - sumCosts} \n")
    }
}