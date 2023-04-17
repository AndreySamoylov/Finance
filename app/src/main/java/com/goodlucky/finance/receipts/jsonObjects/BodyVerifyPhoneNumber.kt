package com.goodlucky.finance.receipts.jsonObjects

data class BodyVerifyPhoneNumber(
    val phone : String,
    val code : String,
    val client_secret : String,
    val os : String
)
