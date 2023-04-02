package com.goodlucky.finance.accountUtilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.AccountItemBinding
import com.goodlucky.finance.items.MyAccount

class MyAccountAdapter(val listener: Listener, val context: Context) : RecyclerView.Adapter<MyAccountAdapter.MyAccountHolder>() {

    val accountList = ArrayList<MyAccount>()

    class  MyAccountHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = AccountItemBinding.bind(item)
        private val myDbManager = MyDbManager(item.context)

        fun bind(myAccount: MyAccount, listener : Listener) = with(binding){
            myDbManager.openDatabase()
            val bankName = myDbManager.getBankName(myAccount.idBank)
            val currencyName = myDbManager.getCurrencyName(myAccount.idCurrency)
            myDbManager.closeDatabase()
            val accountText = "${myAccount.name} ($currencyName) $bankName"
            textViewItemAccountName.text = accountText
            itemView.setOnClickListener{
                listener.onAccountClick(myAccount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        return MyAccountHolder(view)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    override fun onBindViewHolder(holder: MyAccountHolder, position: Int) {
        holder.bind(accountList[position], listener)
    }

    fun addAccount(myAccount: MyAccount){
        accountList.add(myAccount)
        notifyDataSetChanged()
    }

    fun addAllAccountList(list: List<MyAccount>){
        accountList.clear()
        accountList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onAccountClick(myAccount: MyAccount)
    }
}