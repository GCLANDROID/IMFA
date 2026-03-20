package com.genius.imfa.common;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.CreativePermission;
import com.genius.imfa.Utility.NetworkConnectionCheck;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SplashScreenActivity extends AppCompatActivity {
    private static final int PERMISSION_ALL = 100;
    private static final String TAG = "SplashScreenActivity";
    Pref pref;
    String loginFlag="1";
    String android_id, refreshedToken;
    String AEMEmployeeID;
    String UserType, SecurityCode, TutorialFlag,LoginFlag,IsModified;;
    private NetworkConnectionCheck connectionCheck;
    private CreativePermission myPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash_screen);
        initView();
        CheckPermission();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
        connectionCheck = new NetworkConnectionCheck(this);
        refreshedToken = "12344";
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.e("log", "initView: "+pref.getLoginFlag());
        myPermission = new CreativePermission(this,PERMISSION_ALL);
    }

    private void CheckPermission() {
        if (!myPermission.hasPermissions()) {
            myPermission.reqPermisions();
        } else {
            setup();
        }
    }

    private void setup(){
        if (connectionCheck.isNetworkAvailable()) {
            showSplashScreen();
        } else {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_ALL) {
            setup();
        }
    }

    private void showSplashScreen() {
        Log.e(TAG, "showSplashScreen: "+pref.getLoginFlag());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pref.getLoginFlag().equals("1")) {
                    JSONObject obj=new JSONObject();
                    try {
                        obj.put("MasterID",encrypt(pref.getMasterId(),SECRET_KEY));
                        obj.put("Password",encrypt(pref.getPassword(),SECRET_KEY));
                        obj.put("IMEI",android_id);
                        obj.put("DeviceID",android_id);
                        obj.put("DeviceType","A");
                        obj.put("SecurityCode",pref.getSecurityCode());
                        login(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }

                /* if (loginFlag.equals("1")){
                    // loginFunction();
                }else {
                    Toast.makeText(SplashScreenActivity.this,"Block by administrator",Toast.LENGTH_LONG).show();
                }*/
            }
        }, 2000);


    }

    private void login(JSONObject jsonObject) {
        /*final ProgressDialog pd = new ProgressDialog(SplashScreenActivity.this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();*/
        AndroidNetworking.post(Api.sLogin)
                .addJSONObjectBody(jsonObject)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        JSONObject job1 = response;
                        Log.e(TAG, "LOGIN: " + job1);
                        //pd.dismiss();
                        String responseText = job1.optString("Response_Message");
                        int Response_Code = job1.optInt("Response_Code");
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
                                Log.d("Master", MasterID);
                                String UserType = obj.optString("UserType");
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
                                String TutorialFlag = obj.optString("TutorialFlag");
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
                                String LoginFlag = "1";//hardcode on sp by dk and it willbe remove .sp name:GHRMSUserAuthenticationWithnDevice_New
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
                            Intent intent = new Intent(SplashScreenActivity.this, UserDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                           /* if (pref.getSecurityCode().equals("1138")){
                                Intent intent = new Intent(SplashScreenActivity.this, UserDashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {

                                //checkBersion();

                                *//*JSONObject object=new JSONObject();
                                try {
                                    object.put("SecurityCode",pref.getSecurityCode());
                                    versionCheck(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }*//*
                            }*/
                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        // boolean _status = job1.getBoolean("status");
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError error) {
                        //pd.dismiss();
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}