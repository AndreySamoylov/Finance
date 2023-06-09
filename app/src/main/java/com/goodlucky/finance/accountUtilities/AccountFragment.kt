package com.goodlucky.finance.accountUtilities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.items.MyAccount

class AccountFragment : Fragment(), MyAccountAdapter.Listener {

    private lateinit var adapter : MyAccountAdapter
    private lateinit var myDbManager : MyDbManager
    private lateinit var currentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        adapter = MyAccountAdapter(this, inflater.context)
        myDbManager = MyDbManager(inflater.context)
        currentContext = inflater.context

        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.resycleViewAccounts)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.buttonGoToAccoutEditActivity).setOnClickListener {
            val intent = Intent(view.context, AccountEditActivity::class.java)
            intent.putExtra(MyConstants.STATE, MyConstants.STATE_ADD)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDatabase()

        val list : List<MyAccount> = myDbManager.fromAccounts
        adapter.addAllAccountList(list)
    }

    override fun onPause() {
        super.onPause()
        myDbManager.closeDatabase()
    }

    companion object{
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onAccountClick(myAccount: MyAccount) {
        val intent = Intent(currentContext, AccountEditActivity::class.java)
        intent.putExtra(MyConstants.STATE, MyConstants.STATE_CHANGE_OR_DELETE)
        intent.putExtra(MyConstants.KEY_MY_ACCOUNT, myAccount)
        startActivity(intent)
    }
}