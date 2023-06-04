package com.goodlucky.finance.items

data class MyLimit(
    var _id: Long,
    var _type: Byte,
    var _sum: Double,
    var _id_category: Long,
    var _id_currency: Long)
{
    constructor() : this(0, 0, 0.0,  0, 0)
}
