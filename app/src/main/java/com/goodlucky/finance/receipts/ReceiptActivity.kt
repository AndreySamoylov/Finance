package com.goodlucky.finance.receipts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityReceiptBinding
import com.goodlucky.finance.items.MyReceipt
import com.goodlucky.finance.receipts.jsonObjects.Barcode
import com.goodlucky.finance.receipts.jsonObjects.BodyGetTicketId
import com.goodlucky.finance.receipts.jsonObjects.BodySendPhoneNumber
import com.goodlucky.finance.receipts.jsonObjects.BodyVerifyPhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReceiptActivity : AppCompatActivity() {
    private lateinit var myDbManager: MyDbManager

    private lateinit var nalogRuApi : NalogRuApi
    private lateinit var session : com.goodlucky.finance.receipts.jsonObjects.Session
    private lateinit var ticketIdentify : com.goodlucky.finance.receipts.jsonObjects.TicketIdentify
    private lateinit var barcode: Barcode



    private lateinit var binding : ActivityReceiptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NalogRuApiConstants.baseURL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        nalogRuApi = retrofit.create(NalogRuApi::class.java)


        binding.receiptsButtonSendPhoneNumber.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val bodySendPhoneNumber = BodySendPhoneNumber(
                    binding.receiptsPhoneNumber.text.toString(),
                    NalogRuApiConstants.CLIENT_SECRET, NalogRuApiConstants.OS)
                nalogRuApi.sendPhoneNumber(bodySendPhoneNumber)
                runOnUiThread {
                    Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.cheakYourPhone, Toast.LENGTH_SHORT).show()
                    binding.receiptsImagePhoneNumberStatus.setImageResource(com.goodlucky.finance.R.drawable.green_tick_circle)
                }
            }
        }

        binding.receiptsButtonSendVerifyCode.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val bodyVerifyPhoneNumber = BodyVerifyPhoneNumber(
                    binding.receiptsPhoneNumber.text.toString(),
                    binding.receiptsVerifyCode.text.toString(),
                    NalogRuApiConstants.CLIENT_SECRET,
                    NalogRuApiConstants.OS)
                session = nalogRuApi.verifyPhoneNumber(bodyVerifyPhoneNumber)
                runOnUiThread {
                    Toast.makeText(this@ReceiptActivity, com.goodlucky.finance.R.string.verifyCodeSended, Toast.LENGTH_SHORT).show()
                    binding.receiptsImageVerifyCodeStatus.setImageResource(com.goodlucky.finance.R.drawable.green_tick_circle)
                }
            }
        }

        binding.receiptsButtonGetBarcodeInformation.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                val bodyGetTicketId = BodyGetTicketId(binding.receiptsBarcode.text.toString())
                ticketIdentify = nalogRuApi.getTickerId(session.sessionId, bodyGetTicketId)

                barcode = nalogRuApi.getTicket(session.sessionId, ticketIdentify.id)
                runOnUiThread {
                    binding.receiptsTextViewResult.text = barcode.toString()
                }
            }
        }

        binding.receiptsSpinnerReceipt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val receipt : MyReceipt= binding.receiptsSpinnerReceipt.selectedItem as MyReceipt
                binding.receiptsBarcode.setText(receipt.code)
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
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }
}