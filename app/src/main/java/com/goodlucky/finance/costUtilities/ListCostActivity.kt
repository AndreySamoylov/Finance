package com.goodlucky.finance.costUtilities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.items.MyAccount
import com.goodlucky.finance.items.MyCost
import com.goodlucky.finance.items.MyCurrency
import java.util.Date
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

class ListCostActivity : AppCompatActivity(), MyCostAdapter.Listener {

    private val adapter = MyCostAdapter(this, this)

    private lateinit var editTextInitialDateCost : EditText
    private lateinit var initialDate : String
    private lateinit var editTextFinalDateCost : EditText
    private lateinit var finalDate : String
    private lateinit var spinnerAccounts : Spinner
    private lateinit var spinnerCurrencies : Spinner

    private lateinit var myDbManager : MyDbManager

    private var dateAndTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_cost)

        myDbManager = MyDbManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextInitialDateCost = findViewById(R.id.editTextInitialDateCost)
        editTextFinalDateCost = findViewById(R.id.editTextFinalDateCost)
        spinnerAccounts = findViewById(R.id.spinnerAccountOnShowCost)
        spinnerCurrencies = findViewById(R.id.ListCostSpinnerCurrencies)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCosts)

        // Создание слушателя нажатий
        val onEditTextClickListener = OnClickListener { view ->
            when (view.id){
                R.id.editTextInitialDateCost ->{
                    DatePickerDialog(
                        this@ListCostActivity, dateListenerDateFrom,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                    setRecycleViewAdapter()
                }
                R.id.editTextFinalDateCost ->{
                    DatePickerDialog(
                        this@ListCostActivity, dateListenerDateBefore,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                    setRecycleViewAdapter()
                }
            }
        }
        //Назначение слушателя для текстовых полей
        editTextInitialDateCost.setOnClickListener(onEditTextClickListener)
        editTextFinalDateCost.setOnClickListener(onEditTextClickListener)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        spinnerAccounts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setRecycleViewAdapter()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        spinnerCurrencies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createAccountAdapter()
                setRecycleViewAdapter()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        myDbManager.openDatabase()

        // Создание адаптера списка валют
        val adapterCurrency = ArrayAdapter(this@ListCostActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        spinnerCurrencies.adapter = adapterCurrency

        createAccountAdapter()

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
        // Установить первое число текущего мемяца
        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-01"
            else "${year}-0${month}-01"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-01"
            else "${year}-${month}-01"
        }

        setDateTimeFinal(calendar.timeInMillis)    // Установка текущей даты
        calendar.set(Calendar.DAY_OF_MONTH, 1)     // Установка в календарь первого дня текущего месяца
        setDateTimeInitial(calendar.timeInMillis)  // Установка первого числа текущего месяца

        myDbManager.closeDatabase()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        setRecycleViewAdapter()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onCostClick(myCost: MyCost) {
        val intent = Intent(this@ListCostActivity, CostEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_COST, myCost)
        startActivity(intent)
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
        editTextInitialDateCost.setText(
            DateUtils.formatDateTime(
                this,
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

        setRecycleViewAdapter()
    }

    private fun setDateTimeFinal(milliseconds : Long = 0) {
        editTextFinalDateCost.setText(
            DateUtils.formatDateTime(
                this,
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

        setRecycleViewAdapter()
    }

    private fun setRecycleViewAdapter(){
        val account : MyAccount = spinnerAccounts.selectedItem as MyAccount
        //Если выбран элемент с id ноль (т.е все счета)), то вывести данные из всех счетов, иначе из выбранного
        val list : ArrayList<MyCost> = arrayListOf()
        if (account._id  == (0).toLong()){
            val selectedCurrency = spinnerCurrencies.selectedItem as MyCurrency
            val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
            for (myAccount in listAccounts){
                list.addAll(myDbManager.fromCosts(initialDate, finalDate, myAccount._id))
            }
        }
        else{
            list.addAll(myDbManager.fromCosts(initialDate, finalDate, account._id))
        }
        adapter.addAllCostList(list)
    }

    // Создание адаптера для списка счетов
    private fun createAccountAdapter(){
        val selectedCurrency = spinnerCurrencies.selectedItem as MyCurrency
        val accountList: java.util.ArrayList<MyAccount> = arrayListOf(MyAccount(0, this.resources.getString(R.string.allAccounts), 0, selectedCurrency._id))
        accountList.addAll(myDbManager.fromAccountsByCurrency(selectedCurrency._id))

        val adapterAccounts = ArrayAdapter(
            this,
            R.layout.account_item,
            R.id.textViewItemAccountName,
            accountList
        )
        spinnerAccounts.adapter = adapterAccounts
    }
}