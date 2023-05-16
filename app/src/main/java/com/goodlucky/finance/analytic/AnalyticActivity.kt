package com.goodlucky.finance.analytic

import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.MySpinnerImageWithTextArrayAdapter
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.ActivityAnalyticBinding
import com.goodlucky.finance.items.MyCategory
import com.goodlucky.finance.items.MyLimit
import com.kal.rackmonthpicker.RackMonthPicker
import java.text.DecimalFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class AnalyticActivity : AppCompatActivity(), LimitAdapter.Listener {

    private lateinit var initialDate : String
    private lateinit var finalDate : String

    private lateinit var selectedLimit : MyLimit

    private lateinit var calendar: Calendar

    private lateinit var myDbManager: MyDbManager

    private lateinit var binding : ActivityAnalyticBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myDbManager = MyDbManager(this)

        calendar= Calendar.getInstance()

        // Диалог выбора года и месяца
        val locale = resources.configuration.locales.get(0) // is now the preferred accessor.
        val rackMonthPicker = RackMonthPicker(this)
            .setPositiveButton { month, fisthDay, lastDay, year, monthLabel ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, lastDay)
                setDate(calendar)

                createLimitRecyclerViewAdapter()
            }
            .setNegativeButton { dialog ->
                dialog.hide()
            }
            .setLocale(locale)
            .setColorTheme(com.kal.rackmonthpicker.R.color.color_primary)

        binding.analyticDate.setOnClickListener {
            rackMonthPicker.show()
        }

        binding.analyticButtonAdd.setOnClickListener {
            try {
                // Попробовать преобразовать строку в число
                binding.analyticLimitSum.text.toString().toDouble()
            }
            catch (exc : java.lang.Exception){
                Toast.makeText(this, R.string.costSumWrongSum, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = binding.spinnerLimit.selectedItemId.toByte()
            val sum = binding.analyticLimitSum.text.toString().toDouble()
            val category = binding.analyticCategory.selectedItem as MyCategory

            myDbManager.insertToLimits(MyLimit(0, type, sum, category._id))

            createLimitRecyclerViewAdapter()
            binding.analyticButtonAdd.isEnabled = false
            binding.analyticButtonChange.isEnabled = true
        }

        binding.analyticButtonChange.setOnClickListener {
            try {
                // Попробовать преобразовать строку в число
                binding.analyticLimitSum.text.toString().toDouble()
            }
            catch (exc : java.lang.Exception){
                Toast.makeText(this, R.string.costSumWrongSum, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = binding.spinnerLimit.selectedItemId.toByte()
            val sum = binding.analyticLimitSum.text.toString().toDouble()
            val category = binding.analyticCategory.selectedItem as MyCategory

            val limit = MyLimit(selectedLimit._id, type, sum, category._id)

            myDbManager.updateInLimits(limit)

            createLimitRecyclerViewAdapter()
        }

        val itemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = binding.analyticCategory.selectedItem as MyCategory
                val type = binding.spinnerLimit.selectedItemId.toByte()


                selectedLimit = myDbManager.findLimit(category._id, type)

                if (selectedLimit._id != (0).toLong()){
                    val decimalFormat = DecimalFormat("##0.00")
                    val strSum = decimalFormat.format(selectedLimit._sum)
                    binding.analyticLimitSum.setText(strSum)

                    binding.analyticButtonAdd.isEnabled = false
                    binding.analyticButtonChange.isEnabled = true

                }else{
                    binding.analyticLimitSum.setText("")

                    binding.analyticButtonAdd.isEnabled = true
                    binding.analyticButtonChange.isEnabled = false
                }


            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.analyticCategory.onItemSelectedListener = itemSelectedListener
        binding.spinnerLimit.onItemSelectedListener = itemSelectedListener
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        calendar.timeInMillis = Date().time.milliseconds.inWholeMilliseconds
        val maxDayInMonth : Int = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, maxDayInMonth)
        setDate(calendar)

        createLimitAdapter()
        createCategoryAdapter()
        createLimitRecyclerViewAdapter()
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setDate(calendar: Calendar){
        binding.analyticDate.setText(
            DateUtils.formatDateTime(this, calendar.timeInMillis,
            DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR))

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

    private fun createLimitAdapter(){
        val listLimits = resources.getStringArray(R.array.array_limits)
        val adapterLimits = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listLimits)
        binding.spinnerLimit.adapter = adapterLimits
    }

    private fun createCategoryAdapter(){
        val listCategory = myDbManager.fromCategories(MyConstants.CATEGORY_TYPE_COST)
        val adapterCategory = MySpinnerImageWithTextArrayAdapter(this, listCategory)
        binding.analyticCategory.adapter = adapterCategory
    }

    private fun createLimitRecyclerViewAdapter(){
        val limitAdapter = LimitAdapter(this, initialDate, finalDate)
        binding.analyticListLimits.layoutManager = LinearLayoutManager(this)
        binding.analyticListLimits.adapter = limitAdapter

        val listLimit = myDbManager.fromLimits()
        limitAdapter.addAllItemList(listLimit)
    }

    override fun onItemClick(myLimit: MyLimit) {
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    myDbManager.deleteFromLimits(myLimit._id)
                    createLimitRecyclerViewAdapter()

                    // Обновить поля связанные с выбранным ограничением
                    val category = binding.analyticCategory.selectedItem as MyCategory
                    val type = binding.spinnerLimit.selectedItemId.toByte()
                    selectedLimit = myDbManager.findLimit(category._id, type)

                    if (selectedLimit._id != (0).toLong()){
                        val decimalFormat = DecimalFormat("##0.00")
                        val strSum = decimalFormat.format(selectedLimit._sum)
                        binding.analyticLimitSum.setText(strSum)

                        binding.analyticButtonAdd.isEnabled = false
                        binding.analyticButtonChange.isEnabled = true

                    }else{
                        binding.analyticLimitSum.setText("")

                        binding.analyticButtonAdd.isEnabled = true
                        binding.analyticButtonChange.isEnabled = false
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {}
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.doYouWantToDeleteThatLimit)
            .setPositiveButton(R.string.yes, dialogClickListener)
            .setNegativeButton(R.string.no, dialogClickListener)
            .show()
    }
}