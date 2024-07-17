package com.genius.imfa.common;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.Model.HoliDayModel;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.adapter.HolidayAdapter;
import com.genius.imfa.databinding.ActivityHolidayBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class HolidayActivity extends AppCompatActivity {
    private static final String TAG = "HolidayActivity";
    ActivityHolidayBinding binding;
    ArrayList<HoliDayModel> holidayList=new ArrayList<>();
    Pref pref;
    String year;
    int y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHolidayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        btnClick();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
        binding.rvHoliday.setLayoutManager(new LinearLayoutManager(HolidayActivity.this));
        y= Calendar.getInstance().get(Calendar.YEAR);
        year=String.valueOf(y);

        JSONObject object=new JSONObject();
        try {
            object.put("AEMEmployeeID",pref.getEmpId());
            object.put("Year",year);
            object.put("SecurityCode",pref.getSecurityCode());
            holiday(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void btnClick() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HolidayActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void holiday(JSONObject jsonObject) {
        Log.e(TAG, "holiday: "+jsonObject.toString());
        binding.llWLLoader.setVisibility(View.VISIBLE);
        binding.llMain.setVisibility(View.GONE);
        binding.llNodata.setVisibility(View.GONE);
        binding.llAgain.setVisibility(View.GONE);
        AndroidNetworking.post(Api.sHolidayapi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject job1 = response;
                        Log.e(TAG, "HOLIDAY_LIST: " + job1);

                        try {
                            int Response_Code = job1.optInt("Response_Code");
                            if (Response_Code == 101) {
                                // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                                String responseData = job1.optString("Response_Data");

                                JSONArray jsonArray = new JSONArray(responseData);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String HolidayName = obj.optString("HolidayName");
                                    String HolidayDate = obj.optString("HolidayDate");
                                    String HDay = obj.optString("HDay");
                                    Log.e(TAG, "HolidayName: "+obj.optString("HolidayName"));
                                    HoliDayModel obj2 = new HoliDayModel(HolidayName, HolidayDate, HDay);
                                    holidayList.add(obj2);
                                }

                                binding.llWLLoader.setVisibility(View.GONE);
                                binding.llMain.setVisibility(View.VISIBLE);
                                binding.llNodata.setVisibility(View.GONE);
                                binding.llAgain.setVisibility(View.GONE);
                                setAdapter();
                                // boolean _status = job1.getBoolean("status");
                                // do anything with response
                            } else {
                                binding.llWLLoader.setVisibility(View.GONE);
                                binding.llMain.setVisibility(View.VISIBLE);
                                binding.llNodata.setVisibility(View.VISIBLE);
                                binding.llAgain.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode()==401){
                            JSONObject obj=new JSONObject();
                            try {
                                obj.put("MasterID",encrypt(pref.getMasterId(),SECRET_KEY));
                                obj.put("Password",encrypt(pref.getPassword(),SECRET_KEY));
                                obj.put("IMEI","0");
                                obj.put("DeviceID","0");
                                obj.put("DeviceType","A");
                                obj.put("SecurityCode",pref.getSecurityCode());
                                login(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void login(JSONObject jsonObject) {
        final ProgressDialog pd = new ProgressDialog(HolidayActivity.this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sLogin)
                .addJSONObjectBody(jsonObject)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        JSONObject job1 = response;
                        Log.e("response12", "@@@@@@" + job1);
                        pd.dismiss();

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            JSONArray responseData = job1.optJSONArray("Response_Data");
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject obj = responseData.optJSONObject(i);
                                String Genius_Access_Token=obj.optString("Genius_Access_Token");
                                pref.saveAccessToken(Genius_Access_Token);

                                // boolean _status = job1.getBoolean("status");


                                JSONObject object=new JSONObject();
                                try {

                                    object.put("AEMEmployeeID",pref.getEmpId());
                                    object.put("Year",year);
                                    object.put("SecurityCode",pref.getSecurityCode());
                                    holiday(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                // do anything with response
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();


                    }
                });
    }

    private  void setAdapter(){
        HolidayAdapter hAdapter =new HolidayAdapter(holidayList,HolidayActivity.this);
        binding.rvHoliday.setAdapter(hAdapter);
    }
}