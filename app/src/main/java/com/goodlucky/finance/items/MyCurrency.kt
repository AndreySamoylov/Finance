package com.goodlucky.finance.items


import java.io.Serializable

data class MyCurrency(val _id: Long,
                      val name_full: String,
                      val name_short : String,
                      val exchange_rate : Double) : Serializable{
    constructor() : this(0, "", "", 0.0)

    override fun toString(): String {
        return name_full
    }
}
