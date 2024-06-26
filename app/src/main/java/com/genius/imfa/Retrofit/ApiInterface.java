package com.genius.imfa.Retrofit;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiInterface {

    @POST("IMFALeave/GetAdjustmentDetailsforGrid")
    Call<JSONObject> postData(@Body JSONObject jsonObject);

}
