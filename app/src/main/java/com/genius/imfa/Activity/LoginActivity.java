package com.genius.imfa.Activity;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.NetworkConnectionCheck;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.Util;
import com.genius.imfa.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    String android_id;
    Pref pref;
    String AEMEmployeeID;
    String UserType, SecurityCode, TutorialFlag,LoginFlag,IsModified;;
    String version;
    private NetworkConnectionCheck connectionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etUserId.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please Enter UserID", Toast.LENGTH_SHORT).show();
                } else if (binding.etPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    if (connectionCheck.isNetworkAvailable()){
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("MasterID",encrypt(binding.etUserId.getText().toString().trim(),SECRET_KEY));
                            jsonObject.put("Password",encrypt(binding.etPassword.getText().toString().trim(),SECRET_KEY));
                            jsonObject.put("IMEI",android_id);
                            jsonObject.put("DeviceID",android_id);
                            jsonObject.put("DeviceType","A");
                            jsonObject.put("SecurityCode",Api.SECURITY_CODE);
                            loginFunction(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        connectionCheck.getNetworkActiveAlert().show();
                    }

                }
            }
        });

        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        pref = new Pref(getApplicationContext());
        connectionCheck = new NetworkConnectionCheck(this);
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            Log.d("sddk", version);
            Log.d("sdkl", String.valueOf(verCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pref.getCheckFlag().equals("1")) {
            binding.ckRemember.setChecked(true);
            binding.etUserId.setText(pref.getLoginID());
            binding.etPassword.setText(pref.getPassword());
            //etSecurityCode.setText(pref.getSecurityCode());
        }

        if (pref.getCheckFlag().equals("2")) {
            binding.ckRemember.setChecked(false);
            binding.etUserId.setText("");
            binding.etPassword.setText("");
            //etSecurityCode.setText("");
        }

        binding.ckRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pref.saveCheckFlag("1");
                } else {
                    pref.saveCheckFlag("2");
                }
            }
        });
    }


    private void loginFunction(JSONObject jsonObject) {
        Log.e(TAG, "LOGIN_OBJ: "+jsonObject.toString());
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Loading...");
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
                        Log.e(TAG, "LOGIN: " +job1);
                        pd.dismiss();
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message = job1.optString("Response_Message");
                        if (Response_Code==101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            JSONArray responseData = job1.optJSONArray("Response_Data");
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject obj = responseData.optJSONObject(i);
                                AEMEmployeeID = obj.optString("AEMEmployeeID");
                                pref.saveEmpId(AEMEmployeeID);
                                String Genius_Access_Token=obj.optString("Genius_Access_Token");
                                pref.saveAccessToken(Genius_Access_Token);
                                String Access_Token_Expires_On= Util.changeAnyDateFormat(obj.optString("Access_Token_Expires_On"),"dd-MM-yyyy hh:mm:ss","hh:mm");
                                Log.d("aemp", pref.getEmpId());
                                String Name = obj.optString("Name");
                                pref.saveEmpName(Name);
                                Log.d("empname", Name);
                                String LoginDateTime = obj.optString("LoginDateTime");
                                pref.saveloginTime(LoginDateTime);
                                String FlagMenu = obj.optString("FlagMenu");
                                pref.saveMenu(FlagMenu);
                                Log.d("menud", pref.getMenu());
                                String AEMConsultantID = obj.optString("AEMConsultantID");
                                pref.saveEmpConId(AEMConsultantID);
                                String AEMClientID = obj.optString("AEMClientID");
                                pref.saveEmpClintId(AEMClientID);
                                String AEMClientOfficeID = obj.optString("AEMClientOfficeID");
                                pref.saveEmpClintOffId(AEMClientOfficeID);
                                String MasterID = obj.optString("MasterID");
                                pref.saveMasterId(binding.etUserId.getText().toString());
                                Log.d("Master", MasterID);
                                UserType = obj.optString("UserType");
                                pref.saveUserType(UserType);
                                String CTCUrl = obj.optString("CtcPage");
                                pref.saveCTCURL(CTCUrl);
                                Log.d("ctcurl", CTCUrl);
                                String FlagAddr = obj.optString("FlagAddr");
                                pref.saveFlagLocation(FlagAddr);
                                String Password = obj.optString("Password");
                                pref.savePassword(Password);
                                SecurityCode = obj.optString("SecurityCode");
                                pref.saveSecurityCode(SecurityCode);
                                String LeavePage = obj.optString("LeavePage");
                                pref.saveLeaveUrl(LeavePage);
                                String PayrollMenu = obj.optString("PayrollMenu");
                                pref.savePayrollFlag(PayrollMenu);
                                String ImgLocation = obj.optString("ImgLocation");
                                pref.saveImgFlag(ImgLocation);
                                String Loginid = obj.optString("Loginid");
                                pref.saveLoginID(binding.etUserId.getText().toString());
                                String DailyActivityFlag = obj.optString("DailyActivityFlag");
                                pref.saveDailyActivityFlag(DailyActivityFlag);
                                String AttendanceEnableStatus = obj.optString("AttendanceEnableStatus");
                                pref.saveAttenFlag(AttendanceEnableStatus);
                                String DailyLogActivity = obj.optString("DailyLogActivity");
                                pref.saveDailyLogFlag(DailyLogActivity);
                                Log.d("DailyLogActivity", DailyLogActivity);
                                String DemoFlag = obj.optString("DemoFlag");
                                pref.saveDemoFlag(DemoFlag);
                                String OfflineFlag = obj.optString("OfflineFlag");
                                pref.saveOffAttnFlag(OfflineFlag);
                                String GeoConfFlag = obj.optString("GeoConfFlag");
                                TutorialFlag = obj.optString("TutorialFlag");
                                pref.saveTutorialFlag(TutorialFlag);
                                String TutorialMenuText = obj.optString("TutorialMenuText");
                                pref.saveTutorialText(TutorialMenuText);
                                pref.saveGeoFenceConfig(GeoConfFlag);
                                String GeoFenceMenuFlag = obj.optString("GeoFenceMenuFlag");
                                pref.saveGeoFenceMenuFlag(GeoFenceMenuFlag);
                                String GeoFenceAttFlag = obj.optString("GeoFenceAttFlag");
                                pref.saveGeoFenceFlag(GeoFenceAttFlag);
                                String GeoMultiPointConfFlag = obj.optString("GeoMultiPointConfFlag");
                                pref.saveFenceSubMenu(GeoMultiPointConfFlag);
                                pref.saveLoginFlag("1");
                                if (pref.getCheckFlag().equals("1")) {
                                    pref.saveLoginFlag("1");
                                } else {
                                    pref.saveLoginFlag("2");
                                }
                                String LiveDriverTrackingFlag = obj.optString("LiveDriverTrackingFlag");
                                pref.saveLivetrackingFlag(LiveDriverTrackingFlag);
                                String LiveTrackingFlag = obj.optString("LiveTrackingFlag");
                                pref.saveLivetrackingServiceFlag(LiveTrackingFlag);
                                String MultiLangFlag = obj.optString("MultiLangFlag");
                                pref.saveLanguageFlag(MultiLangFlag);
                                pref.saveLanguage("en");
                                LoginFlag = "1";//hardcode on sp by dk and it willbe remove .sp name:GHRMSUserAuthenticationWithnDevice_New
                                String ITPage = obj.optString("ITPage");
                                pref.saveITView(ITPage);
                                String TeamRptFlag = obj.optString("TeamRptFlag");
                                pref.saveTeamReportFlag(TeamRptFlag);
                                IsModified = obj.optString("IsModified");

                                String GeoFenceMapFlag = obj.optString("GeoFenceMapFlag");
                                pref.saveEmpMapAccessFlag(GeoFenceMapFlag);
                                String IsWeeklyOff = obj.optString("IsWeeklyOff");
                                pref.saveWeeklyOffFlag(IsWeeklyOff);
                                String IsHoliday = obj.optString("IsHoliday");
                                pref.saveHolidayMapFlag(IsHoliday);
                            }

                            JSONObject object=new JSONObject();
                            try {
                                object.put("SecurityCode",pref.getSecurityCode());
                                versionCheck(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                             /*Intent intent = new Intent(LoginActivity.this,UserDashboardActivity.class);
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(intent);*/
                        } else {
                           /* shoeDialog();
                            imgForward.setVisibility(View.VISIBLE);
                            pgBar.setVisibility(View.GONE);*/
                            Toast.makeText(LoginActivity.this, Response_Message, Toast.LENGTH_SHORT).show();
                        }
                        // boolean _status = job1.getBoolean("status");
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("LOGIN", "onError: "+error );
                        pd.dismiss();
                    }
                });
    }

    private void versionCheck(JSONObject object) {
        final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sVersionCheckApi)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        pd.dismiss();
                        JSONObject job1 = response;
                        Log.e(TAG, "VERSION_CHECK: " + job1);


                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                JSONObject obj = jsonArray.optJSONObject(0);
                                boolean bAndriodAutoUpdateStatus=obj.optBoolean("bAndriodAutoUpdateStatus");
                                int AndriodAutoUpdateStatus = obj.optInt("AndriodAutoUpdateStatus");
                                String AndriodVersion=obj.optString("AndriodVersion");
                                boolean AppRenameFlag=obj.optBoolean("AppRenameFlag");
                                String AppRenameText=obj.optString("AppRenameText");
                                pref.saveMsgStatus(AppRenameFlag);
                                pref.saveMsg(AppRenameText);

                                Log.e(TAG, "onResponse: CALLED 1");

                                if (version.equals(AndriodVersion)) {
                                    Log.e(TAG, "onResponse: CALLED 2");
                                    if (IsModified.equals("1")) {
                                        Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("ismodiFied", IsModified);
                                        intent.putExtra("empId", AEMEmployeeID);
                                        intent.putExtra("securityCode", SecurityCode );
                                        intent.putExtra("goingstatus","1");
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Log.e(TAG, "onResponse: CALLED 3");

                                    if (AndriodAutoUpdateStatus == 1){
                                        Intent intent = new Intent(LoginActivity.this, UpdateActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        if (IsModified.equals("1")) {
                                            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ismodiFied", IsModified);
                                            intent.putExtra("empId", AEMEmployeeID );
                                            intent.putExtra("securityCode", SecurityCode );
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // boolean _status = job1.getBoolean("status");


                            // do anything with response
                        }else {

                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });
    }
}