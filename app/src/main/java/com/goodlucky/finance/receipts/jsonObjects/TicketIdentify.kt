package com.goodlucky.finance.receipts.jsonObjects

data class TicketIdentify(
    val kind : String,
    val id : String,
    val status : Int,
    val statusReal : Int
)
