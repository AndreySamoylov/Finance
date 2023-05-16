package com.goodlucky.finance.items


import android.content.Context
import android.text.format.DateUtils
import java.io.Serializable
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

data class MyReceipt(
    val _id: Long,
    val code: String,
    val date : String,
    val sum : Double,
    // То что появиться после сканирования
    val items : String, // Что куплено?
    val retailPlace : String, // Название места
    val retailPlaceAddress : String, // Адрес места
    )  : Serializable {

    //var context : Context? = null

    constructor() : this(
        0,
        "",
        "",
        0.0,
        "",
        "",
        "",
    )

    override fun toString(): String {
//        // Инициализация календаря
//        val calendar: Calendar = Calendar.getInstance()
//        calendar.set(Calendar.YEAR, date.substring(0, 4).toInt())
//        calendar.set(Calendar.MONTH, date.substring(5, 7).toInt())
//        calendar.set(Calendar.DAY_OF_MONTH, date.substring(8, 10).toInt())
//        calendar.set(Calendar.HOUR, date.substring(11, 13).toInt())
//        calendar.set(Calendar.MINUTE, date.substring(14, 16).toInt())
//
//        val strDate = DateUtils.formatDateTime(context, calendar.timeInMillis,
//            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_TIME)

        return "$date \t $sum"
    }
}
