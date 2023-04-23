package com.goodlucky.finance.receipts

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityReceiptBinding
import com.goodlucky.finance.items.MyReceipt
import com.goodlucky.finance.receipts.jsonObjects.*
import com.goodlucky.finance.receipts.recicleViewAdapter.DoubleTextAdapter
import com.goodlucky.finance.receipts.recicleViewAdapter.DoubleTextItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList


class ReceiptActivity : AppCompatActivity() {

    private lateinit var nalogRuApi : NalogRuApi
    private lateinit var nalogRu: NalogRu
    private lateinit var barcode: Barcode

    private lateinit var doubleTextAdapter : DoubleTextAdapter
    private lateinit var myDbManager: MyDbManager
    private lateinit var binding : ActivityReceiptBinding

    private var isPhoneNumberSend = false
    private var isPhoneNumberVerify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        doubleTextAdapter = DoubleTextAdapter()
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
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.cheakYourPhone, Toast.LENGTH_SHORT).show()
                binding.receiptsImagePhoneNumberStatus.setImageResource(com.goodlucky.finance.R.drawable.green_tick_circle)
                isPhoneNumberSend = true
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptsButtonSendVerifyCode.setOnClickListener {
            if (!isPhoneNumberSend){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.phoneNumberHaveNotBeenSend, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.receiptsVerifyCode.text.toString().isEmpty()){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.enterVerifyCode, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                nalogRu.verifyPhoneNumber(binding.receiptsPhoneNumber.text.toString(), binding.receiptsVerifyCode.text.toString())
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.verifyCodeSended, Toast.LENGTH_SHORT).show()
                binding.receiptsImageVerifyCodeStatus.setImageResource(com.goodlucky.finance.R.drawable.green_tick_circle)
                isPhoneNumberVerify = true
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptsButtonGetBarcodeInformation.setOnClickListener {
            if (!isPhoneNumberVerify) {
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.youHaveNotBeenVerify, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.receiptsBarcode.text.toString().isEmpty()){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.barcodeIsEmpty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                barcode = nalogRu.returnBarcode(binding.receiptsBarcode.text.toString())
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try{
                var items = ""
                for (item in barcode.ticket.document.receipt.items){
                    items += "${resources.getString(com.goodlucky.finance.R.string.name)} ${item.name} \n"
                    val strValue = item.sum.toInt().toString() // Нужно чтобы убрать копейки из рублей
                    val value = strValue.substring(0, strValue.length - 2) + "." + strValue.substring(strValue.length - 2, strValue.length)
                    items += "${resources.getString(com.goodlucky.finance.R.string.value)} $value \n"
                    items += "${resources.getString(com.goodlucky.finance.R.string.quantity)} ${item.quantity} \n"
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
                listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.date), selectedReceipt.date)) // Дата
                listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.items), items)) // Что купили?
                listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.place), barcode.ticket.document.receipt.retailPlace)) // Название места
                listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.address), barcode.ticket.document.receipt.retailPlaceAddress)) // Адрес места
                // Загрузить в RecycleView
                doubleTextAdapter.submitList(listDoubleTextItem)
            }catch (exc : java.lang.Exception){
                Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.errorRead, Toast.LENGTH_SHORT).show()
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
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.date), selectedReceipt.date))  // Дата
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.items), selectedReceipt.items))  // Что купили?
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.place), selectedReceipt.retailPlace))  // Название места
                    listDoubleTextItem.add(DoubleTextItem(resources.getString(com.goodlucky.finance.R.string.address), selectedReceipt.retailPlaceAddress))  // Адрес места
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

        val receiptAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, myDbManager.fromReceipts())
        binding.receiptsSpinnerReceipt.adapter = receiptAdapter

        setPreferences()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setPreferences() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        // Установить номер телефона
        val phoneNumber = sharedPref.getString(resources.getString(com.goodlucky.finance.R.string.phoneNumber), "")
        binding.receiptsPhoneNumber.setText(phoneNumber)
    }
}