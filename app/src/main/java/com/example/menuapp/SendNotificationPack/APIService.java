package com.example.menuapp.SendNotificationPack;

import com.example.menuapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAwEFI5e4:APA91bFSQX3VcCztgTU7cgZ7SnM0XYDTH7wZXtQG4UyU5gJiiNX-6cDXxHJm9KgihoUCtxmxf74pdUYcPyutF0eNi7j7vmuUwo0a-UkY94wxXbpKy8iXg1w8PfJF9zGHmeJ5DGgXDAOy" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

