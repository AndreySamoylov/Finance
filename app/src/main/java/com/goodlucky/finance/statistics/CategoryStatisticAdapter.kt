package com.goodlucky.finance.statistics


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goodlucky.finance.MyConstants
import com.goodlucky.finance.R
import com.goodlucky.finance.databinding.CategoryStatisticItemBinding
import com.goodlucky.finance.items.MyCategory
import com.goodlucky.finance.items.MyCategoryWithSum
import java.text.DecimalFormat


class CategoryStatisticAdapter(val listener: Listener, val context: Context) : RecyclerView.Adapter<CategoryStatisticAdapter.CategoryStatisticHolder>() {

    private val categoryList = ArrayList<MyCategoryWithSum>()

    class  CategoryStatisticHolder(item : View) : RecyclerView.ViewHolder(item){
        private val binding = CategoryStatisticItemBinding.bind(item)

        fun bind(myCategory: MyCategoryWithSum, listener : Listener) = with(binding){

            //Округление до 2-ух знаков полсе запятой
            val decimalFormat = DecimalFormat("##0.00")
            val strSum: String = decimalFormat.format(myCategory._sum)

            val text = if (myCategory._type == MyConstants.CATEGORY_TYPE_COST)
                "${myCategory._name}    - $strSum"
            else
                "${myCategory._name}    + $strSum"

            textViewitemCategoryStatisticName.text = text
            imageViewItemCategoryStatistic.setBackgroundColor(Color.parseColor(myCategory._color))
            imageViewItemCategoryStatistic.setImageResource(myCategory._image)

            itemView.setOnClickListener{
                listener.onCategoryClick(myCategory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryStatisticHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_statistic_item, parent, false)
        return CategoryStatisticHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryStatisticHolder, position: Int) {
        holder.bind(categoryList[position], listener)
    }

    fun addCategory(myCategory: MyCategoryWithSum){
        categoryList.add(myCategory)
        notifyDataSetChanged()
    }

    fun addAllCategoryList(list: List<MyCategoryWithSum>){
        categoryList.clear()
        categoryList.addAll(list)
        notifyDataSetChanged()
    }

    interface Listener{
        // Нажатие на элемент в RecycleView
        fun onCategoryClick(myCategory: MyCategoryWithSum)
    }
}