package com.genius.imfa.Leave;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.Activity.UserDashboardActivity;
import com.genius.imfa.Payroll.PayrollActivity;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.databinding.ActivityLeaveApplicationDashboardBinding;
import com.genius.imfa.wfh.WFHActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LeaveApplicationDashboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaveApplicationDashboa";
    ActivityLeaveApplicationDashboardBinding binding;
    Pref pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_leave_application_dashboard);
        binding = ActivityLeaveApplicationDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        onClick();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
        JSONObject object=new JSONObject();
        try {
            object.put("CompanyID",pref.getEmpClintId());
            object.put("MenuItemName","Leave");
            object.put("SecurityCode",pref.getSecurityCode());
            accessCheckingLeave(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            jsonObject.put("EmployeeId",pref.getEmpId());
            getWFHMenu(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void onClick() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveApplicationDashboardActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.llLeaveApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveApplicationDashboardActivity.this, LeaveApplicationActivity.class);
                startActivity(intent);
            }
        });


        binding.llWFH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveApplicationDashboardActivity.this, WFHActivity.class);
                startActivity(intent);
            }
        });

        binding.llLeaveBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveApplicationDashboardActivity.this,LeaveBalanceActivity.class);
                startActivity(intent);
            }
        });

        binding.llLeaveOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveApplicationDashboardActivity.this,OtherLeavesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void accessCheckingLeave(JSONObject object) {
        final ProgressDialog pd=new ProgressDialog(LeaveApplicationDashboardActivity.this);
        pd.setMessage("Loading");
        pd.show();
        pd.setCancelable(false);

        AndroidNetworking.post(Api.sGetMenuOnOff)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "GET_MENU_ON_OFF: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message = job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            JSONObject menuObj = job1.optJSONObject("Response_Data");
                            if (menuObj.optBoolean("MenuStatus")){
                                binding.lnAccess.setVisibility(View.GONE);
                                binding.lnNonAccess.setVisibility(View.VISIBLE);
                            } else {
                                binding.lnAccess.setVisibility(View.VISIBLE);
                                binding.lnNonAccess.setVisibility(View.GONE);
                            }
                        } else {
                            binding.lnAccess.setVisibility(View.GONE);
                            binding.lnNonAccess.setVisibility(View.VISIBLE);
                            Toast.makeText(LeaveApplicationDashboardActivity.this, Response_Message, Toast.LENGTH_SHORT).show();
                            //Log.e(TAG, "ERROR: "+job1.optJSONObject("Response_Data"));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "GET_MENU_ON_OFF_onError: "+anError);
                        pd.dismiss();
                        if (anError.getErrorCode()==401){
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
                        } else {
                            binding.lnAccess.setVisibility(View.VISIBLE);
                            binding.lnNonAccess.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void login(JSONObject jsonObject) {
        final ProgressDialog pd = new ProgressDialog((Context) LeaveApplicationDashboardActivity.this);
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
                                    object.put("CompanyID",pref.getEmpClintId());
                                    object.put("MenuItemName","Leave");
                                    object.put("SecurityCode",pref.getSecurityCode());
                                    accessCheckingLeave(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });
    }


    public void getWFHMenu(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveAllDetails: called: "+jsonObject);
        ProgressDialog progressDialog=new ProgressDialog(LeaveApplicationDashboardActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        AndroidNetworking.post(Api.sGetWFHmenu)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject job1 = response;
                        Log.e(TAG, "getLeaveAllDetails: " + job1);

                        progressDialog.dismiss();
                        int Response_Code = job1.optInt("Response_Code");
                        JSONObject Response_Data=job1.optJSONObject("Response_Data");
                        boolean MenuStatus=Response_Data.optBoolean("MenuStatus");
                        if (MenuStatus){
                            binding.llWFH.setVisibility(View.VISIBLE);
                        }else {
                            binding.llWFH.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();

                    }
                });
    }

}