package com.genius.imfa.common;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;

import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.Leave.LeaveApplicationDashboardActivity;
import com.genius.imfa.Model.AttendanceCalenderModel;
import com.genius.imfa.Payroll.PayrollActivity;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.GreetingGenerator;
import com.genius.imfa.Utility.NetworkConnectionCheck;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.TimeDateConverter;
import com.genius.imfa.Utility.Util;
import com.genius.imfa.attendance.AttendanceMarkActivity;
import com.genius.imfa.attendance.AttendanceReportActivity;
import com.genius.imfa.databinding.ActivityUserDashboardBinding;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.haohaohu.autoscrolltextview.IMarqueeListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.naishadhparmar.zcustomcalendar.CustomCalendar;
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener;
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener;
import org.naishadhparmar.zcustomcalendar.Property;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserDashboardActivity extends AppCompatActivity implements OnNavigationButtonClickedListener {
    private static final String TAG = "UserDashboardActivity";
    ActivityUserDashboardBinding binding;
    Pref pref;
    ArrayList<String>presentDays=new ArrayList<>();
    ArrayList<String>leaveDays=new ArrayList<>();
    ArrayList<String>absentDays=new ArrayList<>();
    ArrayList<String>halfday=new ArrayList<>();
    ArrayList<String>halfdayleave=new ArrayList<>();

    ArrayList<String>dateList=new ArrayList<>();

    int y,m;
    ArrayList<AttendanceCalenderModel> itemList = new ArrayList<>();
    JSONArray attendanceArray;
    int date;
    private static final int RC_APP_UPDATE=100;
    private AppUpdateManager mAppUpdateManager;
    private NetworkConnectionCheck connectionCheck;

    String version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_dashboard);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        initView();
        onClick();
        //NotificationAdapter notificationAdapter = new NotificationAdapter();
        //binding.rvNotification.setLayoutManager(new LinearLayoutManager(UserDashboardActivity.this));
        //binding.rvNotification.setAdapter(notificationAdapter);
    }

    private void initView() {
        pref = new Pref(UserDashboardActivity.this);
        connectionCheck = new NetworkConnectionCheck(this);

       // Log.e(TAG, "initView: ====================== "+ FindDocumentInformation.getFileType("data:.pdf;base64,JVBERi0xLjMKJcTl8uXrp"));
       // Log.e(TAG, "initView: base64 ====================== "+ FindDocumentInformation.getBase64Url("data:.pdf;base64,JVBERi0xLjMKJcTl8uXrp"));
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            int verCode = pInfo.versionCode;
            Log.d("sddk", version);
            Log.d("sdkl", String.valueOf(verCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvGreeting.setText(GreetingGenerator.getGreeting());
        }

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            jsonObject.put("EmployeeId",pref.getEmpId());
            getAttendanceMenu(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAppUpdateManager= AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,UserDashboardActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        binding.txtUserName.setText(pref.getEmpName());
        binding.txtLoginTime.setText("Your login time is "+ TimeDateConverter.loginTimeConverter(pref.getloginTime()));
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            // Now you can use version and versionCode as needed
            Log.d("AppVersion", "Version Name: " + version + " Version Code: " + versionCode);
            binding.llDrawerPane.txtAppVersion.setText("Version: "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        y = Calendar.getInstance().get(Calendar.YEAR);
        m = Calendar.getInstance().get(Calendar.MONTH) + 1;

        HashMap<Object, Property> descHashMap = new HashMap<>();

        // Initialize default property
        Property defaultProperty = new Property();

        // Initialize default resource
        defaultProperty.layoutResource = R.layout.default_view;

        // Initialize and assign variable
        defaultProperty.dateTextViewResource = R.id.text_view;

        // Put object and property
        descHashMap.put("default", defaultProperty);

        // for current date
        Property currentProperty = new Property();
        currentProperty.layoutResource = R.layout.wo_view;
        currentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("WO", currentProperty);

        // for present date
        Property presentProperty = new Property();
        presentProperty.layoutResource = R.layout.present_view;
        presentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("P", presentProperty);

        // For absent
        Property absentProperty = new Property();
        absentProperty.layoutResource = R.layout.absent_view;
        absentProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("A", absentProperty);

        //holiday
        Property holidayProperty = new Property();
        holidayProperty.layoutResource = R.layout.holiday_view;
        holidayProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("H", holidayProperty);

        //leave
        Property leaveProperty = new Property();
        leaveProperty.layoutResource = R.layout.leave_view;
        leaveProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("PPL", leaveProperty);
        descHashMap.put("PLC", leaveProperty);
        descHashMap.put("PL", leaveProperty);
        descHashMap.put("COFF", leaveProperty);
        descHashMap.put("LOP", leaveProperty);
        descHashMap.put("SL", leaveProperty);
        descHashMap.put("CL", leaveProperty);
        descHashMap.put("ML", leaveProperty);
        descHashMap.put("PAT", leaveProperty);
        descHashMap.put("PLA", leaveProperty);
        //tvPresent=(TextView)findViewById(R.id.tvPresent);

        Property hdlProperty = new Property();
        hdlProperty.layoutResource = R.layout.hdl_view;
        hdlProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("HDL", hdlProperty);


        Property wcProperty = new Property();
        wcProperty.layoutResource = R.layout.wc_view;
        wcProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("WC", wcProperty);

        Property unapprovedleaveProperty = new Property();
        unapprovedleaveProperty.layoutResource = R.layout.ul_view;
        unapprovedleaveProperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("UL", unapprovedleaveProperty);

        Property hdproperty = new Property();
        hdproperty.layoutResource = R.layout.hd_view;
        hdproperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("HD", hdproperty);

        Property missedproperty = new Property();
        missedproperty.layoutResource = R.layout.missed_punch_view;
        missedproperty.dateTextViewResource = R.id.text_view;
        descHashMap.put("M", missedproperty);

        Property current = new Property();
        current.layoutResource = R.layout.current_view;
        current.dateTextViewResource = R.id.text_view;
        descHashMap.put("C", current);

        binding.customCalendar.setMapDescToProp(descHashMap);
        binding.customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        binding.customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);
        /*lnStatus=(LinearLayout) findViewById(R.id.lnStatus);
        tvDetails=(TextView)findViewById(R.id.tvDetails);
        tvOK=(TextView)findViewById(R.id.tvOK);*/

        binding.tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.lnStatus.setVisibility(View.GONE);
            }
        });

        binding.customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                //Log.e("CAL", "onDateSelected: "+selectedDate.get() );
                String sDate=selectedDate.get(Calendar.DAY_OF_MONTH)
                        +"/" +(selectedDate.get(Calendar.MONTH)+1)
                        +"/" + selectedDate.get(Calendar.YEAR);

                binding.lnStatus.setVisibility(View.GONE);
                String date=Util.changeAnyDateFormat(sDate,"dd/MM/yyyy","dd-MM-yy");
                int pos =dateList.indexOf(date);
                Log.d("position", String.valueOf(pos));
                JSONObject object=attendanceArray.optJSONObject(pos);
                String PunchTiming = object.optString("Punchtime");
                String Status = object.optString("Status").toUpperCase();

                if (Status.equalsIgnoreCase("")) {
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B6B6B6")));
                    binding.tvDetails.setText(date+" : Advance attendance is not available" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));
                } else if (Status.equalsIgnoreCase("P")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F20BCA03")));
                    binding.tvDetails.setText(date + " : " + PunchTiming);
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("A")){
                    Log.e(TAG, "onDateSelected: called ==========");
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F2FA0209")));
                    binding.tvDetails.setText(date + " : "+ PunchTiming + "Absent" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("PPL")
                        || Status.equalsIgnoreCase("PLA")
                        || Status.equalsIgnoreCase("PLC")
                        || Status.equalsIgnoreCase("PL")
                        || Status.equalsIgnoreCase("COFF")
                        || Status.equalsIgnoreCase("LOP")
                        || Status.equalsIgnoreCase("SL")
                        || Status.equalsIgnoreCase("CL")
                        || Status.equalsIgnoreCase("ML")
                        || Status.equalsIgnoreCase("PAT")
                ){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#673AB7")));
                    //binding.tvDetails.setText(date + " : " + PunchTiming );
                    binding.tvDetails.setText(date + " : " + Status);
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));
                } else if (Status.equalsIgnoreCase("HDL")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C09A72")));
                    binding.tvDetails.setText(date + " : " + PunchTiming);
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));
                } else if (Status.equalsIgnoreCase("H")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFED45")));
                    binding.tvDetails.setText(date + " : "+PunchTiming+ " - Holiday" );
                    binding.tvDetails.setTextColor(Color.parseColor("#340885"));
                    binding.tvOK.setTextColor(Color.parseColor("#340885"));

                } else if (Status.equalsIgnoreCase("WO")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1589FF")));
                    binding.tvDetails.setText(date + " : "+PunchTiming + " Weekly Off" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("WC")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F20C5EDA")));
                    binding.tvDetails.setText(date + " : " + PunchTiming );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("M")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AC4782")));
                    binding.tvDetails.setText(date + " : "+ PunchTiming + " Missed Punch" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("HD")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCA61")));
                    binding.tvDetails.setText(date + " : "+PunchTiming + " - Half Day" );
                    binding.tvDetails.setTextColor(Color.parseColor("#340885"));
                    binding.tvOK.setTextColor(Color.parseColor("#340885"));

                } else if (Status.equalsIgnoreCase("UL")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DE2852")));
                    binding.tvDetails.setText(date + " : "+PunchTiming + "Un Approved Leave" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));

                } else if (Status.equalsIgnoreCase("C")){
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#010360")));
                    binding.tvDetails.setText(date + " : "+PunchTiming );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));
                } else {
                    binding.lnStatus.setVisibility(View.VISIBLE);
                    binding.lnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AFAFAF")));
                    binding.tvDetails.setText("Advance Attendance is not available" );
                    binding.tvDetails.setTextColor(Color.parseColor("#F2FFFFFF"));
                    binding.tvOK.setTextColor(Color.parseColor("#F2FFFFFF"));
                }
            }
        });


        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        // Set the animation properties
        animation.setDuration(10000); // 3 seconds
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);

        binding.marqueeTextView.startAnimation(animation);


        JSONObject object=new JSONObject();
        try {
            object.put("SecurityCode",pref.getSecurityCode());
            versionCheck(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a shortcut info builder
       /* ShortcutInfoCompat.Builder shortcutInfoBuilder = new ShortcutInfoCompat.Builder(UserDashboardActivity.this, "shortcut_id")
                .setShortLabel("My App")
                .setIcon(IconCompat.createWithResource(UserDashboardActivity.this, R.mipmap.ic_launcher))
                .setIntent(new Intent(UserDashboardActivity.this, UserDashboardActivity.class)) // Set the intent to launch when the shortcut is tapped
                .setRank(0); // Set the rank to 0 to ensure the shortcut appears at the top

        // Set the badge count
        shortcutInfoBuilder.setLongLabel("5"); // Set the badge count as the long label

        ShortcutInfoCompat shortcutInfo = shortcutInfoBuilder.build();

        ShortcutManagerCompat = ShortcutManagerCompat.setDynamicShortcuts(Collections.singletonList(shortcutInfo));*/
        //shortcutManager.
    }

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        Log.e(TAG, "setBadge: "+launcherClassName);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }


    private void onClick() {
        binding.ivManu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerMenu.open();
            }
        });

        binding.llDrawerPane.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.llDrawerPane.llLeaveApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, LeaveApplicationDashboardActivity.class);
                startActivity(intent);
            }
        });

        binding.llDrawerPane.llHolidayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, HolidayActivity.class);
                startActivity(intent);
            }
        });

        binding.llDrawerPane.llAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkPermissionForFile();
            }
        });

        binding.llDrawerPane.llPayroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, PayrollActivity.class);
                startActivity(intent);
            }
        });

        binding.llDrawerPane.llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDashboardActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        binding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.saveLoginFlag("0");
                Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void currentcalendar(JSONObject jsonObject) {
        Log.e(TAG, "currentcalendar: "+jsonObject.toString());
        presentDays=new ArrayList<>();
        halfday=new ArrayList<>();
        halfdayleave=new ArrayList<>();
        dateList=new ArrayList<>();

        binding.txtPresentDayCount.setText("0");
        binding.txtAbsentCount.setText("0");
        binding.txtOnLeave.setText("0");

        final HashMap<Integer, Object> dateHashmap = new HashMap<>();

        // initialize calendar
        final Calendar calendar = Calendar.getInstance();
        final ProgressDialog pd = new ProgressDialog(UserDashboardActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        pd.setCancelable(false);
        AndroidNetworking.post(Api.sCalendarapi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject job1 = response;
                        Log.e(TAG, "CALENDAR_DATA: "+job1);
                        pd.dismiss();
                        itemList.clear();
                        presentDays.clear();
                        leaveDays.clear();
                        absentDays.clear();
                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            String responseData = job1.optString("Response_Data");

                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                attendanceArray = jsonArray;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.optJSONObject(i);
                                    String sDate = obj.optString("Date");
                                    String Date = Util.changeAnyDateFormat(obj.optString("Date"), "dd-MM-yy", "dd");
                                    try {
                                        date = Integer.parseInt(Date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    String PunchTiming = obj.optString("PunchTiming");
                                    String Day = obj.optString("Day");
                                    String Status = obj.optString("Status");

                                    AttendanceCalenderModel obj2 = new AttendanceCalenderModel();
                                    obj2.setDate(Date);
                                    obj2.setStatus(Status);
                                    obj2.setTime(PunchTiming);
                                    obj2.setDay(Day);
                                    itemList.add(obj2);
                                    dateList.add(sDate);

                                    if (Status.equalsIgnoreCase("P")){
                                        presentDays.add(Day);
                                    }

                                    if (Status.equalsIgnoreCase("PPL")
                                            || Status.equalsIgnoreCase("PLA")
                                            || Status.equalsIgnoreCase("PLC")
                                            || Status.equalsIgnoreCase("PL")
                                            || Status.equalsIgnoreCase("COFF")
                                            || Status.equalsIgnoreCase("LOP")
                                            || Status.equalsIgnoreCase("SL")
                                            || Status.equalsIgnoreCase("CL")
                                            || Status.equalsIgnoreCase("ML")
                                            || Status.equalsIgnoreCase("PAT")
                                    ){
                                        leaveDays.add(Day);
                                        Log.e(TAG, "leaveDays: "+leaveDays.size());
                                    }

                                    if (Status.equalsIgnoreCase("A")){
                                        //absentDays.add(Day);
                                    }


                                  /*  if (Status.equalsIgnoreCase("HD")){
                                        halfday.add(Day);
                                    }

                                    if (Status.equalsIgnoreCase("HDL")){
                                        halfdayleave.add(Day);
                                    }*/
                                    dateHashmap.put(date, Status);
                                }

                                binding.txtPresentDayCount.setText(String.valueOf(presentDays.size()));
                                binding.txtAbsentCount.setText(String.valueOf(absentDays.size()));
                                binding.txtOnLeave.setText(String.valueOf(leaveDays.size()));
                                binding.customCalendar.setDate(calendar, dateHashmap);

                                float halfdaycount=halfday.size();
                                float hdlcount=halfdayleave.size();

                                float halfdayCount=halfdaycount/2;
                                float halfdayleavecount=hdlcount/2;

                                float totalcount=presentDays.size()+halfdayCount+halfdayleavecount;

                                //binding.tvPresent.setText(""+totalcount);
                                //setAdapter();
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
                        if (error.getErrorCode()==401){
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("MasterID",encrypt(pref.getMasterId(),SECRET_KEY));
                                obj.put("Password",encrypt(pref.getPassword(),SECRET_KEY));
                                obj.put("IMEI","0");
                                obj.put("DeviceID","0");
                                obj.put("DeviceType","A");
                                obj.put("SecurityCode",pref.getSecurityCode());
                                //login(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



        JSONObject object = new JSONObject();
        try {
            object.put("EmployeeId",pref.getEmpId());
            object.put("ProductId","0");
            object.put("CompanyID","0");
            object.put("Ishome","0");
            object.put("Operation",1);
            object.put("SecurityCode",pref.getSecurityCode());
            getGetBulletinBoardDetails(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGetBulletinBoardDetails(JSONObject object) {
        Log.e(TAG, "getGetBulletinBoardDetails: "+object.toString());
        final Calendar calendar = Calendar.getInstance();
        final ProgressDialog pd = new ProgressDialog(UserDashboardActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        pd.setCancelable(false);
        AndroidNetworking.post(Api.sGetBulletinBoardDetails)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "DASHBOARD_BULLETIN: "+response.toString());
                        JSONObject job1 = response;
                        pd.dismiss();
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONObject jsonArray = new JSONObject(responseData);
                                String Table = jsonArray.optString("Table");
                                JSONArray array = new JSONArray(Table);
                                String message="";
                                if (array.length() > 0){
                                    for (int i = 0; i < array.length(); i++) {
                                        Log.e(TAG, "onResponse: "+i);
                                        JSONObject obj = array.getJSONObject(i);
                                        if (message.isEmpty()){
                                            message = obj.getString("MessageDesc");
                                        } else {
                                            message += "        "+obj.getString("MessageDesc");
                                        }
                                    }
                                    binding.mainAutoscrollText1.setText(message);
                                    binding.mainAutoscrollText1.setSpeed(7);
                                    //binding.mainAutoscrollText1.startAutoScroll();
                                    binding.mainAutoscrollText1.startScroll();
                                    binding.mainAutoscrollText1.setMarqueeListener(new IMarqueeListener() {

                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onFinish() {
                                            binding.mainAutoscrollText1.startScroll();
                                        }
                                    });
                                    //binding.marqueeTextView.setText(message);
                                    binding.marqueeTextView.setSelected(true);
                                } else {
                                    Log.e(TAG, "onResponse: called 1");
                                    binding.mainAutoscrollText1.setText("No message");
                                    /*nding.mainAutoscrollText1.setSpeed(7);
                                    binding.mainAutoscrollText1.startScroll();
                                    binding.mainAutoscrollText1.setMarqueeListener(new IMarqueeListener() {

                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onFinish() {
                                            binding.mainAutoscrollText1.startScroll();
                                        }
                                    });*/
                                    binding.marqueeTextView.setVisibility(View.VISIBLE);
                                    binding.marqueeTextView.setText("No message");
                                    binding.mainAutoscrollText1.setVisibility(View.GONE);
                                    //binding.marqueeTextView.setText(message);
                                    //binding.marqueeTextView.setSelected(true);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Log.e(TAG, "DASHBOARD_BULLETIN_onError: "+anError.getErrorCode());
                    }
                });
    }

    /*private void setAdapter() {
        AttendanceCalenderAdapter attendanceAdapter = new AttendanceCalenderAdapter(itemList);
        rvItem.setAdapter(attendanceAdapter);
    }*/

    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        Log.e(TAG, "MONTH: "+newMonth.get(Calendar.MONTH));
        Log.e(TAG, "YEAR: "+newMonth.get(Calendar.YEAR));
        switch(newMonth.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                Calendar calendar=Calendar.getInstance();
                calendar.set(newMonth.get(Calendar.YEAR),0,1);

                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject.put("Month",1);
                    jsonObject.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject,calendar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.FEBRUARY:
                Calendar calendar1=Calendar.getInstance();
                calendar1.set(newMonth.get(Calendar.YEAR),1,1);


                JSONObject jsonObject1=new JSONObject();
                try {
                    jsonObject1.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject1.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject1.put("Month",2);
                    jsonObject1.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject1,calendar1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Calendar.MARCH:
                Calendar calendar2=Calendar.getInstance();
                calendar2.set(newMonth.get(Calendar.YEAR),2,1);



                JSONObject jsonObject2=new JSONObject();
                try {
                    jsonObject2.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject2.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject2.put("Month",3);
                    jsonObject2.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject2,calendar2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case  Calendar.APRIL:
                Calendar calendar3=Calendar.getInstance();
                calendar3.set(newMonth.get(Calendar.YEAR),3,1);



                JSONObject jsonObject3=new JSONObject();
                try {
                    jsonObject3.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject3.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject3.put("Month",4);
                    jsonObject3.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject3,calendar3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.MAY:
                Calendar calendar4=Calendar.getInstance();
                calendar4.set(newMonth.get(Calendar.YEAR),4,1);


                JSONObject jsonObject4=new JSONObject();
                try {
                    jsonObject4.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject4.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject4.put("Month",5);
                    jsonObject4.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject4,calendar4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.JUNE:
                Calendar calendar5=Calendar.getInstance();
                calendar5.set(newMonth.get(Calendar.YEAR),5,1);


                JSONObject jsonObject5=new JSONObject();
                try {
                    jsonObject5.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject5.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject5.put("Month",6);
                    jsonObject5.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject5,calendar5);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.JULY:
                Calendar calendar6=Calendar.getInstance();
                calendar6.set(newMonth.get(Calendar.YEAR),6,1);



                JSONObject jsonObject6=new JSONObject();
                try {
                    jsonObject6.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject6.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject6.put("Month",7);
                    jsonObject6.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject6,calendar6);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.AUGUST:
                Calendar calendar7=Calendar.getInstance();
                calendar7.set(newMonth.get(Calendar.YEAR),7,1);



                JSONObject jsonObject7=new JSONObject();
                try {
                    jsonObject7.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject7.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject7.put("Month",8);
                    jsonObject7.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject7,calendar7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.SEPTEMBER:
                Calendar calendar8=Calendar.getInstance();
                calendar8.set(newMonth.get(Calendar.YEAR),8,1);



                JSONObject jsonObject8=new JSONObject();
                try {
                    jsonObject8.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject8.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject8.put("Month",9);
                    jsonObject8.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject8,calendar8);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.OCTOBER:
                Calendar calendar9=Calendar.getInstance();
                calendar9.set(newMonth.get(Calendar.YEAR),9,1);



                JSONObject jsonObject9=new JSONObject();
                try {
                    jsonObject9.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject9.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject9.put("Month",10);
                    jsonObject9.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject9,calendar9);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.NOVEMBER:
                Calendar calendar10=Calendar.getInstance();
                calendar10.set(newMonth.get(Calendar.YEAR),10,1);



                JSONObject jsonObject10=new JSONObject();
                try {
                    jsonObject10.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject10.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject10.put("Month",11);
                    jsonObject10.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject10,calendar10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Calendar.DECEMBER:
                Calendar calendar11=Calendar.getInstance();
                Log.e(TAG, "onNavigationButtonClicked: "+calendar11);
                calendar11.set(newMonth.get(Calendar.YEAR),11,1);


                JSONObject jsonObject11=new JSONObject();
                try {
                    jsonObject11.put("AEMEmployeeId",pref.getEmpId());
                    jsonObject11.put("Year",newMonth.get(Calendar.YEAR));
                    jsonObject11.put("Month",12);
                    jsonObject11.put("SecurityCode",pref.getSecurityCode());
                    currentcalendarForNav(jsonObject11,calendar11);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }


        return arr;
    }

    private void currentcalendarForNav(JSONObject jsonObject,final Calendar calendar) {

        presentDays=new ArrayList<>();
        halfday=new ArrayList<>();
        halfdayleave=new ArrayList<>();
        dateList=new ArrayList<>();

        binding.txtPresentDayCount.setText("0");
        binding.txtAbsentCount.setText("0");
        binding.txtOnLeave.setText("0");

        final HashMap<Integer, Object> dateHashmap = new HashMap<>();

        // initialize calendar

        final ProgressDialog pd = new ProgressDialog(UserDashboardActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        pd.setCancelable(false);
        AndroidNetworking.post(Api.sCalendarapi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        JSONObject job1 = response;
                        Log.e("response12", "@@@@@@" + job1);
                        pd.dismiss();
                        itemList.clear();
                        presentDays.clear();
                        leaveDays.clear();
                        absentDays.clear();

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                attendanceArray=jsonArray;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.optJSONObject(i);
                                    String sDate = obj.optString("Date");
                                    String Date = Util.changeAnyDateFormat(obj.optString("Date"), "dd-MM-yy", "dd");
                                    try {
                                        date = Integer.parseInt(Date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    String PunchTiming = obj.optString("PunchTiming");
                                    String Day = obj.optString("Day");
                                    String Status = obj.optString("Status");


                                    AttendanceCalenderModel obj2 = new AttendanceCalenderModel();
                                    obj2.setDate(Date);
                                    obj2.setStatus(Status);
                                    obj2.setTime(PunchTiming);
                                    obj2.setDay(Day);
                                    itemList.add(obj2);
                                    dateList.add(sDate);
                                    /*if (Status.equalsIgnoreCase("P")||Status.equalsIgnoreCase("WC")){
                                        presentDays.add(Day);
                                    }


                                    if (Status.equalsIgnoreCase("HD")){
                                        halfday.add(Day);
                                    }

                                    if (Status.equalsIgnoreCase("HDL")){
                                        halfdayleave.add(Day);
                                    }*/


                                    if (Status.equalsIgnoreCase("P")){
                                        presentDays.add(Day);
                                    }

                                    if (Status.equalsIgnoreCase("PPL")
                                            || Status.equalsIgnoreCase("PLA")
                                            || Status.equalsIgnoreCase("PLC")
                                            || Status.equalsIgnoreCase("PL")
                                            || Status.equalsIgnoreCase("COFF")
                                            || Status.equalsIgnoreCase("LOP")
                                            || Status.equalsIgnoreCase("SL")
                                            || Status.equalsIgnoreCase("CL")
                                            || Status.equalsIgnoreCase("ML")
                                            || Status.equalsIgnoreCase("PAT")
                                    ){
                                        leaveDays.add(Day);
                                        Log.e(TAG, "leaveDays: "+leaveDays.size());
                                    }

                                    if (Status.equalsIgnoreCase("A")){
                                        //absentDays.add(Day);
                                    }

                                    dateHashmap.put(date, Status);

                                }

                                binding.txtPresentDayCount.setText(String.valueOf(presentDays.size()));
                                binding.txtAbsentCount.setText(String.valueOf(absentDays.size()));
                                binding.txtOnLeave.setText(String.valueOf(leaveDays.size()));
                                binding.customCalendar.setDate(calendar, dateHashmap);

                                float halfdaycount=halfday.size();
                                float hdlcount=halfdayleave.size();

                                float halfdayCount=halfdaycount/2;
                                float halfdayleavecount=hdlcount/2;

                                float totalcount=presentDays.size()+halfdayCount+halfdayleavecount;

                                //tvPresent.setText(""+totalcount);
                                //setAdapter();

                            } catch (Exception e) {
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
                        if (error.getErrorCode()==401){
                            JSONObject obj=new JSONObject();
                            try {
                                obj.put("MasterID",encrypt(pref.getMasterId(),SECRET_KEY));
                                obj.put("Password",encrypt(pref.getPassword(),SECRET_KEY));
                                obj.put("IMEI","0");
                                obj.put("DeviceID","0");
                                obj.put("DeviceType","A");
                                obj.put("SecurityCode",pref.getSecurityCode());
                                //login(obj);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
    }

    /*private void setAdapter() {
        AttendanceCalenderAdapter attendanceAdapter = new AttendanceCalenderAdapter(itemList);
        rvItem.setAdapter(attendanceAdapter);
    }*/


    @Override
    protected void onStop() {
        if (mAppUpdateManager!=null){
            // mAppUpdateManager.registerListener(installStateUpdatedListener);
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==RC_APP_UPDATE && resultCode !=RESULT_OK){

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private InstallStateUpdatedListener installStateUpdatedListener=new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus()== InstallStatus.DOWNLOADED){
                showCompleteUpdate();
            }

        }

        private void showCompleteUpdate() {
            Toast.makeText(UserDashboardActivity.this,"Update Complete",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onResume() {
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,UserDashboardActivity.this,RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AEMEmployeeId",pref.getEmpId());
            jsonObject.put("Year",y);
            jsonObject.put("Month",m);
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            currentcalendar(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onResume();


    }


    private void versionCheck(JSONObject object) {
        final ProgressDialog pd=new ProgressDialog(UserDashboardActivity.this);
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

                                } else {


                                    if (AndriodAutoUpdateStatus == 1){
                                        Intent intent = new Intent(UserDashboardActivity.this, UpdateActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {

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


    public void getAttendanceMenu(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveAllDetails: called: "+jsonObject);
        ProgressDialog progressDialog=new ProgressDialog(UserDashboardActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        AndroidNetworking.post(Api.sGetAttendanceMenu)
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
                            binding.llDrawerPane.llAttendance.setVisibility(View.VISIBLE);
                        }else {
                            binding.llDrawerPane.llAttendance.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();

                    }
                });
    }


    private void checkPermissionForFile() {
        Dexter.withContext(UserDashboardActivity.this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Log.e("onPermissionsGranted", "Called");
                            if (connectionCheck.isGPSEnabled()) {
                                Intent intent = new Intent(UserDashboardActivity.this, AttendanceReportActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(UserDashboardActivity.this, "Please turn on your GPS Connection", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(UserDashboardActivity.this, "Permissions are required to perform app functionality.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}