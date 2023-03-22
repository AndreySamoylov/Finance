package com.goodlucky.finance.firebase

import com.goodlucky.finance.items.*

data class MyFirebaseUserData(var listCost : List<MyCost>,
                              var listIncome : List<MyIncome>,
                              var listCategory : List<MyCategoryFirebase>,
                              var listAccount : List<MyAccount>,
                              var listBank : List<MyBank>,
                              var listCurrency: List<MyCurrency>,
                              var listReceipt: List<MyReceipt>
){
    constructor() : this(listOf(), listOf(),listOf(),listOf(), listOf(), listOf(), listOf())
}
