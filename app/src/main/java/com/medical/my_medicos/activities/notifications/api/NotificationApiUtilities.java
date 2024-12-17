package com.medical.my_medicos.activities.notifications.api;

import static com.medical.my_medicos.activities.utils.ConstantsNotification.BASE_URL_NOTIFICATION;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationApiUtilities {

    private static Retrofit retrofit = null;

    public static NotificationApiInterface getClient(){

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_NOTIFICATION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(NotificationApiInterface.class);

    }
}
