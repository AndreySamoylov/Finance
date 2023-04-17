package com.goodlucky.finance.items


import java.io.Serializable

data class MyReceipt(
    val _id: Long,
    val code: String,
    val date : String,
    val sum : Double)  : Serializable {
    constructor() : this(0, "", "", 0.0)

    override fun toString(): String {
        return code
    }
}
