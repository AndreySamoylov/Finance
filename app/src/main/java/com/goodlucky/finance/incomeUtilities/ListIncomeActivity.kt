package com.goodlucky.finance.incomeUtilities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.items.MyAccount
import com.goodlucky.finance.items.MyCurrency
import com.goodlucky.finance.items.MyIncome
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class ListIncomeActivity : AppCompatActivity(), MyIncomeAdapter.Listener {

    private val adapter = MyIncomeAdapter(this, this)

    private lateinit var editTextInitialDateIncome : EditText
    private lateinit var initialDate : String
    private lateinit var editTextFinalDateIncome : EditText
    private lateinit var finalDate : String
    private lateinit var spinnerAccounts : Spinner
    private lateinit var spinnerCurrencies : Spinner

    private lateinit var myDbManager : MyDbManager

    private var dateAndTime = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_income)

        myDbManager = MyDbManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextInitialDateIncome = findViewById(R.id.editTextInitialDateIncome)
        editTextFinalDateIncome = findViewById(R.id.editTextFinalDateIncome)
        spinnerAccounts = findViewById(R.id.spinnerAccountOnShowIncome)
        spinnerCurrencies = findViewById(R.id.listIncomeSpinnerCurrencies)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewIncome)

        // Создание слушателя нажатий
        val onEditTextClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.editTextInitialDateIncome -> {
                    DatePickerDialog(
                        this@ListIncomeActivity, dateListenerInitialDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.editTextFinalDateIncome -> {
                    DatePickerDialog(
                        this@ListIncomeActivity, dateListenerFinalDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }
        }
        //Назначение слушателя для текстовых полей
        editTextInitialDateIncome.setOnClickListener(onEditTextClickListener)
        editTextFinalDateIncome.setOnClickListener(onEditTextClickListener)

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
        val adapterCurrency = ArrayAdapter(this@ListIncomeActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        spinnerCurrencies.adapter = adapterCurrency


        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        // Установка текущей даты
        setDateTime(calendar.timeInMillis, editTextFinalDateIncome, false, isSetRecyclerViewAdapter = false)
        // Установка в календарь первого дня текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Установка первого числа текущего месяца
        setDateTime(calendar.timeInMillis, editTextInitialDateIncome, true, isSetRecyclerViewAdapter = false)

        createCurrencyAdapter()
        createAccountAdapter()

        myDbManager.closeDatabase()
    }

    override fun onIncomeClick(myIncome: MyIncome) {
        val intent = Intent(this@ListIncomeActivity, IncomeEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_INCOME, myIncome)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    // Обработчик события для выбора начальной даты
    private var dateListenerInitialDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, editTextInitialDateIncome, true)
    }

    // Обработчик события для выбора конечной даты
    private var dateListenerFinalDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        dateAndTime[Calendar.YEAR] = year
        dateAndTime[Calendar.MONTH] = monthOfYear
        dateAndTime[Calendar.DAY_OF_MONTH] = dayOfMonth
        setDateTime(dateAndTime.timeInMillis, editTextFinalDateIncome, false)
    }

    private fun setDateTime(milliseconds : Long = 0, editText : EditText, isInitial : Boolean, isSetRecyclerViewAdapter : Boolean = true) {
        editText.setText(
            DateUtils.formatDateTime(
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

        if (isSetRecyclerViewAdapter) setRecycleViewAdapter()
    }

    // Создание адаптера для списка операций
    private fun setRecycleViewAdapter(){
        val account : MyAccount = spinnerAccounts.selectedItem as MyAccount
        //Если выбран элемент с id ноль (т.е все счета)), то вывести данные из всех счетов, иначе из выбранного
        val list : ArrayList<MyIncome> = arrayListOf()
        if (account._id  == (0).toLong()){
            val selectedCurrency = spinnerCurrencies.selectedItem as MyCurrency
            val listAccounts = myDbManager.fromAccountsByCurrency(selectedCurrency._id)
            for (myAccount in listAccounts){
                list.addAll(myDbManager.fromIncome(initialDate, finalDate, myAccount._id))
            }
        }
        else{
            list.addAll(myDbManager.fromIncome(initialDate, finalDate, account._id))
        }
        adapter.addAllIncomeList(list)
    }
    // Создание адаптера списка валют
    private fun createCurrencyAdapter(){
        val adapterCurrency = ArrayAdapter(this@ListIncomeActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        spinnerCurrencies.adapter = adapterCurrency
    }
    // Создание адаптера для списка счетов
    private fun createAccountAdapter(){
        val selectedCurrency = spinnerCurrencies.selectedItem as MyCurrency
        val accountList: ArrayList<MyAccount> = arrayListOf(MyAccount(0, this.resources.getString(R.string.allAccounts), 0, selectedCurrency._id))
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