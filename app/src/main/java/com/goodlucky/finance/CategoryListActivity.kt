package com.goodlucky.finance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.costUtilities.MyCostAdapter
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityCategoryListBinding
import com.goodlucky.finance.incomeUtilities.MyIncomeAdapter
import com.goodlucky.finance.items.MyCost
import com.goodlucky.finance.items.MyIncome
import java.text.DecimalFormat


class CategoryListActivity : AppCompatActivity(), MyCostAdapter.Listener, MyIncomeAdapter.Listener {

    private val adapterCost = MyCostAdapter(this, this)
    private val adapterIncome = MyIncomeAdapter   (this, this)

    private lateinit var state : String
    private lateinit var initialDate : String
    private lateinit var finalDate : String
    private var idAccount : Long = -1
    private var idCurrency : Long = -1
    private var value : Double = -1.0
    private lateinit var nameCategory : String

    private lateinit var myDbManager: MyDbManager

    private lateinit var binding : ActivityCategoryListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        state = intent.getStringExtra(MyConstants.STATE).toString()
        initialDate = intent.getStringExtra(MyConstants.INITIAL_DATE).toString()
        finalDate = intent.getStringExtra(MyConstants.FINAL_DATE).toString()
        idAccount = intent.getLongExtra(MyConstants.ID_ACCOUNT, -1)
        idCurrency = intent.getLongExtra(MyConstants.ID_CURRENCY, -1)
        value = intent.getDoubleExtra(MyConstants.VALUE, -1.0)
        nameCategory = intent.getStringExtra(MyConstants.NAME_CATEGORY).toString()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        setResultText()
        if (state == MyConstants.COST) createCategoryAdapterCost()
        else if (state == MyConstants.INCOME) createCategoryAdapterIncome()
    }

    override fun onPause() {
        super.onPause()
        myDbManager .closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){android.R.id.home -> finish()}
        return super.onOptionsItemSelected(item)
    }

    private fun setResultText(){
        val decimalFormat = DecimalFormat("##0.00")
        val strValue: String = decimalFormat.format(value)
        val text = "${this.resources.getString(R.string.category)}: $nameCategory \n" +
                "${this.resources.getString(R.string.result)}: $strValue"

        binding.CategoryListActivityTextResult.text = text
    }

    private fun createCategoryAdapterCost(){
        binding.CategoryListActiviryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.CategoryListActiviryRecycleView.adapter = adapterCost

        val idCategory = myDbManager.findCategoryByName(nameCategory)

        val listCost = ArrayList<MyCost>()

        //  Если передан НЕ параметр "Все счета"
        if (idAccount > (0).toLong()){
            val list = myDbManager.fromCosts(initialDate, finalDate, idAccount, idCategory)
            listCost.addAll(list)
        }else{
            val listAccount = myDbManager.fromAccountsByCurrency(idCurrency)
            for (account in listAccount){
                val list = myDbManager.fromCosts(initialDate, finalDate, account._id, idCategory)
                listCost.addAll(list)
            }
        }

        adapterCost.addAllCostList(listCost)
    }

    private fun createCategoryAdapterIncome(){
        binding.CategoryListActiviryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.CategoryListActiviryRecycleView.adapter = adapterIncome

        val idCategory = myDbManager.findCategoryByName(nameCategory)

        val listIncome = ArrayList<MyIncome>()

        //  Если передан НЕ параметр "Все счета"
        if (idAccount > (0).toLong()){
            val list = myDbManager.fromIncome(initialDate, finalDate, idAccount, idCategory)
            listIncome.addAll(list)
        }else{
            val listAccount = myDbManager.fromAccountsByCurrency(idCurrency)
            for (account in listAccount){
                val list = myDbManager.fromIncome(initialDate, finalDate, account._id, idCategory)
                listIncome.addAll(list)
            }
        }

        adapterIncome.addAllIncomeList(listIncome)
    }

    override fun onCostClick(myCost: MyCost) {

    }

    override fun onIncomeClick(myIncome: MyIncome) {
        TODO("Not yet implemented")
    }
}