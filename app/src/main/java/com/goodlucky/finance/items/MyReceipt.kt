package com.goodlucky.finance.items


import java.io.Serializable

data class MyReceipt(val _id: Long, val code: String)  : Serializable {
    constructor() : this(0, "")

    override fun toString(): String {
        return code
    }
}
