package com.goodlucky.finance.analytic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.LimitItemBinding
import com.goodlucky.finance.items.MyLimit
import java.text.DecimalFormat


class LimitAdapter(val listener: Listener, val initialDate : String, val finalDate : String) : RecyclerView.Adapter<LimitAdapter.MyLimitHolder>() {

    private val limitList = ArrayList<MyLimit>()

    class  MyLimitHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = LimitItemBinding.bind(item)
        private val myDbManager = MyDbManager(item.context)
        private val context = item.context

        fun bind(limit: MyLimit, listener : Listener, initialDate: String, finalDate: String) = with(binding){
            myDbManager.openDatabase()
            // Название категории
            val strCategory = myDbManager.findCategoryByID(limit._id_category)
            // Название валюты
            val strCurrency = myDbManager.getCurrencyName(limit._id_currency)
            // Сумма по категории
            val listAccount = myDbManager.fromAccountsByCurrency(limit._id_currency)
            var totallySumByCategory = 0.0
            for (account in listAccount){
                val sum = myDbManager.getSumCostByCategory(limit._id_category, initialDate, finalDate, account._id)
                totallySumByCategory += sum

            }
            myDbManager.closeDatabase()

            var strType = "" // Текстовое обозначение типа
            var limitText = "" // Выполняется ли ограничение?
            when(limit._type){
                MyConstants.LIMIT_NO_MORE -> {
                    strType = context.resources.getString(R.string.noMore)
                    limitText = if (limit._sum >= totallySumByCategory) context.resources.getString(R.string.limitIsFine)
                        else context.resources.getString(R.string.limitIsBreak)
                }
                MyConstants.LIMIT_NO_LESS ->{
                    strType = context.resources.getString(R.string.noLess)
                    limitText = if (limit._sum <= totallySumByCategory) context.resources.getString(R.string.limitIsFine)
                    else context.resources.getString(R.string.limitIsBreak)
                }
                else -> {
                    strType = context.resources.getString(R.string.error)
                    limitText = context.resources.getString(R.string.error)
                }
            }

            val decimalFormat = DecimalFormat("##0.00")
            val strSum = decimalFormat.format(limit._sum)
            val strTotalSum = decimalFormat.format(totallySumByCategory)


            val strLimit = "${context.resources.getString(R.string.category)}: ${strCategory}\n" +
                    "${context.resources.getString(R.string.limit)}: $strType\n" +
                    "${context.resources.getString(R.string.sum)}: $strSum\n" +
                    "$limitText \n" +
                    "${context.resources.getString(R.string.currency)}: $strCurrency\n" +
                    "${context.resources.getString(R.string.currentSum)}: $strTotalSum \n\n"

            limitItemTextView.text = strLimit

            itemView.setOnClickListener{
                listener.onItemClick(limit)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyLimitHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.limit_item, parent, false)
        return MyLimitHolder(view)
    }

    override fun getItemCount(): Int {
        return limitList.size
    }

    override fun onBindViewHolder(holder: MyLimitHolder, position: Int) {
        holder.bind(limitList[position], listener, initialDate, finalDate)
    }

    fun addItem(limit: MyLimit){
        limitList.add(limit)
        notifyDataSetChanged()
    }

    fun addAllItemList(list: List<MyLimit>){
        limitList.clear()
        limitList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        fun onItemClick(myLimit: MyLimit)
    }
}
