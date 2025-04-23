package com.genius.imfa.Utility;


import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RefreshAccessToken {
    private static final String TAG = "RefreshAccessToken";
    static Pref pref;
    public static void apiCall(Context context, final OnResponse onResponse){
        pref = new Pref(context);
        onResponse.onPostCall();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("MasterID",encrypt(pref.getMasterId(),SECRET_KEY));
            jsonObject.put("Password",encrypt(pref.getPassword(),SECRET_KEY));
            jsonObject.put("IMEI","11");
            jsonObject.put("DeviceID","11");
            jsonObject.put("DeviceType","A");
            jsonObject.put("SecurityCode",Api.SECURITY_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(Api.sLogin)
                .addJSONObjectBody(jsonObject)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject job1 = response;
                            String responseText = job1.optString("responseText");
                            responseText = job1.optString("Response_Message");
                            int Response_Code = job1.optInt("Response_Code");
                            if (Response_Code == 101) {
                                JSONArray responseData = job1.optJSONArray("Response_Data");
                                for (int i = 0; i < responseData.length(); i++) {
                                    JSONObject obj = responseData.getJSONObject(i);
                                    String Genius_Access_Token=obj.optString("Genius_Access_Token");
                                    pref.saveAccessToken(Genius_Access_Token);
                                    onResponse.onSuccess();
                                }
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "LOGIN_error: "+anError.getErrorBody());
                        onResponse.onFailure(anError.getErrorBody());
                    }
                });
    }

    public interface OnResponse{
        void onPostCall();
        void onSuccess();
        void onFailure(String message);
    }
}
