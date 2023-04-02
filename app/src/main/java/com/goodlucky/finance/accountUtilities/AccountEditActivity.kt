package com.goodlucky.finance.accountUtilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.goodlucky.finance.MyFunction.getSerializable
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityAccountEditBinding
import com.goodlucky.finance.items.MyAccount
import com.goodlucky.finance.items.MyBank
import com.goodlucky.finance.items.MyCurrency

class AccountEditActivity : AppCompatActivity() {

    private lateinit var myDbManager: MyDbManager

    private lateinit var state : String // Состояние. Для добавления, или изменения и удаления
    private lateinit var receivedAccount : MyAccount // Полученный счёт из другой активити

    private lateinit var binding : ActivityAccountEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        this.setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        val editTextAccountName = findViewById<EditText>(R.id.editTextAccountName)
        val buttonOkAccount = findViewById<Button>(R.id.buttonOkAccount)
        val buttonCancelAccount = findViewById<Button>(R.id.buttonCancelAccount)

        // Получить состояние
        state = intent.getStringExtra(MyConstants.STATE).toString()
        // В зависимости от того какое состояние, выполнить соответствующее действие
        if(state == MyConstants.STATE_ADD){
            receivedAccount = MyAccount()
            buttonOkAccount.text = resources.getText(R.string.add)
            buttonCancelAccount.text = resources.getText(R.string.cancel)
        }
        else if(state == MyConstants.STATE_CHANGE_OR_DELETE){
            receivedAccount = getSerializable(this, MyConstants.KEY_MY_ACCOUNT, MyAccount::class.java)
            editTextAccountName.setText(receivedAccount.name)
            buttonOkAccount.text = resources.getText(R.string.change)
            buttonCancelAccount.text = resources.getText(R.string.remove)
        }

        //Нажатие на кнопку ОК, в зависимости от состояние - state, либо добавляет, либо изменять данные
        buttonOkAccount.setOnClickListener {
            if (editTextAccountName.text.toString().length >= 3) {
                if (state == MyConstants.STATE_ADD) {
                    val selectedBank = binding.AccountEditActivitySpinnerBank.selectedItem as MyBank
                    val selectedCurrency =
                        binding.AccountEditActivitySpinnerCurrency.selectedItem as MyCurrency
                    val account = MyAccount(
                        0,
                        editTextAccountName.text.toString(),
                        selectedBank._id,
                        selectedCurrency._id
                    )
                    myDbManager.insertToAccounts(account)
                } else if (state == MyConstants.STATE_CHANGE_OR_DELETE) {
                    val selectedBank = binding.AccountEditActivitySpinnerBank.selectedItem as MyBank
                    val selectedCurrency =
                        binding.AccountEditActivitySpinnerCurrency.selectedItem as MyCurrency
                    val account = MyAccount(
                        receivedAccount._id,
                        editTextAccountName.text.toString(),
                        selectedBank._id,
                        selectedCurrency._id
                    )
                    myDbManager.updateInAccounts(account)
                }
                finish()
            }
            else{
                Toast.makeText(this@AccountEditActivity, R.string.accountTextFewLenght, Toast.LENGTH_SHORT).show()
            }
        }

        // Нажатие на кнопку отмена, в зависимости от состояние - state, либо закрывает страницу,
        // либо удаляет данные
        buttonCancelAccount.setOnClickListener {
            if (state == MyConstants.STATE_CHANGE_OR_DELETE){
                myDbManager.deleteFromAccounts(receivedAccount._id)
            }
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        //Создание адаптеров
        val listBank = myDbManager.fromBanks()
        val spinnerBanksAdapter = ArrayAdapter(this, androidx.transition.R.layout.support_simple_spinner_dropdown_item, listBank)
        binding.AccountEditActivitySpinnerBank.adapter = spinnerBanksAdapter

        val listCurrency = myDbManager.fromCurrencies()
        val spinnerCurrencyAdapter = ArrayAdapter(this, androidx.transition.R.layout.support_simple_spinner_dropdown_item, listCurrency)
        binding.AccountEditActivitySpinnerCurrency.adapter = spinnerCurrencyAdapter
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }
}
