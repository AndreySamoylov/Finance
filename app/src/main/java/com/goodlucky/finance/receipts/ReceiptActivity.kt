package com.goodlucky.finance.receipts

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityReceiptBinding
import com.goodlucky.finance.items.MyReceipt
import com.goodlucky.finance.receipts.jsonObjects.*
import com.goodlucky.finance.receipts.recicleViewAdapter.DoubleTextAdapter
import com.goodlucky.finance.receipts.recicleViewAdapter.DoubleTextItem
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.kal.rackmonthpicker.RackMonthPicker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.milliseconds


class ReceiptActivity : AppCompatActivity(), DoubleTextAdapter.Listener {

    private lateinit var calendar: Calendar

    private lateinit var initialDate : String
    private lateinit var finalDate : String

    private lateinit var rackMonthPicker : RackMonthPicker

    private lateinit var nalogRuApi : NalogRuApi
    private lateinit var nalogRu: NalogRu
    private lateinit var barcode: Barcode

    private lateinit var doubleTextAdapter : DoubleTextAdapter
    private lateinit var myDbManager: MyDbManager
    private lateinit var binding : ActivityReceiptBinding

    private var isPhoneNumberSend = false
    private var isPhoneNumberVerify = false
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        calendar = Calendar.getInstance()

        // Диалог выбора года и месяца
        val locale = resources.configuration.locales.get(0) // is now the preferred accessor.
        rackMonthPicker = RackMonthPicker(this)
            .setPositiveButton { month, fisthDay, lastDay, year, monthLabel ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, lastDay)
                setDate(calendar)

                createReceiptAdapter()
            }
            .setNegativeButton { dialog ->
                dialog.hide()
            }
            .setLocale(locale)
            .setColorTheme(com.kal.rackmonthpicker.R.color.color_primary)


        doubleTextAdapter = DoubleTextAdapter(this)
        binding.receiptsRecycleView.layoutManager = LinearLayoutManager(this)
        binding.receiptsRecycleView.adapter = doubleTextAdapter

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NalogRuApiConstants.baseURL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        nalogRuApi = retrofit.create(NalogRuApi::class.java)

        nalogRu = NalogRu(nalogRuApi)


        binding.receiptsButtonSendPhoneNumber.setOnClickListener {
            try{
                nalogRu.sendPhoneNumber(binding.receiptsPhoneNumber.text.toString())
                Toast.makeText(this@ReceiptActivity, R.string.cheakYourPhone, Toast.LENGTH_SHORT).show()
                binding.receiptsImagePhoneNumberStatus.setImageResource(R.drawable.green_tick_circle)
                isPhoneNumberSend = true
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptsButtonSendVerifyCode.setOnClickListener {
            if (!isPhoneNumberSend){
                Toast.makeText(this@ReceiptActivity, R.string.phoneNumberHaveNotBeenSend, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.receiptsVerifyCode.text.toString().isEmpty()){
                Toast.makeText(this@ReceiptActivity, R.string.enterVerifyCode, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                nalogRu.verifyPhoneNumber(binding.receiptsPhoneNumber.text.toString(), binding.receiptsVerifyCode.text.toString())
                Toast.makeText(this@ReceiptActivity, R.string.verifyCodeSended, Toast.LENGTH_SHORT).show()
                binding.receiptsImageVerifyCodeStatus.setImageResource(R.drawable.green_tick_circle)
                isPhoneNumberVerify = true
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptsButtonGetBarcodeInformation.setOnClickListener {
            if (!isPhoneNumberVerify) {
                Toast.makeText(this@ReceiptActivity, R.string.youHaveNotBeenVerify, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.receiptsBarcode.text.toString().isEmpty()){
                Toast.makeText(this@ReceiptActivity, R.string.barcodeIsEmpty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                barcode = nalogRu.returnBarcode(binding.receiptsBarcode.text.toString())
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, R.string.error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                var items = ""
                for (item in barcode.ticket.document.receipt.items){
                    items += "${resources.getString(R.string.name)} ${item.name} \n"
                    val strValue = item.sum.toInt().toString() // Нужно чтобы убрать копейки из рублей
                    val value = strValue.substring(0, strValue.length - 2) + "." + strValue.substring(strValue.length - 2, strValue.length)
                    items += "${resources.getString(R.string.value)} $value \n"
                    items += "${resources.getString(R.string.quantity)} ${item.quantity} \n"
                    items += ""
                }

                // Обновить запись чека в базе данных
                val selectedReceipt = binding.receiptsSpinnerReceipt.selectedItem as MyReceipt
                // Обновление записи в базе данные, т.к. чек проверен
                val myReceipt = MyReceipt(selectedReceipt._id, selectedReceipt.code, selectedReceipt.date,
                    selectedReceipt.sum, items, barcode.ticket.document.receipt.retailPlace,
                    barcode.ticket.document.receipt.retailPlaceAddress)
                myDbManager.updateInReceipts(myReceipt)

                // *** Заполнение списка ***
                val listDoubleTextItem = ArrayList<DoubleTextItem>()
                listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.date), selectedReceipt.date)) // Дата
                listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.items), items)) // Что купили?
                listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.place), barcode.ticket.document.receipt.retailPlace)) // Название места
                listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.address), barcode.ticket.document.receipt.retailPlaceAddress)) // Адрес места
                // Загрузить в RecycleView
                doubleTextAdapter.submitList(listDoubleTextItem)
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, R.string.errorRead, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptsSpinnerReceipt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val receipt : MyReceipt= binding.receiptsSpinnerReceipt.selectedItem as MyReceipt
                binding.receiptsBarcode.setText(receipt.code)

                // Если чек уже проверен вывести информацию о нём в списке
                val listDoubleTextItem = ArrayList<DoubleTextItem>()
                val selectedReceipt = binding.receiptsSpinnerReceipt.selectedItem as MyReceipt
                if (selectedReceipt.retailPlaceAddress != ""){
                    // ********* Заполнение списка *********
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.date), selectedReceipt.date))  // Дата
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.items), selectedReceipt.items))  // Что купили?
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.place), selectedReceipt.retailPlace))  // Название места
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(R.string.address), selectedReceipt.retailPlaceAddress))  // Адрес места
                    // Загрузить в RecycleView
                    doubleTextAdapter.submitList(listDoubleTextItem)
                }else{
                    listDoubleTextItem.clear()
                    doubleTextAdapter.submitList(listDoubleTextItem)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        val maxDayInMonth : Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, maxDayInMonth)
        setDate(calendar)

        createReceiptAdapter()

        setPreferences()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.receipt_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
            R.id.receiptToolbarMenuScanner -> barcodeLauncher.launch(ScanOptions())
            R.id.receiptToolbarMenuDatePicker -> rackMonthPicker.show()
            R.id.receiptToolbarMenuDeleteReceipt -> {
                val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> deleteReceipt()
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.doYouWantDeleteReceipt  )
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDate(calendar: Calendar){
//        binding.analyticDate.setText(
//            DateUtils.formatDateTime(this, calendar.timeInMillis,
//                DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Приведение начальной и конечной даты в нужный формта
        initialDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-01"
            else "${year}-0${month}-01"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-01"
            else "${year}-${month}-01"
        }

        finalDate = if (month < 10) {
            if (dayOfMonth < 10) "${year}-0${month}-0${dayOfMonth}"
            else "${year}-0${month}-${dayOfMonth}"
        } else {
            if (dayOfMonth < 10) "${year}-${month}-0${dayOfMonth}"
            else "${year}-${month}-${dayOfMonth}"
        }
    }

    private fun createReceiptAdapter(){
        val listReceipt = myDbManager.fromReceipts(initialDate, finalDate) // Получение списка

        val receiptAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listReceipt)
        binding.receiptsSpinnerReceipt.adapter = receiptAdapter
    }

    private fun setPreferences() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        // Установить номер телефона
        val phoneNumber = sharedPref.getString(resources.getString(com.goodlucky.finance.R.string.phoneNumber), "")
        binding.receiptsPhoneNumber.setText(phoneNumber)
    }

    override fun onItemClick(doubleTextItem: DoubleTextItem) {

    }

    // Сканер
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText( this@ReceiptActivity, R.string.canceled, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                this@ReceiptActivity,
                R.string.scanned,
                Toast.LENGTH_LONG
            ).show()

            //Обработка строки
            //Получение даты
            val year = result.contents.substring(2, 6)
            val month = result.contents.substring(6, 8)
            val dayOfMonth = result.contents.substring(8, 10)
            val hour = result.contents.substring(11, 13)
            val minute = result.contents.substring(13, 15)

            // Получение суммы
            var sIndex = 0 // Индекс буквы 's'
            var pointIndex = 0 //Индекс точки '.'
            for (i in 0 until result.contents.length){
                if (result.contents.elementAt(i) == 's') {
                    sIndex = i
                }
                if (result.contents.elementAt(i) == '.'){
                    pointIndex = i
                }
            }

            val sum = result.contents.substring(sIndex + 2, pointIndex + 3)

            //Добавление чека в базу данных
            val date = "${year}-${month}-${dayOfMonth} ${hour}:${minute}"
            val receipt = MyReceipt(0, result.contents, date, sum.toDouble(),  "", "", "")
            myDbManager.insertToReceipt(receipt)

            // Обновление списка чеков
            createReceiptAdapter()
        }
    }

    //  Метод удаляет выбранный в списке чек
    private fun deleteReceipt(){
        var selectedReceipt = MyReceipt()
        try {
            selectedReceipt = binding.receiptsSpinnerReceipt.selectedItem as MyReceipt
        } catch (exception : java.lang.NullPointerException){
            Toast.makeText(this@ReceiptActivity, R.string.error, Toast.LENGTH_SHORT).show()
            Log.d("Receipt Activity", exception.toString())
        }
        myDbManager.deleteFromReceipts(selectedReceipt._id)
        createReceiptAdapter()
    }
}