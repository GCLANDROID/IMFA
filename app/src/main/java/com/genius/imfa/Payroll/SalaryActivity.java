package com.genius.imfa.Payroll;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.common.UserDashboardActivity;
import com.genius.imfa.Model.SalaryModule;
import com.genius.imfa.Model.SpinnerModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.adapter.SalaryAdapter;
import com.genius.imfa.databinding.ActivitySalaryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SalaryActivity extends AppCompatActivity {
    private static final String TAG = "SalaryActivity";
    ActivitySalaryBinding binding;
    Spinner spYear;
    AlertDialog alertDialog;
    Pref pref;

    String yearid = "";
    String yearName;
    ArrayList<SpinnerModel> modelYearList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<SalaryModule> salaryList = new ArrayList<>();
    SalaryAdapter salaryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_salary);
        binding = ActivitySalaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        btnClick();
        showSearchDialog();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
        binding.rvSalary.setLayoutManager(new LinearLayoutManager(this));
        binding.llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
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
                Intent intent = new Intent(SalaryActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    private void showSearchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SalaryActivity.this, R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_search, null);
        dialogBuilder.setView(dialogView);
        spYear = (Spinner) dialogView.findViewById(R.id.spYear);

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("ddltype",1);
            jsonObject.put("id1",0);
            jsonObject.put("id2",0);
            jsonObject.put("id3",0);
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            setYearItem(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearid = modelYearList.get(position).getItemId();
                yearName=modelYearList.get(position).getItem();
                binding.tvYear.setText(yearName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        LinearLayout llShow = (LinearLayout) dialogView.findViewById(R.id.llShow);
        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("SlNo", 1);
                    jsonObject.put("AEMClientID", pref.getEmpClintId());
                    jsonObject.put("AEMEmployeeID", pref.getEmpId());
                    jsonObject.put("FinancialYear", yearid);
                    jsonObject.put("Month", 0);
                    jsonObject.put("SecurityCode", pref.getSecurityCode());
                    getSalaryList(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
            }
        });
        TextView tvShow=(TextView)dialogView.findViewById(R.id.tvShow);
        if (pref.getLanguage().equals("hi")){
            tvShow.setText("प्रदर्शन");
        }else {
            tvShow.setText("SHOW");
        }



        ImageView imgCancel = (ImageView) dialogView.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    private void setYearItem(JSONObject jsonObject) {
        Log.e(TAG, "setYearItem: "+jsonObject.toString());
        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage("Loading...");
        progressBar.show();
        AndroidNetworking.post(Api.sCommonDDL)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.dismiss();
                        yearList.clear();
                        JSONObject job1 = response;
                        Log.e(TAG, "SALARY_YEAR:" + job1);


                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String YearName = obj.optString("Value");
                                    String YearID = obj.optString("ID");
                                    yearList.add(YearName);
                                    SpinnerModel mainDocModule = new SpinnerModel(YearName, YearID);
                                    modelYearList.add(mainDocModule);
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                        (SalaryActivity.this, android.R.layout.simple_spinner_item,
                                                yearList); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spYear.setAdapter(spinnerArrayAdapter);
                                int index = yearList.indexOf(yearName);
                                Log.d("indexr", String.valueOf(index));
                                spYear.setSelection(index);
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
                        }}
                });
    }

    private void getSalaryList(JSONObject jsonObject) {
        Log.e(TAG, "getSalaryList: "+jsonObject.toString());
        binding.llLoader.setVisibility(View.VISIBLE);
        binding.llMain.setVisibility(View.GONE);
        binding.llNodata.setVisibility(View.GONE);
        binding.llAgain.setVisibility(View.GONE);
        AndroidNetworking.post(Api.sPayslipApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        salaryList.clear();
                        JSONObject job1 = response;
                        Log.e(TAG, "SALARY_LIST: " + job1);
                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            String responseData = job1.optString("Response_Data");
                            Log.e(TAG, "Called: =========");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Log.e(TAG, "LOOP_CALLED: "+i);
                                    JSONObject obj = jsonArray.optJSONObject(i);
                                    String SalMonth = obj.optString("MonthName");
                                    String SalYear = obj.optString("FinancialYear");
                                    String MonthlyNet = obj.optString("MonthlyNet");
                                    String url = obj.optString("PayslipPage");
                                    SalaryModule salaryModule = new SalaryModule(SalYear, SalMonth, MonthlyNet, url);
                                    salaryList.add(salaryModule);
                                }

                                Log.e(TAG, "LIST SIZE: "+salaryList.size());

                                if (salaryList.size() > 0) {
                                    binding.llLoader.setVisibility(View.GONE);
                                    binding.llMain.setVisibility(View.VISIBLE);
                                    binding.llNodata.setVisibility(View.GONE);
                                    binding.llAgain.setVisibility(View.GONE);
                                    setAdapter();
                                } else {
                                    binding.llLoader.setVisibility(View.GONE);
                                    binding.llMain.setVisibility(View.GONE);
                                    binding.llNodata.setVisibility(View.GONE);
                                    binding.llAgain.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // boolean _status = job1.getBoolean("status");
                            // do anything with response
                        }else {
                            binding.llLoader.setVisibility(View.GONE);
                            binding.llMain.setVisibility(View.GONE);
                            binding.llNodata.setVisibility(View.VISIBLE);
                            binding.llAgain.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_LONG).show();
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

    private void setAdapter() {
        salaryAdapter = new SalaryAdapter(salaryList,getApplicationContext());
        binding.rvSalary.setAdapter(salaryAdapter);
    }

    private void login(JSONObject jsonObject) {
        final ProgressDialog pd = new ProgressDialog((Context) SalaryActivity.this);
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


                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });
    }

}