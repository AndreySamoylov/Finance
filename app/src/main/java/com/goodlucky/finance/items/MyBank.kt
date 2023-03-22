package com.goodlucky.finance.items


import java.io.Serializable

data class MyBank(val _id: Long, val name: String) : Serializable{
    constructor() : this(0, "")

    override fun toString(): String {
        return name
    }
}