package com.medical.my_medicos.activities.home;


import com.medical.my_medicos.activities.home.model.PlanRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/plans/generate")
    Call<ResponseBody> generatePlan(@Body PlanRequest planRequest);  // Endpoint of the signup API // No response body expected


}

