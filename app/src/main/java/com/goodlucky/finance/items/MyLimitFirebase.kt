package com.goodlucky.finance.items

data class MyLimitFirebase(var _id: Long,
                           var _type: Int,
                           var _sum: Double,
                           var _id_category: Long,
                           var _id_currency: Long) {
    constructor() : this(0, 0, 0.0,  0, 0)
}