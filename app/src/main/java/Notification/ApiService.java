package Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlV3AGTI:APA91bFpAm0NG8sT4oK3Os7mS5SgP9fDu-8hj5uxLyM9J1-V1tgckrvpmY9-ykT16M0aVgPDGQyidoK6iEB7pPyEyQPerhqSMZKJD4tlsvPbLcKv3wTPfqQo_cHL3KyX4Di109-Tbm_P"

            }
            )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
