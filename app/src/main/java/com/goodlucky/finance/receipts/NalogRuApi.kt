package com.goodlucky.finance.receipts

import com.goodlucky.finance.receipts.jsonObjects.*
import retrofit2.Response
import retrofit2.http.*

interface NalogRuApi {
    @Headers(
        "Host: ${NalogRuApiConstants.HOST}",
        "Accept: ${NalogRuApiConstants.ACCEPT}",
        "Device-OS: ${NalogRuApiConstants.DEVICE_OS}",
        "Device-Id: ${NalogRuApiConstants.DEVICE_ID}",
        "clientVersion: ${NalogRuApiConstants.CLIENT_VERSION}",
        "Accept-Language: ${NalogRuApiConstants.ACCEPT_LANGUAGE}",
        "User-Agent: ${NalogRuApiConstants.USER_AGENT}",
    )
    @POST("v2/auth/phone/request")
    suspend fun sendPhoneNumber(@Body bodySendPhoneNumber: BodySendPhoneNumber) : Response<Unit>

    @Headers(
        "Host: ${NalogRuApiConstants.HOST}",
        "Accept: ${NalogRuApiConstants.ACCEPT}",
        "Device-OS: ${NalogRuApiConstants.DEVICE_OS}",
        "Device-Id: ${NalogRuApiConstants.DEVICE_ID}",
        "clientVersion: ${NalogRuApiConstants.CLIENT_VERSION}",
        "Accept-Language: ${NalogRuApiConstants.ACCEPT_LANGUAGE}",
        "User-Agent: ${NalogRuApiConstants.USER_AGENT}",
    )
    @POST("v2/auth/phone/verify")
    suspend fun verifyPhoneNumber(@Body bodyVerifyPhoneNumber: BodyVerifyPhoneNumber) : Session

    @Headers(
        "Host: ${NalogRuApiConstants.HOST}",
        "Accept: ${NalogRuApiConstants.ACCEPT}",
        "Device-OS: ${NalogRuApiConstants.DEVICE_OS}",
        "Device-Id: ${NalogRuApiConstants.DEVICE_ID}",
        "clientVersion: ${NalogRuApiConstants.CLIENT_VERSION}",
        "Accept-Language: ${NalogRuApiConstants.ACCEPT_LANGUAGE}",
        "User-Agent: ${NalogRuApiConstants.USER_AGENT}",
    )
    @POST("v2/ticket")
    suspend fun getTickerId(@Header("sessionId") sessionId : String,
                            @Body bodyGetTicketId: BodyGetTicketId) : TicketIdentify

    @Headers(
        "Host: ${NalogRuApiConstants.HOST}",
        "Accept: ${NalogRuApiConstants.ACCEPT}",
        "Device-OS: ${NalogRuApiConstants.DEVICE_OS}",
        "Device-Id: ${NalogRuApiConstants.DEVICE_ID}",
        "clientVersion: ${NalogRuApiConstants.CLIENT_VERSION}",
        "Accept-Language: ${NalogRuApiConstants.ACCEPT_LANGUAGE}",
        "User-Agent: ${NalogRuApiConstants.USER_AGENT}",
        "Content-Type: application/json"
    )
    @GET("v2/tickets/{ticket_id}")
    suspend fun getTicket(@Header("sessionId") sessionId : String,
                          @Path("ticket_id") ticket_id : String) : Barcode
}