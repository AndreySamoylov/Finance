package com.goodlucky.finance.items


import java.io.Serializable

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
        return "$date \t $sum"
    }
}
