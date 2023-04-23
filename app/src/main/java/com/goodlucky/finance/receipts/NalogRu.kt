package com.goodlucky.finance.receipts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.goodlucky.finance.receipts.jsonObjects.*

class NalogRu(val nalogRuApi: NalogRuApi) {
    private lateinit var session :  Session
    private lateinit var ticketIdentify : TicketIdentify
    private var barcode: Barcode? = null

    fun sendPhoneNumber(phoneNumber: String){
        CoroutineScope(Dispatchers.IO).launch {
            val bodySendPhoneNumber = BodySendPhoneNumber(phoneNumber, NalogRuApiConstants.CLIENT_SECRET, NalogRuApiConstants.OS)
            nalogRuApi.sendPhoneNumber(bodySendPhoneNumber)
        }
    }

    fun verifyPhoneNumber(phoneNumber: String , code : String){
        CoroutineScope(Dispatchers.IO).launch {
            val bodyVerifyPhoneNumber = BodyVerifyPhoneNumber(phoneNumber, code, NalogRuApiConstants.CLIENT_SECRET, NalogRuApiConstants.OS)
            session = nalogRuApi.verifyPhoneNumber(bodyVerifyPhoneNumber)
        }
    }

    private fun getTicketId(qrCode : String){
        CoroutineScope(Dispatchers.IO).launch {
            val bodyGetTicketId = BodyGetTicketId(qrCode)
            ticketIdentify = nalogRuApi.getTickerId(session.sessionId, bodyGetTicketId)
        }
    }

    private fun getTicket(qrCode: String){
        CoroutineScope(Dispatchers.IO).launch{
            // Получить идентификатор чека
            val bodyGetTicketId = BodyGetTicketId(qrCode)
            ticketIdentify = nalogRuApi.getTickerId(session.sessionId, bodyGetTicketId)

            // Получить информацию о чеке по его идентификатору
            barcode = nalogRuApi.getTicket(session.sessionId, ticketIdentify.id)

            //Обновить токен, сессию
            val bodyRefreshToken = BodyRefreshToken(session.refresh_token, NalogRuApiConstants.CLIENT_SECRET)
            session = nalogRuApi.refreshToken(bodyRefreshToken)
        }
    }

    fun returnBarcode(qrCode: String) : Barcode {
        getTicket(qrCode)
        while(barcode == null){
            Thread.sleep(500)
        }
        return barcode!!
    }
}