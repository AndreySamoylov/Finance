package com.goodlucky.finance.receipts.recicleViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.R
import com.goodlucky.finance.database.MyDbManager
import com.goodlucky.finance.databinding.DoubleTextItemBinding
import com.goodlucky.finance.items.MyAccount


class DoubleTextAdapter(val listener: Listener) : ListAdapter<DoubleTextItem, DoubleTextAdapter.Holder>(Comparator()) {
    class Holder(view : View) : RecyclerView.ViewHolder(view){
        private val binding = DoubleTextItemBinding.bind(view)

        fun bind(doubleTextItem: DoubleTextItem, listener: Listener) = with(binding){
            doubleTextItemTitle.text = doubleTextItem.title
            doubleTextItemDescription.text = doubleTextItem.description

            itemView.setOnClickListener {
                listener.onItemClick(doubleTextItem)
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<DoubleTextItem>(){
        override fun areItemsTheSame(oldItem: DoubleTextItem, newItem: DoubleTextItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: DoubleTextItem, newItem: DoubleTextItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.double_text_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface Listener{
        fun onItemClick(doubleTextItem: DoubleTextItem)
    }
}