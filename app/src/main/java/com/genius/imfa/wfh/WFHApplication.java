package com.genius.imfa.wfh;


import static com.genius.imfa.Leave.fragment.OtherApplicationFragment.getRealPath;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.Activity.AndroidXCameraActivity;
import com.genius.imfa.Leave.LeaveApplicationActivity;
import com.genius.imfa.Model.CompOffDetailsModel;
import com.genius.imfa.Model.DayBreakUpModel;
import com.genius.imfa.Model.LeaveBalanceDetailsModel;
import com.genius.imfa.Model.PrevieModel;
import com.genius.imfa.Model.SpinnerModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.FileToBase64Converter;
import com.genius.imfa.Utility.FindDocumentInformation;
import com.genius.imfa.Utility.ImageUtils;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.TimeDateConverter;
import com.genius.imfa.adapter.CompOffAdapter;
import com.genius.imfa.adapter.DayBreakUpAdapter;
import com.genius.imfa.adapter.LeaveBalanceDetailsAdapter;
import com.genius.imfa.adapter.PreviewAdapter;
import com.genius.imfa.databinding.FragmentOtherApplicationBinding;
import com.genius.imfa.databinding.FragmentWfhBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WFHApplication extends Fragment {
    private static final String TAG = "ApplicationFragment";
    FragmentWfhBinding binding;
    View v;
    Pref pref;
    ArrayList<String>daymodeList=new ArrayList<>();
    String startDate="";
    int strtDate;
    String endDate="";
    AlertDialog al1;
    String daymode;
    AlertDialog alerDialog1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWfhBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView(){
        pref=new Pref(getContext());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            jsonObject.put("CompanyID",pref.getEmpClintId());
            jsonObject.put("EmployeeId",pref.getEmpId());
            getWFHDetails(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.llStrtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStrtDatePicker();
            }
        });


        binding.llEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startDate.equals("")){
                    showendDatePicker();
                }else {
                    Toast.makeText(getContext(),"Please Select Start Date",Toast.LENGTH_LONG).show();
                }

            }
        });
        binding.spDayMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               daymode= String.valueOf(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.llSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startDate.equals("")){
                    if (!endDate.equals("")){
                        if (binding.etWorkPlace.getText().toString().replaceAll(" ","").length()>0){
                            if (binding.etReason.getText().toString().replaceAll(" ","").length()>0){
                                JSONObject jsonObject1=new JSONObject();
                                try {
                                    jsonObject1.put("SecurityCode",pref.getSecurityCode());
                                    jsonObject1.put("CompanyID",pref.getEmpClintId());
                                    jsonObject1.put("EmployeeId",pref.getEmpId());
                                    jsonObject1.put("Halfmode",daymode);
                                    jsonObject1.put("StartDate",startDate);
                                    jsonObject1.put("EndDate",endDate);
                                    jsonObject1.put("WorkPlace",binding.etWorkPlace.getText().toString());
                                    jsonObject1.put("Purpose",binding.etReason.getText().toString());
                                    jsonObject1.put("Accessories","");
                                    jsonObject1.put("createdby",pref.getEmpId());
                                    jsonObject1.put("AID","");
                                    postWFH(jsonObject1);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                Toast.makeText(getContext(),"Please Enter Reason",Toast.LENGTH_LONG).show();
                            }

                        }else {
                            Toast.makeText(getContext(),"Please Enter Work Place",Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(getContext(),"Please Select End Date",Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getContext(),"Please Select Start Date",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void getWFHDetails(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveAllDetails: called: "+jsonObject);
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        AndroidNetworking.post(Api.sGetwfhApplicationDetailsDetails)
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
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {


                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONObject jsonArray=new JSONObject(responseData);
                                String Table1=jsonArray.optString("Table1");
                                Log.e(TAG, "onResponse: Table1: "+Table1);
                                daymodeList.add("Full Day");
                                daymodeList.add("Half Day (First Half)");
                                daymodeList.add("Half Day (Second Half)");
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                        (getContext(), android.R.layout.simple_spinner_item,
                                                daymodeList); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spDayMode.setAdapter(spinnerArrayAdapter);

                                JSONArray approverArray = new JSONArray(Table1);

                                JSONObject approverObject = approverArray.optJSONObject(0);
                                String ApproverName = approverObject.optString("ApproverName");


                                String Table=jsonArray.optString("Table");
                                JSONArray balanceArray = new JSONArray(Table);
                                for (int i = 0; i < balanceArray.length(); i++) {
                                    JSONObject balanceObject = balanceArray.optJSONObject(i);
                                    String TotalApplication = balanceObject.optString("TotalApplication");
                                    binding.tvWFHOpening.setText(TotalApplication);
                                    String ApproveApplication = balanceObject.optString("ApproveApplication");
                                    binding.tvWFHTaken.setText(ApproveApplication);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // boolean _status = job1.getBoolean("status");
                            // do anything with response
                        } else {

                        }
                    }

                    @Override
                    public void onError(ANError error) {

                    }
                });
    }


    private void showStrtDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        strtDate = dayOfMonth + monthOfYear + year;
                        int month = (monthOfYear + 1);
                        startDate = dayOfMonth + "-" + month + "-" + year;
                        binding.tvStrtDate.setText(TimeDateConverter.convert_Date_DD_MM_YYYY_To_dd_MMM_yyyy(startDate));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();

    }


    private void showendDatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int enddate = dayOfMonth + monthOfYear + year;
                        int month = (monthOfYear + 1);
                        endDate = dayOfMonth + "-" + month + "-" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date strDate = null;
                        try {
                            strDate = sdf.parse(startDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        Date striDate = null;
                        try {
                            striDate = df.parse(endDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (striDate.getTime() > strDate.getTime() ||striDate.getTime() == strDate.getTime()) {
                            binding.tvEndDate.setText(TimeDateConverter.convert_Date_DD_MM_YYYY_To_dd_MMM_yyyy(endDate));
                        }else {
                            showErrorDialog("End date should not before than Start date");
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();
    }

    private void showErrorDialog(String text) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.error_ayput, null);
        dialogBuilder.setView(dialogView);
        TextView tvError = (TextView) dialogView.findViewById(R.id.tvError);
        tvError.setText(text);
        ImageView imgCancel = (ImageView) dialogView.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.dismiss();
                binding.tvEndDate.setText("");
            }
        });

        al1 = dialogBuilder.create();
        al1.setCancelable(false);
        Window window = al1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        al1.show();
    }


    public void postWFH(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveAllDetails: called: "+jsonObject);
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        AndroidNetworking.post(Api.sSavewfhDetails)
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
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code==101){
                            successAlert();
                        }else {
                            Toast.makeText(getContext(),Response_Message,Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {

                    }
                });
    }


    private void successAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_success, null);
        dialogBuilder.setView(dialogView);
        TextView tvInvalidDate = (TextView) dialogView.findViewById(R.id.tvSuccess);

        tvInvalidDate.setText("WFH has been successfully applied from "+TimeDateConverter.convert_Date_DD_MM_YYYY_To_dd_MMM_yyyy(startDate)+" to "+TimeDateConverter.convert_Date_DD_MM_YYYY_To_dd_MMM_yyyy(endDate));



        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialog1.dismiss();
                ((WFHActivity) getContext()).loadDetailsFragment();
            }
        });

        alerDialog1 = dialogBuilder.create();
        alerDialog1.setCancelable(true);
        Window window = alerDialog1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alerDialog1.show();
    }



}
