package com.genius.imfa.Payroll;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.common.UserDashboardActivity;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.databinding.ActivityPayrollBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class PayrollActivity extends AppCompatActivity {
    private static final String TAG = "PayrollActivity";
    ActivityPayrollBinding binding;
    Pref pref;

    int y;
    String year;
    String cuurentFinancialYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_payroll);
        binding = ActivityPayrollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        btnClick();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());

        y = Calendar.getInstance().get(Calendar.YEAR);
        year = String.valueOf(y);
        int futureYear = y + 1;
        cuurentFinancialYear = year + "-" + futureYear;

        JSONObject object=new JSONObject();
        try {
            object.put("CompanyID",pref.getEmpClintId());
            object.put("MenuItemName","Payroll");
            object.put("SecurityCode",pref.getSecurityCode());
            accessChecking(object);
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
                Intent intent = new Intent(PayrollActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

       /*binding.llCTC.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               JSONObject object = new JSONObject();
               try {
                   object.put("CompanyID", pref.getEmpClintId());
                   object.put("MenuItemName", "CurrentCTC");
                   object.put("SecurityCode", pref.getSecurityCode());
                   accessCheckingForCTC(object);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });*/

        binding.llSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("CompanyID", pref.getEmpClintId());
                    object.put("MenuItemName", "Payslip");
                    object.put("SecurityCode", pref.getSecurityCode());
                    accessCheckingForSalary(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent(PayrollActivity.this,SalaryActivity.class);
                //startActivity(intent);
            }
        });


    }

    private void accessCheckingForSalary(JSONObject object) {
        final ProgressDialog pd=new ProgressDialog(PayrollActivity.this);
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
                        Log.e(TAG, "GET_MENU_ON_OFF_SALARY: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            JSONObject menuObj = job1.optJSONObject("Response_Data");
                            if (menuObj.optBoolean("MenuStatus")){
                                showAlert();
                            } else {
                                Intent intent=new Intent(PayrollActivity.this, SalaryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            // ERROR
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "GET_MENU_ON_OFF_SALARY: "+anError);
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
                            Intent intent=new Intent(PayrollActivity.this, SalaryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void accessCheckingForCTC(JSONObject object) {
        Log.e(TAG, "accessCheckingForCTC: "+object.toString());
        final ProgressDialog pd=new ProgressDialog(PayrollActivity.this);
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
                        Log.e(TAG, "GET_MENU_ON_OFF_CTC: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            JSONObject menuObj = job1.optJSONObject("Response_Data");
                            if (menuObj.optBoolean("MenuStatus")){
                                showAlert();
                            } else {
                                Intent intent=new Intent(PayrollActivity.this, WebViewActivity.class);
                                intent.putExtra("imageurl",pref.getCTCURL());
                                intent.putExtra("month",".");
                                intent.putExtra("flag","CTC");
                                intent.putExtra("year",cuurentFinancialYear);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            //ERROR
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
                            Intent intent=new Intent(PayrollActivity.this, WebViewActivity.class);
                            intent.putExtra("imageurl",pref.getCTCURL());
                            intent.putExtra("month",".");
                            intent.putExtra("flag","CTC");
                            intent.putExtra("year",cuurentFinancialYear);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void accessChecking(JSONObject object) {
        Log.e(TAG, "accessChecking: "+object);

        final ProgressDialog pd=new ProgressDialog(PayrollActivity.this);
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
                            Toast.makeText(PayrollActivity.this, Response_Message, Toast.LENGTH_SHORT).show();
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
        final ProgressDialog pd = new ProgressDialog((Context) PayrollActivity.this);
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
                                    object.put("MenuItemName","Payroll");
                                    object.put("SecurityCode",pref.getSecurityCode());
                                    accessChecking(object);
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

    private void showAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sorry ! Currently this menu is not accessible");
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        alertDialogBuilder.show();
    }
}