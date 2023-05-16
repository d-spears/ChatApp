package com.app.chatapp.interfaceApi

import com.app.chatapp.PushNotification
import com.example.chatapp.constants.Constants.Companion.CONTENT_TYPE
import com.example.chatapp.constants.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: com.app.chatapp.PushNotification
    ): Response<ResponseBody>

}