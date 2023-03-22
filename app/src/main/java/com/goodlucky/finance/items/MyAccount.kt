package com.goodlucky.finance.items

import java.io.Serializable

data class MyAccount(val _id: Long, val name: String, val idBank : Long, val idCurrency : Long) : Serializable{
    constructor() : this(0, "", 0, 0)

    override fun toString(): String {
        return name
    }
}
