package com.medical.my_medicos.activities.notifications.api;

import static com.medical.my_medicos.activities.utils.ConstantsNotification.CONTENT_TYPE_NOTIFICATION;
import static com.medical.my_medicos.activities.utils.ConstantsNotification.SERVER_KEY_NOTIFICATION;

import com.medical.my_medicos.activities.notifications.model.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApiInterface {

    @Headers({"Authorization: key=" + SERVER_KEY_NOTIFICATION, "Content-Type:"+CONTENT_TYPE_NOTIFICATION})
    @POST("fcm/send")
    Call<PushNotification> sendNotification(@Body PushNotification notification);

}
