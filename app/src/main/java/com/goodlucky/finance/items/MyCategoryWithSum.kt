package com.goodlucky.finance.items


data class MyCategoryWithSum(
    var _id: Long,
    var _name: String,
    var _color: String,
    var _image: Int,
    var _sum : Double,
    var _type : Byte,
    var _undeletable : Byte = 0
) : java.io.Serializable{
    constructor() : this(0, "", "", 0,0.0, 0, 0)
    override fun toString(): String {
        return _name
    }
}
