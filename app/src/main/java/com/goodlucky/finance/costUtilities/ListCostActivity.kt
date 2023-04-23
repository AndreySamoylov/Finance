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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        myDbManager = MyDbManager(this)


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
                        this@ListCostActivity, dateListenerInitialDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                R.id.editTextFinalDateCost ->{
                    DatePickerDialog(
                        this@ListCostActivity, dateListenerFinalDate,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)
                    ).show()
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

        // Инициализация календаря
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds

        // Установка текущей даты
        setDateTime(calendar.timeInMillis, editTextFinalDateCost, false, isSetRecyclerViewAdapter = false)
        // Установка в календарь первого дня текущего месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Установка первого числа текущего месяца
        setDateTime(calendar.timeInMillis, editTextInitialDateCost, true, isSetRecyclerViewAdapter = false)

        createCurrencyAdapter()
        createAccountAdapter()

        myDbManager.closeDatabase()
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

    override fun onCostClick(myCost: MyCost) {
        val intent = Intent(this@ListCostActivity, CostEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_COST, myCost)
        startActivity(intent)
    }

    // Обработчик события для выбора начальной даты даты
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
    // Создание адаптера списка валют
    private fun createCurrencyAdapter(){
        val adapterCurrency = ArrayAdapter(this@ListCostActivity, android.R.layout.simple_spinner_dropdown_item, myDbManager.fromCurrencies())
        spinnerCurrencies.adapter = adapterCurrency
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