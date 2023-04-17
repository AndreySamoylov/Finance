package com.goodlucky.finance.receipts.jsonObjects

import com.google.gson.annotations.SerializedName

data class Barcode(
    @SerializedName("status") val status : Int,
    @SerializedName("statusReal") val statusReal : Int,
    @SerializedName("id") val id : String,
    @SerializedName("kind") val kind : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("qr") val qr : String,
    @SerializedName("operation") val operation : Operation,
    @SerializedName("process") val process : List<Process>,
    @SerializedName("query") val query : Query,
    @SerializedName("ticket") val ticket : Ticket,
    @SerializedName("organization") val organization : Organization,
    @SerializedName("seller") val seller : Seller
)

data class Operation(
    @SerializedName("date") val date : String,
    @SerializedName("type") val type : Int,
    @SerializedName("sum") val sum : Float
)

data class Process(
    @SerializedName("time") val time : String,
    @SerializedName("result") val result : Int
)

data class Query(
    @SerializedName("operationType") val operationType : Int,
    @SerializedName("sum") val sum : Float,
    @SerializedName("documentId") val documentId : Long,
    @SerializedName("fsId") val fsId : String,
    @SerializedName("fiscalSign") val fiscalSign : String,
    @SerializedName("date") val date : String,
)

data class Ticket(
    @SerializedName("document") val document : Document,
)

data class Document(
    @SerializedName("receipt" ) val receipt : Receipt,
)

data class Receipt(
    @SerializedName("dateTime") val dateTime : Long,
    @SerializedName("addressToCheckFiscalSign") val addressToCheckFiscalSign : String,
    @SerializedName("buyerAddress") val buyerAddress : String,
    @SerializedName("cashTotalSum") val cashTotalSum : Float,
    @SerializedName("ecashTotalSum") val ecashTotalSum : Float,
    @SerializedName("fiscalDocumentNumber") val fiscalDocumentNumber : Int,
    @SerializedName("fiscalDriveNumber") val fiscalDriveNumber : String,
    @SerializedName("fiscalSign") val fiscalSign : Long,
    @SerializedName("items") val items: List<Item>,
    @SerializedName("kktRegId")  val kktRegId : String,
    @SerializedName("nds18") val nds18 : Float,
    @SerializedName("operationType") val operationType : Int,
    @SerializedName("operator") val operator : String,
    @SerializedName("receiptCode")  val receiptCode : Int,
    @SerializedName("requestNumber") val requestNumber : Int,
    @SerializedName("retailPlaceAddress") val retailPlaceAddress : String,
    @SerializedName("senderAddress") val senderAddress : String,
    @SerializedName("shiftNumber") val shiftNumber : Int,
    @SerializedName("taxationType") val taxationType : Int,
    @SerializedName("totalSum") val totalSum : Float,
    @SerializedName("user") val user : String,
    @SerializedName("userInn") val userInn : String,
)

data class Item(
    @SerializedName("name") val name : String,
    @SerializedName("price") val price : Double,
    @SerializedName("quantity") val quantity : Int,
    @SerializedName("sum") val sum : Double
)

data class Organization(
    @SerializedName("name") val name : String,
    @SerializedName("inn") val inn : String
)

data class Seller(
    @SerializedName("name") val name : String,
    @SerializedName("inn") val inn : String
)
