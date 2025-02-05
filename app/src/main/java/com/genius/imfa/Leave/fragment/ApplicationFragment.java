package com.genius.imfa.Leave.fragment;


import static com.genius.imfa.Leave.fragment.OtherApplicationFragment.getRealPath;
import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.developers.imagezipper.ImageZipper;
import com.genius.imfa.Model.EncashmentItemModel;
import com.genius.imfa.Utility.FileUtils;
import com.genius.imfa.adapter.LeaveEncashmentAdapter;
import com.genius.imfa.common.AndroidXCameraActivity;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationFragment extends Fragment {
    private static final String TAG = "ApplicationFragment";

    View v;
    RecyclerView rvItem;
    ArrayList<LeaveBalanceDetailsModel> itemList = new ArrayList<>();
    ArrayList<EncashmentItemModel> encashItemList = new ArrayList<>();
    TextView tvRequested, tvApporved, tvRejected, tvPending;
    LinearLayout llLoader, llPending, llRejected, llApproved, llRequested;
    Pref pref;
    TextView tvEmpName, tvApproverName;
    Spinner spRefName, spLeaveType, spLeaveMode,spPayableYear,spPayableMonth;
    ArrayList<String> refList = new ArrayList<>();
    ArrayList<SpinnerModel> mRefList = new ArrayList<>();
    ArrayList<String> leaveTypeList = new ArrayList<>();
    ArrayList<SpinnerModel> mLeaveTypeList = new ArrayList<>();
    ArrayList<String> leaveMode = new ArrayList<>();
    ArrayList<SpinnerModel> mLeaveMode = new ArrayList<>();
    ProgressDialog pd;
    LinearLayout llStrtDate;
    TextView tvStrtDate;
    LinearLayout llEndDate;
    TextView tvEndDate;
    AlertDialog al1, alert1, alert2, alert3, alert4, alert6;
    int strtDate;
    ArrayList<String> applicantList = new ArrayList<>();
    ArrayList<SpinnerModel> mApplicantList = new ArrayList<>();
    String applicantId;
    String appid = "", applicantName;
    String typeId = "";
    ImageView imgEndDay, imgStrtDay;
    String startDate, endDate, showEndDate;
    String leaveModeId = "";
    RecyclerView rvBrkupItem;
    ArrayList<DayBreakUpModel> dayBreakupList = new ArrayList<>();
    ArrayList<String> typeIdList = new ArrayList<>();
    ArrayList<String> availdList = new ArrayList<>();
    ArrayList<String> typeAvaild = new ArrayList<>();
    String typeAvailable, preViewResponse;
    DayBreakUpAdapter dayAdapter;
    ArrayList<String> dayBreakupListDetails = new ArrayList<>();
    ArrayList<String> halfdetails = new ArrayList<>();
    ArrayList<String> compOffListDetails = new ArrayList<>();
    String dayBreakUpDetails;
    String compOffDetails="";
    LinearLayout llPreview;
    EditText etReason;
    LinearLayout llChoose;
    ImageView imgPic;
    Uri imageUri;
    String encodedImage;
    File file;
    private static final int CAMERA_REQUEST = 1;
    private static final int PDF_REQUEST = 2;
    int attachmentFlag = 0;
    RecyclerView rvPreviewItem;
    ArrayList<PrevieModel> previewItem = new ArrayList<>();
    String leaveType;
    LinearLayout llShow;
    TextView tvAllApplication, tvCancel, tvApproval;
    File pdffile;
    String LeaveValue;
    ProgressDialog pg;
    String category;
    AlertDialog alerDialog1;
    String stringFile = "";
    TextView tvRequestedName, tvApprovedName, tvRejectedName, tvPendingName, tvLeaveTypeName, tvLeaveModeName, tvContactName, tvStartDateName,
            tvEndDateName, tvReasonName, tvDocName, tvPreviewName,txtPdfUrl;
    String color;
    TextView tvBalance, tvDetail, txtEncashApproverName,txtEcashmentDuringYear;
    String hCode;
    AlertDialog alert5;
    ArrayList<CompOffDetailsModel> compOffList = new ArrayList<>();
    RecyclerView rvCompOffItem,rvEncashment;
    CompOffAdapter compOffAdapter;
    LinearLayout lnBalance,lnDocument;

    RadioGroup radioGroup;
    RadioButton rbLeaveApplication;
    RadioButton rbLeaveEncashment;
    LinearLayout llLeaveApplication,llLeaveEncashment;
    EditText etPlaNumber,etPlcNumber,etPlNumber, etTotalNumberOfEncasement;
    int totalEncasementCount =0, totalEncasementValue = 0;
    int PlaElementCount,PlcElementCount;
    int globlePlaValue=0,globlePlcValue=0;
    ArrayList<String> encasementYearList = new ArrayList<>();
    ArrayList<String> encasementMonthList = new ArrayList<>();
    LinearLayout llEncasmentSubmit;
    String LeaveTypeId_1="0",LeaveTypeId_2="0",EncashValue_1="0",EncashValue_2="0",TotalEncashValue="0";
    String enCashPLALeaveId ="0", enCashPLCLeaveId ="0",enCashPLLeaveId="0";
    TextView tvImportantPoint;
    LinearLayout llPLC,llPLA,llPL;
    String realPath,fileType;
    File compressedImageFile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_application, container, false);
        initView();
        onClick();
        return v;
    }

    @SuppressLint("ResourceType")
    private void initView() {
        lnBalance=(LinearLayout)v.findViewById(R.id.lnBalance);
        lnDocument=(LinearLayout)v.findViewById(R.id.lnDocument);
        rvItem = (RecyclerView) v.findViewById(R.id.rvItem);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvItem.setLayoutManager(gridLayoutManager);
        tvRequested = (TextView) v.findViewById(R.id.tvRequested);
        tvApporved = (TextView) v.findViewById(R.id.tvApporved);
        tvRejected = (TextView) v.findViewById(R.id.tvRejected);
        tvPending = (TextView) v.findViewById(R.id.tvPending);
        txtEncashApproverName = (TextView) v.findViewById(R.id.txtEncashApproverName);
        txtEcashmentDuringYear = (TextView) v.findViewById(R.id.txtEcashmentDuringYear);
        llLoader = (LinearLayout) v.findViewById(R.id.llLoader);
        llPending = (LinearLayout) v.findViewById(R.id.llPending);
        llRejected = (LinearLayout) v.findViewById(R.id.llRejected);
        llApproved = (LinearLayout) v.findViewById(R.id.llApproved);
        llRequested = (LinearLayout) v.findViewById(R.id.llRequested);
        llLeaveApplication = (LinearLayout) v.findViewById(R.id.llLeaveApplication);
        llLeaveEncashment = (LinearLayout) v.findViewById(R.id.llLeaveEncashment);
        llEncasmentSubmit = (LinearLayout) v.findViewById(R.id.llEncasmentSubmit);
        tvImportantPoint = (TextView) v.findViewById(R.id.tvImportantPoint);
        rvEncashment = (RecyclerView) v.findViewById(R.id.rvEncashment);
        rvEncashment.setLayoutManager(new LinearLayoutManager(getContext()));
        radioGroup = v.findViewById(R.id.radioGroup);
        rbLeaveApplication = v.findViewById(R.id.rbLeaveApplication);
        rbLeaveEncashment = v.findViewById(R.id.rbLeaveEncashment);
        etPlaNumber = v.findViewById(R.id.etPlaNumber);
        etPlcNumber = v.findViewById(R.id.etPlcNumber);
        etPlNumber = v.findViewById(R.id.etPlNumber);
        etTotalNumberOfEncasement = v.findViewById(R.id.etTotalNumberOfEncasement);
        llPLC = v.findViewById(R.id.llPLC);
        llPLA = v.findViewById(R.id.llPLA);
        llPL = v.findViewById(R.id.llPL);
        rbLeaveApplication.setChecked(true);
        pref = new Pref(getContext());
        if (pref.getSecurityCode().equals("1167")){
            lnBalance.setVisibility(View.GONE);
            lnDocument.setVisibility(View.GONE);
        }else {
            lnBalance.setVisibility(View.VISIBLE);
            lnDocument.setVisibility(View.VISIBLE);
        }
        /*JSONObject object=new JSONObject();
        try {
            object.put("AEMEmployeeID",pref.getEmpId());
            object.put("CompanyID",pref.getEmpClintId());
            object.put("SecurityCode",pref.getSecurityCode());
            getApproverOrNot(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        applicantId = pref.getEmpId();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CompanyID",pref.getEmpClintId());
            jsonObject.put("EmployeeID",pref.getEmpId());
            jsonObject.put("ApproverID",pref.getEmpId());
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            getLeaveAllDetails(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvEmpName = (TextView) v.findViewById(R.id.tvEmpName);

        tvApproverName = (TextView) v.findViewById(R.id.tvApproverName);
        spRefName = (Spinner) v.findViewById(R.id.spRefName);
        spLeaveType = (Spinner) v.findViewById(R.id.spLeaveType);
        spLeaveMode = (Spinner) v.findViewById(R.id.spLeaveMode);
        pd = new ProgressDialog(getActivity());
        llStrtDate = (LinearLayout) v.findViewById(R.id.llStrtDate);
        tvStrtDate = (TextView) v.findViewById(R.id.tvStrtDate);
        llEndDate = (LinearLayout) v.findViewById(R.id.llEndDate);
        tvEndDate = (TextView) v.findViewById(R.id.tvEndDate);
        imgEndDay = (ImageView) v.findViewById(R.id.imgEndDay);
        imgStrtDay = (ImageView) v.findViewById(R.id.imgStrtDay);
        spRefName = (Spinner) v.findViewById(R.id.spRefName);
        llPreview = (LinearLayout) v.findViewById(R.id.llPreview);
        etReason = (EditText) v.findViewById(R.id.etReason);
        llChoose = (LinearLayout) v.findViewById(R.id.llChoose);
        imgPic = (ImageView) v.findViewById(R.id.imgPic);
        llShow = (LinearLayout) v.findViewById(R.id.llShow);

        tvAllApplication = (TextView) v.findViewById(R.id.tvAllApplication);
        tvCancel = (TextView) v.findViewById(R.id.tvCancel);
        tvApproval = (TextView) v.findViewById(R.id.tvApproval);
        txtPdfUrl = (TextView) v.findViewById(R.id.txtPdfUrl);


        pg = new ProgressDialog(getContext());
        pg.setMessage("Loading..");
        pg.setCancelable(false);


        color = "<font color='#EE0000'>*</font>";
        /*String gender = "Gender";
        tvGenderTitle.setText(Html.fromHtml(gender + color));*/

        tvRequestedName = (TextView) v.findViewById(R.id.tvRequestedName);
        tvApprovedName = (TextView) v.findViewById(R.id.tvApprovedName);
        tvRejectedName = (TextView) v.findViewById(R.id.tvRejectedName);
        tvPendingName = (TextView) v.findViewById(R.id.tvPendingName);
        tvLeaveTypeName = (TextView) v.findViewById(R.id.tvLeaveTypeName);
        tvLeaveModeName = (TextView) v.findViewById(R.id.tvLeaveModeName);
        tvContactName = (TextView) v.findViewById(R.id.tvContactName);
        tvStartDateName = (TextView) v.findViewById(R.id.tvStartDateName);
        tvEndDateName = (TextView) v.findViewById(R.id.tvEndDateName);
        tvReasonName = (TextView) v.findViewById(R.id.tvReasonName);
        tvDocName = (TextView) v.findViewById(R.id.tvDocName);
        tvPreviewName = (TextView) v.findViewById(R.id.tvPreviewName);
        tvBalance = (TextView) v.findViewById(R.id.tvBalance);
        tvDetail = (TextView) v.findViewById(R.id.tvDetail);

        spPayableYear = (Spinner) v.findViewById(R.id.spPayableYear);
        spPayableMonth = (Spinner) v.findViewById(R.id.spPayableMonth);

        if (pref.getLanguage().equals("hi")) {
            tvRequestedName.setText("अनुरोध किया");
            tvApprovedName.setText("मंजूर की");
            tvRejectedName.setText("अस्वीकृत");
            tvLeaveModeName.setText("अपूर्ण");
            tvContactName.setText("आपातकालीन संपर्क");
            tvStartDateName.setText(Html.fromHtml("आरंभ करने की तिथि" + color));
            tvEndDateName.setText(Html.fromHtml("अंतिम तिथि" + color));
            tvReasonName.setText(Html.fromHtml("कारण" + color));
            tvDocName.setText("दस्तावेज़ अपलोड करें");
            tvLeaveTypeName.setText(Html.fromHtml("प्रकार" + color));
            tvLeaveModeName.setText(Html.fromHtml("मोड" + color));
            tvBalance.setText("बकाया छुट्टियां");
            tvDetail.setText("छुट्टी का अनुरोध विवरण");
            tvPreviewName.setText("पूर्वावलोकन");
        } else {
            tvRequestedName.setText("Applied");
            tvApprovedName.setText("Approved");
            tvRejectedName.setText("Rejected");
            tvLeaveModeName.setText("Pending");
            tvContactName.setText("Emergency Contact");
            tvStartDateName.setText(Html.fromHtml("From date" + color));
            tvEndDateName.setText(Html.fromHtml("To date" + color));
            tvReasonName.setText(Html.fromHtml("Reason" + color));
            tvDocName.setText("Upload document");
            tvLeaveTypeName.setText(Html.fromHtml("Leave Type" + color));
            tvLeaveModeName.setText(Html.fromHtml("Mode" + color));
            tvBalance.setText("Leave balance details");
            tvDetail.setText("Leave application details");
            //tvDetail.setText("Leave request details");
            tvPreviewName.setText("PREVIEW");
        }

        encasementYearList.add("2024");
        encasementYearList.add("2025");
        encasementYearList.add("2026");
        encasementYearList.add("2027");
        encasementYearList.add("2028");
        encasementYearList.add("2029");
        encasementYearList.add("2030");

        encasementMonthList.add("January");
        encasementMonthList.add("February");
        encasementMonthList.add("March");
        encasementMonthList.add("May");
        encasementMonthList.add("June");
        encasementMonthList.add("July");
        encasementMonthList.add("August");
        encasementMonthList.add("September");
        encasementMonthList.add("October");
        encasementMonthList.add("November");
        encasementMonthList.add("December");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        encasementYearList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPayableYear.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinnerEncaseMonthAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        encasementMonthList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPayableMonth.setAdapter(spinnerEncaseMonthAdapter);
    }

    private void onClick() {
        spLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String lId = mLeaveTypeList.get(position).getItemId();

                    leaveType = mLeaveTypeList.get(position).getItem().replaceAll("\\s+", "%20");
                    String[] sep = lId.split("_");
                    typeId = sep[0];
                    category = sep[1];
                    if (pref.getLanguage().equals("hi")) {
                        getHindiMode();
                    } else {
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("CompanyID",pref.getEmpClintId());
                            jsonObject.put("EmployeeID",applicantId);
                            jsonObject.put("LeaveTypeID",typeId);
                            jsonObject.put("SecurityCode",pref.getSecurityCode());
                            getLeaveMode(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("typeId", typeId);
                    Log.e("category", category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spLeaveMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leaveModeId = mLeaveMode.get(i).getItemId();
                Log.d("leaveModeId",leaveModeId);
                tvEndDate.setText("");
                endDate="";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgStrtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!typeId.equals("")) {
                    showStrtDatePicker();
                } else {
                    Toast.makeText(getContext(), "Please select Leave type", Toast.LENGTH_LONG).show();
                }
            }
        });

        imgEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStrtDate.getText().toString().equals("")) {
                    showendDatePicker();
                } else {
                    showErrorDialog("Please select Start Date");
                }
            }
        });
        etReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etReason.getText().toString().length() > 3) {
                    llPreview.setVisibility(View.VISIBLE);
                } else {
                    llPreview.setVisibility(View.GONE);
                }
            }
        });

        llPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayBreakupListDetails != null) {
                    if (!tvEndDate.getText().toString().equals("")) {
                        //preView();
                        JSONObject object = new JSONObject();
                        try {
                            object.put("CompanyID", pref.getEmpClintId());
                            object.put("EmployeeID", applicantId);
                            object.put("StartDate", startDate);
                            object.put("endDate", endDate);
                            object.put("LeaveTypeID", typeId);
                            object.put("LeaveMode", leaveModeId);
                            object.put("StrAvailableBalance", typeAvailable);
                            object.put("StrDayBreakUp", dayBreakUpDetails);
                            object.put("IsAttachment", attachmentFlag);
                            object.put("SecurityCode", pref.getSecurityCode());
                            preView(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getContext(),"End date not selected",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "please select daily break up details", Toast.LENGTH_LONG).show();
                }
            }
        });

        llChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionForFile();
                //showChooseFileDialog();
            }
        });



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbLeaveApplication.isChecked()){
                    Log.e(TAG, "onCheckedChanged: 1");
                    llLeaveApplication.setVisibility(View.VISIBLE);
                    llLeaveEncashment.setVisibility(View.GONE);
                    clearDataForEncashment();

                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("ApproverID",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        getLeaveAllDetails(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (rbLeaveEncashment.isChecked()){
                    Log.e(TAG, "onCheckedChanged: 2");

                    clearDataForLeaveApplication();

                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("ApproverID",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        getEncashmentDetails(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        etPlaNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty() && etPlcNumber.getText().toString().isEmpty()){
                    etTotalNumberOfEncasement.setText("");
                } else if (s.toString().trim().isEmpty() && !etPlcNumber.getText().toString().isEmpty()){
                    totalEncasementValue =  Integer.parseInt(etPlcNumber.getText().toString());
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                }else if (!s.toString().trim().isEmpty() && !etPlcNumber.getText().toString().isEmpty()){
                    totalEncasementValue = Integer.parseInt(s.toString().trim()) + Integer.parseInt(etPlcNumber.getText().toString());
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                } else if (!s.toString().trim().isEmpty()){
                    totalEncasementValue = Integer.parseInt(s.toString().trim()) + 0;
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                }
            }
        });

        etPlcNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty() && etPlaNumber.getText().toString().isEmpty()){
                    etTotalNumberOfEncasement.setText("");
                } else if (s.toString().trim().isEmpty() && !etPlaNumber.getText().toString().isEmpty()){
                    totalEncasementValue =  Integer.parseInt(etPlaNumber.getText().toString());
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                }else if (!s.toString().trim().isEmpty() && !etPlaNumber.getText().toString().isEmpty()){
                    totalEncasementValue = Integer.parseInt(s.toString().trim()) + Integer.parseInt(etPlaNumber.getText().toString());
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                } else if (!s.toString().trim().isEmpty()){
                    totalEncasementValue = Integer.parseInt(s.toString().trim()) + 0;
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                }
            }
        });

        etPlNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()){
                    etTotalNumberOfEncasement.setText("");
                } else {
                    totalEncasementValue = Integer.parseInt(s.toString().trim()) + 0;
                    etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
                }
            }
        });

        llEncasmentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etPlaNumber.getText().toString().trim().isEmpty() && !etPlcNumber.getText().toString().trim().isEmpty()){
                    Log.e(TAG, "onClick: called PLA + PLC");
                    LeaveTypeId_1 = enCashPLALeaveId;
                    LeaveTypeId_2 = enCashPLCLeaveId;
                    EncashValue_1 = etPlaNumber.getText().toString().trim();
                    EncashValue_2 = etPlcNumber.getText().toString().trim();
                    TotalEncashValue = etTotalNumberOfEncasement.getText().toString().trim();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("LeaveTypeId_1",LeaveTypeId_1);
                        jsonObject.put("LeaveTypeId_2",LeaveTypeId_2);
                        jsonObject.put("EncashValue_1",EncashValue_1);
                        jsonObject.put("EncashValue_2",EncashValue_2);
                        jsonObject.put("TotalEncashValue",TotalEncashValue);
                        jsonObject.put("ApprovedBy",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        saveEncashment(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (!etPlaNumber.getText().toString().trim().isEmpty() && etPlcNumber.getText().toString().trim().isEmpty()){
                    Log.e(TAG, "onClick: called PLA");
                    LeaveTypeId_1 = enCashPLALeaveId;
                    LeaveTypeId_2 = "0";
                    EncashValue_1 = etPlaNumber.getText().toString().trim();
                    EncashValue_2 = "0";
                    TotalEncashValue = etTotalNumberOfEncasement.getText().toString().trim();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("LeaveTypeId_1",LeaveTypeId_1);
                        jsonObject.put("LeaveTypeId_2",LeaveTypeId_2);
                        jsonObject.put("EncashValue_1",EncashValue_1);
                        jsonObject.put("EncashValue_2",EncashValue_2);
                        jsonObject.put("TotalEncashValue",TotalEncashValue);
                        jsonObject.put("ApprovedBy",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        saveEncashment(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (etPlaNumber.getText().toString().trim().isEmpty() && !etPlcNumber.getText().toString().trim().isEmpty()){
                    Log.e(TAG, "onClick: called PLC");
                    LeaveTypeId_1 = enCashPLCLeaveId;
                    LeaveTypeId_2 = "0";
                    EncashValue_1 = etPlcNumber.getText().toString().trim();
                    EncashValue_2 = "0";
                    TotalEncashValue = etTotalNumberOfEncasement.getText().toString().trim();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("LeaveTypeId_1",LeaveTypeId_1);
                        jsonObject.put("LeaveTypeId_2",LeaveTypeId_2);
                        jsonObject.put("EncashValue_1",EncashValue_1);
                        jsonObject.put("EncashValue_2",EncashValue_2);
                        jsonObject.put("TotalEncashValue",TotalEncashValue);
                        jsonObject.put("ApprovedBy",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        saveEncashment(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (!etPlNumber.getText().toString().trim().isEmpty()) {
                    Log.e(TAG, "onClick: called PL");
                    LeaveTypeId_1 = enCashPLLeaveId;
                    LeaveTypeId_2 = "0";
                    EncashValue_1 = etPlNumber.getText().toString().trim();
                    EncashValue_2 = "0";
                    TotalEncashValue = etTotalNumberOfEncasement.getText().toString().trim();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("LeaveTypeId_1",LeaveTypeId_1);
                        jsonObject.put("LeaveTypeId_2",LeaveTypeId_2);
                        jsonObject.put("EncashValue_1",EncashValue_1);
                        jsonObject.put("EncashValue_2",EncashValue_2);
                        jsonObject.put("TotalEncashValue",TotalEncashValue);
                        jsonObject.put("ApprovedBy",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        Log.e(TAG, "onClick: "+jsonObject);
                        //saveEncashment(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tvImportantPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImportantPoint();
            }
        });
    }

    private void openImportantPoint() {
        Dialog dialogView = new Dialog(getActivity(),R.style.CustomDialogNew2);
        dialogView.setContentView(R.layout.importent_point_layout);
        dialogView.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogView.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogView.setCancelable(false);
        TextView tvOk = dialogView.findViewById(R.id.tvOk);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
            }
        });

        dialogView.show();
    }

    private void clearDataForEncashment() {
        etPlaNumber.setText("");
        etPlcNumber.setText("");
        etTotalNumberOfEncasement.setText("");
        LeaveTypeId_1="0";
        LeaveTypeId_2="0";
        EncashValue_1="0";
        EncashValue_2="0";
        TotalEncashValue="0";
        enCashPLALeaveId ="0";
        enCashPLCLeaveId ="0";
    }

    private void clearDataForLeaveApplication() {
        llLeaveApplication.setVisibility(View.GONE);
        llLeaveEncashment.setVisibility(View.VISIBLE);
        leaveMode.clear();
        startDate="";
        endDate = "";
        etReason.setText("");
        tvStrtDate.setText("");
        tvEndDate.setText("");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        leaveMode); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLeaveMode.setAdapter(spinnerArrayAdapter);
    }

    private void saveEncashment(JSONObject object) {
        Log.e(TAG, "saveEncashment: "+object);
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        AndroidNetworking.post(Api.sSaveEncashmentDetails)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "SAVE_ENCASHMENT: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message = job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            successAlertForEncashment();
                        } else {
                            showErrorEncashDialog(Response_Message);
                            //Toast.makeText(getContext(),Response_Message,Toast.LENGTH_LONG).show();
                            //alert3.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Log.e(TAG, "SAVE_ENCASHMENT_anError: "+anError);
                    }
                });
    }

    private void showErrorEncashDialog(String responseMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.error_layout_2, null);
        dialogBuilder.setView(dialogView);
        TextView tvError = (TextView) dialogView.findViewById(R.id.tvError);
        tvError.setText(responseMessage);
        ImageView imgCancel = (ImageView) dialogView.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.dismiss();
                tvEndDate.setText("");
            }
        });

        al1 = dialogBuilder.create();
        al1.setCancelable(false);
        Window window = al1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        al1.show();
    }

    private void getEncashmentDetails(JSONObject jsonObject) {
        Log.e(TAG, "getEncashmentDetails: "+jsonObject.toString());
        llLoader.setVisibility(View.VISIBLE);

        AndroidNetworking.post(Api.sGetLeaveApplicationAllDetailsEncash)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Encashment_details: "+response.toString());
                        llLoader.setVisibility(View.GONE);
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {

                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONObject jsonArray = new JSONObject(responseData);

                                String Table1=jsonArray.optString("Table1");
                                Log.e(TAG, "onResponse: Table1: "+Table1);
                                itemList.clear();
                                encashItemList.clear();
                                JSONArray leaveBalanceArray = new JSONArray(Table1);
                                Log.e(TAG, "leaveBalanceArray: "+leaveBalanceArray.length());
                                Log.e(TAG, "leaveBalanceArray: "+itemList.size());
                                for (int i = 0; i < leaveBalanceArray.length(); i++) {
                                    JSONObject balanceObject = leaveBalanceArray.optJSONObject(i);
                                    final String Code = balanceObject.optString("Code");

                                    final String Opening = balanceObject.optString("Opening");
                                    final String LeaveAvailed = balanceObject.optString("LeaveAvailed");
                                    final String Avaliable = balanceObject.optString("Avaliable");
                                    String LeaveTypeID = balanceObject.optString("LeaveTypeID");
                                    String LeaveTypeName = balanceObject.optString("LeaveTypeName");
                                    String AppCLS = balanceObject.optString("AppCLS");
                                    String appValue = balanceObject.optString("appValue");
                                    String iD = balanceObject.optString("iD");
                                    typeAvaild.add(LeaveTypeID + "_" + Avaliable);
                                    if (Code.equals("PLA")) {
                                        enCashPLALeaveId = LeaveTypeID;
                                        llPLA.setVisibility(View.VISIBLE);
                                    } else if (Code.equals("PLC")) {
                                        enCashPLCLeaveId = LeaveTypeID;
                                        llPLC.setVisibility(View.VISIBLE);
                                    } else if (Code.equals("PL")) {
                                        enCashPLLeaveId = LeaveTypeID;
                                        llPL.setVisibility(View.VISIBLE);
                                    }
                                    /*if (!LeaveTypeID.equals("0")){
                                        LeaveBalanceDetailsModel model = new LeaveBalanceDetailsModel(Code, Opening, LeaveAvailed,LeaveTypeName);
                                        itemList.add(model);
                                        EncashmentItemModel encashmentItemModel = new EncashmentItemModel(LeaveTypeID,Code,LeaveTypeName,Opening,LeaveAvailed,
                                                Avaliable,AppCLS,appValue,iD);

                                        encashItemList.add(encashmentItemModel);
                                    }*/
                                }
                                Log.e(TAG, "onResponse: itemList: "+itemList.size());
                                //LeaveEncashmentAdapter leaveEncashmentAdapter = new LeaveEncashmentAdapter(getContext(),ApplicationFragment.this,encashItemList);
                                //rvEncashment.setAdapter(leaveEncashmentAdapter);

                                String Table=jsonArray.optString("Table");
                                JSONArray leaveReqArray = new JSONArray(Table);
                                for (int i = 0; i < leaveReqArray.length(); i++) {
                                    JSONObject requestObject = leaveReqArray.optJSONObject(i);
                                    String Request = requestObject.optString("Request");
                                    Log.e(TAG, "Request: "+Request);
                                    tvRequested.setText(Request);
                                    String Approve = requestObject.optString("Approve");
                                    Log.e(TAG, "Approve: "+Approve);
                                    String Reject = requestObject.optString("Reject");
                                    Log.e(TAG, "Reject: "+Reject);
                                    String Pending = requestObject.optString("Pending");
                                    Log.e(TAG, "Pending: "+Pending);
                                    tvApporved.setText(Approve);
                                    tvRejected.setText(Reject);
                                    tvPending.setText(Pending);
                                }


                                String Table2 = jsonArray.optString("Table2");
                                Log.e(TAG, "Table2: "+Table2);
                                JSONArray arrayTable2 = new JSONArray(Table2);
                                for (int i = 0; i < arrayTable2.length(); i++) {
                                    JSONObject requestObject = arrayTable2.optJSONObject(i);
                                    if (requestObject.optString("ApproverName") != null || !requestObject.optString("ApproverName").isEmpty()){
                                        Log.e(TAG, "onResponse: called 1");
                                        String approverName = requestObject.optString("ApproverName");
                                        txtEncashApproverName.setText("Approver Name: "+approverName);
                                        txtEncashApproverName.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.e(TAG, "onResponse: called 2");
                                        txtEncashApproverName.setVisibility(View.GONE);
                                    }
                                }

                                //Table10

                                String Table10 = jsonArray.optString("Table10");
                                Log.e(TAG, "Table10: "+Table10);
                                JSONArray arrayTable10 = new JSONArray(Table10);
                                for (int i = 0; i < arrayTable10.length(); i++) {
                                    JSONObject requestObject = arrayTable10.optJSONObject(i);
                                    Log.e(TAG, "onResponse: "+requestObject );
                                    txtEcashmentDuringYear.setText("Leave Encashment During The Calendar Year: "+requestObject.optString("Avaliable"));
                                }

                                setAdapter();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        llLoader.setVisibility(View.GONE);
                        Log.e(TAG, "Encashment_details_anError: "+anError);
                    }
                });
    }

    public void addPlaPlcValue(int calledFrom,String value) {
        Log.e(TAG, "addPlaPlcValue: val: "+value);
        Log.e(TAG, "addPlaPlcValue: globlePlaValue: "+globlePlaValue);
        if (calledFrom == 1 && !value.isEmpty()){
            globlePlaValue = Integer.valueOf(value);
            int val = Integer.valueOf(value);

            if (globlePlaValue < val) {
                Log.e(TAG, "addPlaPlcValue: called: 1");
                totalEncasementValue = globlePlaValue + val;
                //globlePlaValue = Integer.valueOf(value);
            } else if (globlePlaValue > val) {
                Log.e(TAG, "addPlaPlcValue: called: 2");
                totalEncasementValue = globlePlaValue - val;
                //globlePlaValue = Integer.valueOf(value);
            }
            Log.e(TAG, "addPlaPlcValue: condition: 1: "+globlePlaValue);
            Log.e(TAG, "addPlaPlcValue: totalEncasementValue: 1: "+ totalEncasementValue);
        } else if (calledFrom == 2){
            //globlePlcValue = Integer.valueOf(value);
            int val = Integer.valueOf(value);
            if (globlePlcValue < val) {
                globlePlcValue = globlePlcValue + val;
            } else if (globlePlcValue > val) {
                globlePlcValue = globlePlcValue - val;
            }
            Log.e(TAG, "addPlaPlcValue: condition: 2: "+globlePlcValue);
        }
    }


    private void setAdapter() {
        LeaveBalanceDetailsAdapter lAdaapter = new LeaveBalanceDetailsAdapter(itemList, getContext());
        rvItem.setAdapter(lAdaapter);
    }

    public void getLeaveAllDetails(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveAllDetails: called: "+jsonObject);
        llLoader.setVisibility(View.VISIBLE);
        llRejected.setEnabled(false);
        llPending.setEnabled(false);
        llApproved.setEnabled(false);
        llRequested.setEnabled(false);

        AndroidNetworking.post(Api.sLeaveDetails)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        llLoader.setVisibility(View.GONE);
                        JSONObject job1 = response;
                        Log.e(TAG, "getLeaveAllDetails: " + job1);

                        leaveTypeList.add("Please select");
                        mLeaveTypeList.add(new SpinnerModel("0", "0"));
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {


                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONObject jsonArray=new JSONObject(responseData);
                                String Table1=jsonArray.optString("Table1");
                                Log.e(TAG, "onResponse: Table1: "+Table1);
                                itemList.clear();
                                JSONArray leaveBalanceArray = new JSONArray(Table1);
                                for (int i = 0; i < leaveBalanceArray.length(); i++) {
                                    JSONObject balanceObject = leaveBalanceArray.optJSONObject(i);
                                    final String Code = balanceObject.optString("Code");
                                    final String Opening = balanceObject.optString("Opening");
                                    final String LeaveAvailed = balanceObject.optString("LeaveAvailed");
                                    final String Avaliable = balanceObject.optString("Avaliable");
                                    String LeaveTypeID = balanceObject.optString("LeaveTypeID");
                                    typeAvaild.add(LeaveTypeID + "_" + Avaliable);

                                    LeaveBalanceDetailsModel model = new LeaveBalanceDetailsModel(Code, Opening, LeaveAvailed);
                                    itemList.add(model);
                                }

                                typeAvailable = typeAvaild.toString().replace("]", "").replace("[", "").replaceAll("\\s+", "");
                                ;
                                Log.d("availd", typeAvaild.toString());
                                setAdapter();

                                String Table=jsonArray.optString("Table");
                                JSONArray leaveReqArray = new JSONArray(Table);
                                for (int i = 0; i < leaveReqArray.length(); i++) {
                                    JSONObject requestObject = leaveReqArray.optJSONObject(i);
                                    String Request = requestObject.optString("Request");
                                    tvRequested.setText(Request);
                                    String Approve = requestObject.optString("Approve");
                                    String Reject = requestObject.optString("Reject");
                                    String Pending = requestObject.optString("Pending");
                                    tvApporved.setText(Approve);
                                    tvRejected.setText(Reject);
                                    tvPending.setText(Pending);
                                }

                                String Table7=jsonArray.optString("Table7");
                                Log.e(TAG, "onResponse: Table7: "+Table7);
                                JSONArray leaveTypeArray = new JSONArray(Table7);
                                for (int i = 0; i < leaveTypeArray.length(); i++) {
                                    JSONObject typeObject = leaveTypeArray.optJSONObject(i);
                                    String LeaveTypeID = typeObject.optString("LeaveTypeID");
                                    final String Name = typeObject.optString("Name");
                                    if (pref.getLanguage().equals("hi")) {
                                        /*final Handler textViewHandler2 = new Handler();
                                        new AsyncTask<Void, Void, Void>() .execute();*/
                                    } else {
                                        leaveTypeList.add(Name);
                                    }

                                    Log.e(TAG, "LEAVE NAME: name: "+Name+" LeaveTypeID: "+LeaveTypeID);
                                    SpinnerModel spModel = new SpinnerModel(Name, LeaveTypeID);
                                    mLeaveTypeList.add(spModel);
                                }

                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                        (getContext(), android.R.layout.simple_spinner_item,
                                                leaveTypeList); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spLeaveType.setAdapter(spinnerArrayAdapter);

                                String Table2=jsonArray.optString("Table2");
                                JSONArray leaveApproverArray = new JSONArray(Table2);
                                for (int i = 0; i < leaveApproverArray.length(); i++) {
                                    JSONObject approverObject = leaveApproverArray.optJSONObject(i);
                                    final String ApproverName = approverObject.optString("ApproverName");

                                    if (pref.getLanguage().equals("hi")) {
                                        final Handler textViewHandler2 = new Handler();
                                        //new AsyncTask<Void, Void, Void>() .execute();
                                    } else {
                                        tvApproverName.setText("Approver Name:" + ApproverName);
                                    }
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
                        } else {
                            llLoader.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getApproverOrNot(JSONObject jsonObject) {
        Log.e(TAG, "getApproverOrNot: called: "+jsonObject.toString());
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sApproverCheckApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mApplicantList.clear();
                        applicantList.clear();
                        applicantList.add("Please select");
                        mApplicantList.add(new SpinnerModel("0", "0"));

                        pd.dismiss();
                        JSONObject job1 = response;
                        Log.e(TAG, "LEAVE_APPROVER: " + job1);

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                if (jsonArray.length()>0) {

                                    showApproverDialog();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        final String Name = obj.optString("Name");
                                        String ApplicantID = obj.optString("ApplicantID");

                                        if (pref.getLanguage().equals("hi")) {
                                            final Handler textViewHandler2 = new Handler();
                                            //new AsyncTask<Void, Void, Void>() .execute();
                                        } else {
                                            applicantList.add(Name);
                                        }

                                        SpinnerModel spModel = new SpinnerModel(Name, ApplicantID);
                                        mApplicantList.add(spModel);
                                    }
                                    ((LeaveApplicationActivity) getContext()).approverVisibility();
                                } else {
                                    applicantId = pref.getEmpId();
                                    JSONObject jsonObject=new JSONObject();
                                    try {
                                        jsonObject.put("CompanyID",pref.getEmpClintId());
                                        jsonObject.put("EmployeeID",pref.getEmpId());
                                        jsonObject.put("ApproverID",pref.getEmpId());
                                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                                        getLeaveAllDetails(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //getLeaveAllDetails();
                                    ((LeaveApplicationActivity) getContext()).approverHidden();
                                    // llShow.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // boolean _status = job1.getBoolean("status");
                            // do anything with response
                        }else {


                            applicantId = pref.getEmpId();
                            JSONObject jsonObject=new JSONObject();
                            try {
                                jsonObject.put("CompanyID",pref.getEmpClintId());
                                jsonObject.put("EmployeeID",pref.getEmpId());
                                jsonObject.put("ApproverID",pref.getEmpId());
                                jsonObject.put("SecurityCode",pref.getSecurityCode());
                                getLeaveAllDetails(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        pd.dismiss();


                    }
                });
    }


    public void getLeaveMode(JSONObject jsonObject) {
        Log.e(TAG, "getLeaveMode: called");
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();

        AndroidNetworking.post(Api.sLeaveModeApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        leaveMode.clear();
                        mLeaveMode.clear();

                        JSONObject job1 = response;
                        pd.dismiss();
                        Log.e("response12", "@@@@@@" + job1);


                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject obj=jsonArray.optJSONObject(i);
                                    final String VALUE = obj.optString("VALUE");
                                    String ID = obj.optString("ID");

                                    SpinnerModel spModel = new SpinnerModel(VALUE, ID);
                                    mLeaveMode.add(spModel);
                                    leaveMode.add(VALUE);

                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                        (getContext(), android.R.layout.simple_spinner_item,
                                                leaveMode); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spLeaveMode.setAdapter(spinnerArrayAdapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            // boolean _status = job1.getBoolean("status");


                            // do anything with response
                        }else {
                            Toast.makeText(getContext(),Response_Message,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                        llLoader.setVisibility(View.GONE);
                    }
                });
    }

    private void getHindiMode() {
        //names.clear();
//        llLoader.setVisibility(View.VISIBLE);
        leaveMode.add("पूरा दिन");
        leaveMode.add("पूरा और आधा दिन");
        leaveMode.add("पूरा दिन");
        mLeaveMode.add(new SpinnerModel("पूरा दिन", "1"));
        mLeaveMode.add(new SpinnerModel("पूरा और आधा दिन", "2"));
        mLeaveMode.add(new SpinnerModel("आधा दिन", "0"));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        leaveMode); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLeaveMode.setAdapter(spinnerArrayAdapter);


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
                        startDate = month + "/" + dayOfMonth + "/" + year;
                        tvStrtDate.setText(TimeDateConverter.convert_Date_MM_DD_YYYY_To_dd_MMM_yyyy(startDate));
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
                        endDate = month + "/" + dayOfMonth + "/" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date strDate = null;
                        try {
                            strDate = sdf.parse(startDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        Date striDate = null;
                        try {
                            striDate = df.parse(endDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (striDate.getTime() > strDate.getTime() ||striDate.getTime() == strDate.getTime()) {
                            JSONObject jsonObject=new JSONObject();
                            try {
                                jsonObject.put("CompanyID",pref.getEmpClintId());
                                jsonObject.put("EmployeeID",applicantId);
                                jsonObject.put("StartDate",startDate);
                                jsonObject.put("EndDate",endDate);
                                jsonObject.put("LeaveTypeID",typeId);
                                jsonObject.put("LeaveMode",leaveModeId);
                                jsonObject.put("SecurityCode",pref.getSecurityCode());
                                validationChecking(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                tvEndDate.setText("");
            }
        });

        al1 = dialogBuilder.create();
        al1.setCancelable(false);
        Window window = al1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        al1.show();
    }


    private void showApproverDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.approver_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView tvDialog = (TextView) dialogView.findViewById(R.id.tvDialog);
        if (pref.getLanguage().equals("hi")) {
            tvDialog.setText("स्वयं के लिए आवेदन करने या सूची से चयन करने के लिए स्व पर क्लिक करें");
        } else {
            tvDialog.setText("Click self to apply for own leave or select from drop down to apply on behalf of your team member.");
        }
        Spinner spSub = (Spinner) dialogView.findViewById(R.id.spSub);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,
                        applicantList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSub.setAdapter(spinnerArrayAdapter);
        spSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    appid = mApplicantList.get(i).getItemId();
                    applicantName = mApplicantList.get(i).getItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        if (pref.getLanguage().equals("hi")) {
            btnSubmit.setText("प्रस्तुत");
        } else {
            btnSubmit.setText("Submit");
        }
        Button btnSelf = (Button) dialogView.findViewById(R.id.btnSelf);
        if (pref.getLanguage().equals("hi")) {
            btnSelf.setText("स्वयं");
        } else {
            btnSelf.setText("Self");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appid.equals("")) {

                    applicantId = appid;
                    alert1.dismiss();
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("CompanyID",pref.getEmpClintId());
                        jsonObject.put("EmployeeID",pref.getEmpId());
                        jsonObject.put("ApproverID",pref.getEmpId());
                        jsonObject.put("SecurityCode",pref.getSecurityCode());
                        getLeaveAllDetails(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (pref.getLanguage().equals("hi")) {
                        final Handler textViewHandler2 = new Handler();
                        //new AsyncTask<Void, Void, Void>() .execute();
                    } else {
                        tvEmpName.setText("Leave application of " + applicantName);
                    }

                } else {
                    Toast.makeText(getContext(), "Please select leave applicant", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applicantId = pref.getEmpId();
                alert1.dismiss();
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("CompanyID",pref.getEmpClintId());
                    jsonObject.put("EmployeeID",pref.getEmpId());
                    jsonObject.put("ApproverID",pref.getEmpId());
                    jsonObject.put("SecurityCode",pref.getSecurityCode());
                    getLeaveAllDetails(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pref.getLanguage().equals("hi")) {
                    final Handler textViewHandler2 = new Handler();
                    //new AsyncTask<Void, Void, Void>() .execute();
                } else {
                    tvEmpName.setText("Leave application of " + pref.getEmpName());
                }
            }
        });

        alert1 = dialogBuilder.create();
        alert1.setCancelable(false);
        Window window = alert1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        alert1.show();
    }


    private void validationChecking(JSONObject jsonObject) {
        Log.e(TAG, "validationChecking: called: "+jsonObject);
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sLeaveStartCheckApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        pd.dismiss();
                        JSONObject job1 = response;
                        Log.e(TAG, "Validation_Checking" + job1);
                        String Response_Message=job1.optString("Response_Message");

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                            tvEndDate.setText(TimeDateConverter.convert_Date_MM_DD_YYYY_To_dd_MMM_yyyy(endDate));
                            if (leaveModeId.equals("0") || leaveModeId.equals("2")) {
                                dayBreakupListDetails.clear();
                                showDailyBrkUpDialog();
                            } else if (leaveModeId.equals("1")){


                            }else {
                                showCompOffDialog();
                            }

                            // boolean _status = job1.getBoolean("status");
                            // do anything with response
                        }else {
                            endDate = "";
                            tvEndDate.setText("");
                            showErrorDialog(Response_Message);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });
    }


    private void showDailyBrkUpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.day_breakup_dialog, null);
        dialogBuilder.setView(dialogView);
        rvBrkupItem = (RecyclerView) dialogView.findViewById(R.id.rvBrkupItem);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvBrkupItem.setLayoutManager(layoutManager);

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("CompanyID",pref.getEmpClintId());
            jsonObject.put("EmployeeID",applicantId);
            jsonObject.put("StartDate",startDate);
            jsonObject.put("EndDate",endDate);
            jsonObject.put("LeaveTypeID",typeId);
            jsonObject.put("SecurityCode",pref.getSecurityCode());
            getDayBreakUp(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);

        if (pref.getLanguage().equals("hi")) {
            btnSubmit.setText("प्रस्तुत");
        } else {
            btnSubmit.setText("Submit");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leaveModeId.equals("2")){
                    if (halfdetails.contains("0.5") && halfdetails.contains("1")){
                        alert2.dismiss();
                    }else {
                        Toast.makeText(getContext(),"You have to select one half day and one full day",Toast.LENGTH_LONG).show();
                    }
                }else {
                    if (halfdetails.contains("0.5") || halfdetails.contains("1")){
                        alert2.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Please select first half or second half",Toast.LENGTH_LONG).show();
                    }
                }
               /* if (category.equals("1") || category.equals("3")) {
                    showCompOffDialog();
                } else {

                }*/
            }
        });
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        if (pref.getLanguage().equals("hi")) {
            btnCancel.setText("रद्द करना");
        } else {
            btnCancel.setText("Cancel");
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayBreakupListDetails.clear();
                alert2.dismiss();
                endDate="";
                tvEndDate.setText("");
            }
        });


        alert2 = dialogBuilder.create();
        alert2.setCancelable(false);
        Window window = alert2.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        alert2.show();
    }


    private void showCompOffDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.compoff_dialog, null);
        dialogBuilder.setView(dialogView);
        rvCompOffItem = (RecyclerView) dialogView.findViewById(R.id.rvCompOffItem);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCompOffItem.setLayoutManager(layoutManager);
        //getCompOffDetails();
        JSONObject object = new JSONObject();
        try {
            object.put("CompanyID", pref.getEmpClintId());
            object.put("EmployeeID", pref.getEmpId());
            object.put("StartDate", startDate);
            object.put("EndDate", endDate);
            object.put("LeaveTypeID", typeId);
            object.put("Iscompoff", category);
            object.put("SecurityCode", pref.getSecurityCode());
            getCompOffDetails2(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        if (pref.getLanguage().equals("hi")) {
            btnSubmit.setText("प्रस्तुत");
        } else {
            btnSubmit.setText("Submit");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compOffListDetails.size()>0) {
                    alert5.dismiss();
                }else {
                    Toast.makeText(getContext(),"Please select item",Toast.LENGTH_LONG).show();
                }

            }
        });
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnDiscard);
        if (pref.getLanguage().equals("hi")) {
            btnCancel.setText("रद्द करना");
        } else {
            btnCancel.setText("Cancel");
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dayBreakupListDetails.clear();
                alert5.dismiss();
                endDate="";
                tvEndDate.setText("");
            }
        });

        alert5 = dialogBuilder.create();
        alert5.setCancelable(false);
        Window window = alert5.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        alert5.show();
    }


    private void showChooseFileDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.choose_file_dialog_2, null);
        dialogBuilder.setView(dialogView);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert4.dismiss();
            }
        });
        ImageView imgCamera = (ImageView) dialogView.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cameraIntent();
                //Intent intent = new Intent(getContext(), AndroidXCameraActivity.class);
                //startActivityForResult(intent,100);
                //mSelectDocumentImages.launch(intent);

                cameraIntent();
            }
        });
        ImageView imgPdf = (ImageView) dialogView.findViewById(R.id.imgPDF);
        imgPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        alert4 = dialogBuilder.create();
        alert4.setCancelable(true);
        Window window = alert4.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alert4.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf,image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "image/*"});
        mSelectDocumentImages.launch(intent);
    }

    ActivityResultLauncher<Intent> mSelectDocumentImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 100) {
                //isFileSelectedFlag = true;
                alert4.dismiss();
                //selectDocument.cancel();
                String imageURl = result.getData().getStringExtra("image").toString();
                Uri uri = Uri.parse(imageURl);

                Log.e(TAG, "onActivityResult: "+imageURl);
                String imageName = FindDocumentInformation.FileNameFromURL(imageURl);
                Log.e(TAG, "onActivityResult: imageName: "+imageName);


                boolean isImageTooLarge = ImageUtils.isImageGreaterThan2MB(getActivity(), uri);
                if (isImageTooLarge) {
                    Log.e(TAG, "isImageTooLarge: true");
                    // Image is larger than 2 MB
                    /*compressedImageFile = new Compressor.Builder(getActivity())
                            .setMaxWidth(1024)
                            .setMaxHeight(768)
                            .setQuality(70)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                            ).build()
                            .compressToFile(file);*/

                    try {
                        compressedImageFile = new ImageZipper(getActivity())
                                .setQuality(70)
                                .setMaxWidth(1024)
                                .setMaxHeight(768)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .compressToFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                } else {
                    // Image is 2 MB or smaller
                    Log.e(TAG, "isImageTooLarge: false");
                    Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                }


                compressedImageFile  = new File(uri.getPath());
                try {
                    encodedImage = ImageUtils.fileToBase64(compressedImageFile).replaceAll("\n","");
                    //Log.e(TAG, "base64Image: ==================="+base64image );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Log.e(TAG, "mSelectDocumentImages: "+uri.getPath());
                imgPic.setImageURI(uri);
                imgPic.setVisibility(View.VISIBLE);
                //base64image = FileToBase64Converter.imageToBase642(compressedImageFile.getPath());

                Log.e(TAG, "onActivityResult: base64image testing: "+encodedImage);

                realPath = getRealPath(getActivity(),uri);
                //fileType = "png";
                //fileType = "image/"+FindDocumentInformation.FindFileTypeFromDocumentName(imageName);
                fileType = "image/jpg";
                Log.e(TAG, "onActivityResult: "+fileType);
                //binding.txtPdfUrl.setVisibility(View.GONE);
                //binding.imgPic.setVisibility(View.VISIBLE);

                           /* if (realPath.endsWith(".pdf")) {
                                Log.e(TAG, "FILE TYPE: "+fileType);
                                fileType = ".pdf";
                            } else {
                                Log.e(TAG, "FILE TYPE: "+fileType);

                            }*/
                if (imageName.contains("_")) {
                    imageName = imageName.replace("_", "-");
                    Log.e(TAG, "Replaced string: " + imageName);
                }
                stringFile = imageName + "_" + encodedImage + "_" + fileType;
                Log.e("stringFile", stringFile);
            } else {
                if (result.getData() != null){
                    //isFileSelectedFlag = true;
                    alert4.dismiss();
                    Uri selectedFileUri = result.getData().getData();
                    Log.e(TAG, "onActivityResult: "+selectedFileUri.getPath());
                    Log.e(TAG, "onActivityResult: REAL PATH: "+getRealPath(getActivity(),selectedFileUri));
                    realPath = getRealPath(getActivity(),selectedFileUri);
                    String docName = FindDocumentInformation.FileNameFromURL(realPath);
                    //imageName = FindDocumentInformation.FileNameFromURL(realPath);
                    if (realPath.endsWith(".pdf")) {
                        try {
                            encodedImage = FileToBase64Converter.convertToBase64(FileToBase64Converter.convertInputStreamToFile(getContext(),selectedFileUri,docName)).replaceAll("\n","");
                            //Log.e(TAG, "onActivityResult: base64image: "+base64image);
                        } catch (IOException e) {
                            //Log.e(TAG, "onActivityResult: ERROR");
                            throw new RuntimeException(e);
                        }
                        Log.e(TAG, "FILE TYPE: "+fileType);
                        //fileType = "pdf";
                        fileType = "application/pdf";

                        if (docName.contains("_")) {
                            docName = docName.replace("_", "-");
                            Log.e(TAG, "Replaced string: " + docName);
                        }

                        stringFile = docName + "_" + encodedImage + "_" + fileType;
                        Log.e(TAG, "onActivityResult: "+stringFile );
                        txtPdfUrl.setText(selectedFileUri.getPath());
                        txtPdfUrl.setVisibility(View.VISIBLE);
                        imgPic.setVisibility(View.VISIBLE);
                        Drawable myDrawable = getResources().getDrawable(R.drawable.pdf_1);
                        imgPic.setImageDrawable(myDrawable);
                    } else {
                        compressedImageFile  = new File(realPath);
                        boolean isImageTooLarge = ImageUtils.isImageGreaterThan2MB(getActivity(), selectedFileUri);
                        if (isImageTooLarge) {
                            Log.e(TAG, "isImageTooLarge: true");
                            // Image is larger than 2 MB
                            /*compressedImageFile = new Compressor.Builder(getActivity())
                                    .setMaxWidth(1024)
                                    .setMaxHeight(768)
                                    .setQuality(70)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                    ).build()
                                    .compressToFile(compressedImageFile);*/

                            try {
                                compressedImageFile = new ImageZipper(getActivity())
                                        .setQuality(70)
                                        .setMaxWidth(1024)
                                        .setMaxHeight(768)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .compressToFile(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                        } else {
                            // Image is 2 MB or smaller
                            Log.e(TAG, "isImageTooLarge: false");
                            Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                        }

                        try {
                            encodedImage = ImageUtils.fileToBase64(compressedImageFile).replaceAll("\n","");
                            //Log.e(TAG, "base64Image: ==================="+encodedImage);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.e(TAG, "FILE TYPE: "+fileType);
                        //fileType = FindDocumentInformation.FindFileTypeFromDocumentName(docName).replaceAll("\n","");
                        //fileType = "image/"+FindDocumentInformation.FindFileTypeFromDocumentName(docName);
                        fileType = "image/jpg";
                        if (docName.contains("_")) {
                            docName = docName.replace("_", "-");
                            Log.e(TAG, "Replaced string: " + docName);
                        }
                        stringFile = docName + "_" + encodedImage + "_" + fileType;
                        Log.e(TAG, "onActivity Result: "+stringFile);
                        imgPic.setImageURI(selectedFileUri);
                        txtPdfUrl.setVisibility(View.GONE);
                        imgPic.setVisibility(View.VISIBLE);
                    }
                }
            }


        }
    });

    /*private void showChooseFileDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.choose_file_dialog, null);
        dialogBuilder.setView(dialogView);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert4.dismiss();
            }
        });
        ImageView imgCamera = (ImageView) dialogView.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        ImageView imgPdf = (ImageView) dialogView.findViewById(R.id.imgPDF);
        imgPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPDFChooser();
            }
        });


        alert4 = dialogBuilder.create();
        alert4.setCancelable(true);
        Window window = alert4.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alert4.show();
    }*/

    private void showPDFChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQUEST);
    }



    private void getDayBreakUp(JSONObject jsonObject) {
        Log.e(TAG, "getDayBreakUp: INPUT: "+jsonObject);
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sLeaveDayDetailsapi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pd.dismiss();
                        JSONObject job1 = response;
                        Log.e(TAG, "getDayBreakUp: " + job1);
                        String Response_Message=job1.optString("Response_Message");
                        try {
                            int Response_Code = job1.optInt("Response_Code");
                            if (Response_Code == 101) {
                                // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                                String responseData = job1.optString("Response_Data");
                                JSONArray jsonArray = new JSONArray(responseData);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String BreakDate = obj.optString("BreakDate");
                                    String DateName = obj.optString("DateName");
                                    String DayAccess = obj.optString("DayAccess");
                                    String DayAccessDesc = obj.optString("DayAccessDesc");
                                    if (DayAccess.equals("-1")) {
                                        dayBreakupListDetails.add(BreakDate + "_" + "0" + "_" + "0");
                                    } else {

                                    }
                                    DayBreakUpModel spModel = new DayBreakUpModel(BreakDate, DateName, DayAccess, DayAccessDesc);
                                    dayBreakupList.add(spModel);
                                }

                                dayAdapter = new DayBreakUpAdapter(dayBreakupList, ApplicationFragment.this, getContext(),leaveModeId);
                                rvBrkupItem.setAdapter(dayAdapter);
                            } else {

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                    }
                });
    }


    public void updateStatus(int position, boolean status) {
        dayBreakupList.get(position).setSelected(status);
        if (dayBreakupList.get(position).isSelected() == true) {
            halfdetails.add(dayBreakupList.get(position).getBalance());
            dayBreakupListDetails.add(dayBreakupList.get(position).getBrkupDate() + "_" + dayBreakupList.get(position).getDayModeValue() + "_" + dayBreakupList.get(position).getBalance());
        } else {
            dayBreakupListDetails.remove(position);
            halfdetails.remove(position);
        }


        dayBreakUpDetails = dayBreakupListDetails.toString().replace("[", "").replace("]", "").replaceAll("\\s+", "");
        Log.d("detailslist", dayBreakUpDetails);

        /*Log.d("arpan", itemList.toString());
        String i = itemList.toString();
        String d = i.replace("[", "").replace("]", "");
        empId = d.replaceAll("\\s+", "");
        String emp=empName.toString();
        String replace=emp.replace("[", "").replace("]", "");
        tvEmpName.setText(replace);
*/

        dayAdapter.notifyDataSetChanged();
    }

    public void updateStatusForComPff(int position, boolean status) {
        compOffList.get(position).setSelected(status);
        if (compOffList.get(position).isSelected() == true) {
            compOffListDetails.add(compOffList.get(position).getBrkUpDate() + "_" + compOffList.get(position).getDayValue() );


        } else {
            compOffListDetails.remove(position);
        }

        compOffDetails = compOffListDetails.toString().replace("[", "").replace("]", "").replaceAll("\\s+", "");
        Log.d("compffdetails", compOffDetails);

        /*Log.d("arpan", itemList.toString());
        String i = itemList.toString();
        String d = i.replace("[", "").replace("]", "");
        empId = d.replaceAll("\\s+", "");
        String emp=empName.toString();
        String replace=emp.replace("[", "").replace("]", "");
        tvEmpName.setText(replace);
*/

        compOffAdapter.notifyDataSetChanged();
    }




    private void preView(JSONObject object) {
        Log.e(TAG, "preView2: "+object.toString());
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        AndroidNetworking.post(Api.sCheckLeaveViewSummary)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "PRE_VIEW: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message = job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            if (leaveModeId.equals("1")) {
                                dayBreakUpDetails = job1.optString("Response_Data");
                            } else {
                                //dayBreakUpDetails = null;
                            }
                            showPreviewDialog();
                        } else {
                            showErrorDialog(Response_Message);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "PRE_VIEW_onError: "+anError);
                        pd.dismiss();
                    }
                });

    }


    private void cameraIntent() {
       /* ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);*/

        Intent intent = new Intent(getContext(), AndroidXCameraActivity.class);
        mSelectDocumentImages.launch(intent);
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:

                if (resultCode == RESULT_OK) {
                    try {
                        try {

                            //messageAlert();
                            String imageurl = *//*"file://" +*//* getRealPathFromURIPath(imageUri);
                            file = new File(imageurl);

                            // Log.d("imageSixw", String.valueOf(getReadableFileSize(compressedImageFile.length())));

                           *//* try {
                                encodedImage = ImageUtils.fileToBase64(file).replaceAll("\n","");
                                //Log.e(TAG, "base64Image: ==================="+base64image );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }*//*
                            BitmapFactory.Options o = new BitmapFactory.Options();
                            o.inSampleSize = 6;
                            //Bitmap bm = cropToSquare(BitmapFactory.decodeFile(imageurl, o));
                            Bitmap bm = new ImageZipper(getContext()).compressToBitmap(file);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            //encodedImage = encodeFileToBase64Binary(file);
                            Log.e("encoded", encodedImage);
                            imgPic.setImageBitmap(bm);
                            imgPic.setVisibility(View.VISIBLE);
                            attachmentFlag = 1;
                            alert4.dismiss();

                            //String[] brkDown = imageurl.split("/");
                            //String name = brkDown[5];

                            String contentType = "image/jpg";
                            String docName = FindDocumentInformation.FileNameFromURL(imageurl);
                            if (docName.contains("_")) {
                                docName = docName.replace("_", "-");
                                Log.e(TAG, "Replaced string: " + docName);
                            }
                            //String contentType = "image/"+FindDocumentInformation.FindFileTypeFromDocumentName(docName);
                            //String contentType = FindDocumentInformation.FindFileTypeFromDocumentName(docName);
                            stringFile = docName + "_" + encodedImage + "_" + contentType;
                            Log.e("stringFile", stringFile);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PDF_REQUEST:
                if (requestCode == PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri selectedFileURI = data.getData();
                    pdffile = new File(getRealPDFPathFromURI(selectedFileURI));
                    alert4.dismiss();
                    encodedImage = encodeFileToBase64Binary(pdffile);
                    String filePath = getRealPDFPathFromURI(selectedFileURI);
                    String[] brkDown = filePath.split("/");
                    String name = brkDown[5];
                    String contentType = "application/pdf";
                    stringFile = name + "_" + encodedImage + "_" + contentType;
                }
                break;
        }
    }*/

    private String getRealPathFromURIPath(Uri contentURI) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentURI, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void showPreviewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.preview_dialog, null);
        dialogBuilder.setView(dialogView);
        rvPreviewItem = (RecyclerView) dialogView.findViewById(R.id.rvPreviewItem);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvPreviewItem.setLayoutManager(layoutManager);
        //getPreviewItem();
        JSONObject object = new JSONObject();
        try {
            object.put("CompanyID", pref.getEmpClintId());
            object.put("EmployeeID", applicantId);
            object.put("StartDate", startDate);
            object.put("EndDate", endDate);
            object.put("StrDayBreakUp", dayBreakUpDetails);
            object.put("LeaveType", leaveType);
            //object.put("Reason", etReason.getText().toString().trim());
            object.put("Reason", etReason.getText().toString().replaceAll("\\s+", "%20"));
            object.put("IsAttachment", attachmentFlag);
            object.put("SecurityCode", pref.getSecurityCode());
            getPreviewItem2(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView tvReason = (TextView) dialogView.findViewById(R.id.tvReason);
        TextView tvValue = (TextView) dialogView.findViewById(R.id.tvValue);
        TextView tvEndDate = (TextView) dialogView.findViewById(R.id.tvEndDate);
        TextView tvStrtDate = (TextView) dialogView.findViewById(R.id.tvStrtDate);
        TextView tvType = (TextView) dialogView.findViewById(R.id.tvType);

        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //leaveSave();

                JSONObject object = new JSONObject();
                try {
                    object.put("CompanyID", pref.getEmpClintId());
                    object.put("EmployeeId", applicantId);
                    object.put("StartDate", startDate);
                    object.put("EndDate", endDate);
                    object.put("LeaveTypeID", typeId);
                    object.put("LeaveMode", leaveModeId);
                    object.put("AppliedLeave", LeaveValue);
                    object.put("Reasons", etReason.getText().toString());
                    object.put("LeaveCategory", category);
                    object.put("StrDayBreakUp", (dayBreakUpDetails.isEmpty())?JSONObject.NULL:dayBreakUpDetails);
                    object.put("StrCompOff", compOffDetails);
                    object.put("StrFile", stringFile);
                    object.put("createdby", pref.getEmpId());
                    object.put("SecurityCode", pref.getSecurityCode());
                    object.put("Operation", "0");
                    LeaveSave(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button btnDiscard = (Button) dialogView.findViewById(R.id.btnDiscard);
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert3.dismiss();
            }
        });

        if (pref.getLanguage().equals("hi")) {
            tvReason.setText("कारण");
            tvValue.setText("मूल्य");
            tvEndDate.setText("अंतिम तिथि");
            tvStrtDate.setText("आरंभ तिथि");
            tvType.setText("प्रकार");
            btnSubmit.setText("प्रस्तुत");
            btnDiscard.setText("रद्द करें");
        } else {
            tvReason.setText("Reason");
            tvValue.setText("Value");
            tvEndDate.setText("End date");
            tvStrtDate.setText("Start date");
            tvType.setText("Type");
            btnDiscard.setText("Discard");
            btnSubmit.setText("Submit");
        }

        alert3 = dialogBuilder.create();
        alert3.setCancelable(false);
        Window window = alert3.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        alert3.show();
    }




    private void getPreviewItem2(JSONObject object) {
        Log.e(TAG, "BIND_VIEW_SUMMERY: object: "+object );
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        //CompanyID=" + pref.getEmpClintId() + "&EmployeeID=" + applicantId + "&StartDate=" + startDate + "&EndDate=" + endDate + "&StrDayBreakUp=" + dayBreakUpDetails + "&LeaveType=" + leaveType + "&Reason=" + etReason.getText().toString().replaceAll("\\s+", "%20") + "&IsAttachment=" + attachmentFlag + "&SecurityCode=" + pref.getSecurityCode();

        AndroidNetworking.post(Api.sBindViewSummary)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "BIND_VIEW_SUMMERY: "+response.toString());
                        pd.dismiss();
                        try {
                            previewItem.clear();
                            JSONObject job1 = response;
                            int Response_Code = job1.optInt("Response_Code");
                            String Response_Message = job1.optString("Response_Message");
                            if (Response_Code == 101) {
                                String responseData = job1.optString("Response_Data");
                                JSONArray jsonArray = new JSONArray(responseData);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    String LeaveType = obj.optString("LeaveType");
                                    String StartDate = obj.optString("StartDate");
                                    String EndDate = obj.optString("EndDate");
                                    LeaveValue = obj.optString("LeaveValue");
                                    String Reason = obj.optString("Reason");

                                    Log.e(TAG, "LeaveValue: "+LeaveValue);

                                    PrevieModel spModel = new PrevieModel(LeaveType, StartDate, EndDate, LeaveValue, Reason);
                                    previewItem.add(spModel);
                                }

                                PreviewAdapter preAdapter = new PreviewAdapter(previewItem, getContext());
                                rvPreviewItem.setAdapter(preAdapter);
                            } else {

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "BIND_VIEW_SUMMERY_error: "+anError);
                    }
                });
    }


    private String encodeFileToBase64Binary(File yourFile) {
        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encoded;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPDFPathFromURI(Uri contentURI) {
        final String id = DocumentsContract.getDocumentId(contentURI);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void LeaveSave(JSONObject object) {
        Log.e(TAG, "LeaveSave: object: "+object);
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        AndroidNetworking.post(Api.sLeaveAdd)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e(TAG, "LEAVE_SAVE: "+response.toString());
                                pd.dismiss();
                                JSONObject job1 = response;
                                int Response_Code = job1.optInt("Response_Code");
                                String Response_Message = job1.optString("Response_Message");
                                if (Response_Code == 101) {
                                    successAlert();
                                } else {
                                    Toast.makeText(getContext(),Response_Message,Toast.LENGTH_LONG).show();
                                    alert3.dismiss();
                                    pd.dismiss();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "LEAVE_SAVE_onError: "+anError);
                                pd.dismiss();
                            }
                        });
    }


    private void successAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_success, null);
        dialogBuilder.setView(dialogView);
        TextView tvInvalidDate = (TextView) dialogView.findViewById(R.id.tvSuccess);
        if (pref.getLanguage().equals("hi")) {
            tvInvalidDate.setText("सफलतापूर्वक लागू किया गया");
        } else {
            tvInvalidDate.setText("Leave has been successfully applied");
        }


        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialog1.dismiss();
                alert3.dismiss();
                ((LeaveApplicationActivity) getContext()).loadDetailsFragment();
            }
        });

        alerDialog1 = dialogBuilder.create();
        alerDialog1.setCancelable(true);
        Window window = alerDialog1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alerDialog1.show();
    }


    private void successAlertForEncashment(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_success, null);
        dialogBuilder.setView(dialogView);
        TextView tvInvalidDate = (TextView) dialogView.findViewById(R.id.tvSuccess);
        if (pref.getLanguage().equals("hi")) {
            tvInvalidDate.setText("सफलतापूर्वक लागू किया गया");
        } else {
            tvInvalidDate.setText("Leave Encashment Application Saved successfully and Mail has been sent to your Approver.");
        }


        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialog1.dismiss();
                clearDataForEncashment();
                rbLeaveApplication.setChecked(true);
                //llLeaveApplication.setVisibility(View.VISIBLE);
                //llLeaveEncashment.setVisibility(View.GONE);
            }
        });

        alerDialog1 = dialogBuilder.create();
        alerDialog1.setCancelable(true);
        Window window = alerDialog1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alerDialog1.show();
    }



    private void getCompOffDetails2(JSONObject object) {
        Log.e(TAG, "getCompOffDetails2: called");
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        /*String surl = pref.getIpAddress()+"ghrmsapi/api/Leave/CompBreakUp?CompanyID=" + pref.getEmpClintId() + "&EmployeeID=" + pref.getEmpId() + "&StartDate=" + startDate + "&EndDate=" + endDate + "&LeaveTypeID=" + typeId + "&Iscompoff=" + category + "&SecurityCode=" + pref.getSecurityCode();
        Log.d("printcompff", surl);*/

        AndroidNetworking.post(Api.sGetCompOffBreakUp)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject job1 = response;
                        try {
                            int Response_Code = job1.optInt("Response_Code");
                            String Response_Message = job1.optString("Response_Message");
                            if (Response_Code == 101) {
                                String responseData = job1.optString("Response_Data");
                                JSONArray jsonArray = new JSONArray(responseData);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String CoffDate = obj.optString("CoffDate");
                                    String LeaveValue = obj.optString("LeaveValue");

                                    CompOffDetailsModel spModel = new CompOffDetailsModel(CoffDate, LeaveValue);
                                    compOffList.add(spModel);
                                }
                                compOffAdapter = new CompOffAdapter(compOffList, ApplicationFragment.this, getContext());
                                rvCompOffItem.setAdapter(compOffAdapter);
                            } else {

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void checkPermissionForFile() {
        Dexter.withContext(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Log.e("onPermissionsGranted", "Called");
                            showChooseFileDialog();
                        } else {
                            Toast.makeText(getActivity(), "Permissions are required to perform app functionality.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*public void setEncashmentValue(int pos,String leaveNumber, String leaveCode){
        if(pos == 0){
            LeaveTypeId_1 = encashItemList.get(pos).getLeaveTypeID();

            encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
            totalEncasementValue = encashItemList.get(pos).getEncaseLeaveCount();
            etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));

            //totalEncasementValue = encashItemList.get(pos).getEncaseLeaveCount();
            //etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
           *//* if (Integer.parseInt(leaveNumber) == 0){
                encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
                totalEncasementValue -= encashItemList.get(pos).getEncaseLeaveCount();
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            } else if (encashItemList.get(pos).getEncaseLeaveCount() == 0){
                Log.e(TAG, "setEncashmentValue: 1 ");
                encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
                totalEncasementValue = encashItemList.get(pos).getEncaseLeaveCount();
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            } else if(encashItemList.get(pos).getEncaseLeaveCount() > Integer.parseInt(leaveNumber)){
                Log.e(TAG, "setEncashmentValue: 2 ");
                encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
                totalEncasementValue -= encashItemList.get(pos).getEncaseLeaveCount();
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            } else if(encashItemList.get(pos).getEncaseLeaveCount() < Integer.parseInt(leaveNumber)) {
                Log.e(TAG, "setEncashmentValue: 3 ");
                encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
                totalEncasementValue = encashItemList.get(pos).getEncaseLeaveCount();
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            }*//*
            *//*if (s.toString().trim().isEmpty() && etPlcNumber.getText().toString().isEmpty()){
                etTotalNumberOfEncasement.setText("");
            } else if (s.toString().trim().isEmpty() && !etPlcNumber.getText().toString().isEmpty()){
                totalEncasementValue =  Integer.parseInt(etPlcNumber.getText().toString());
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            }else if (!s.toString().trim().isEmpty() && !etPlcNumber.getText().toString().isEmpty()){
                totalEncasementValue = Integer.parseInt(s.toString().trim()) + Integer.parseInt(etPlcNumber.getText().toString());
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            } else if (!s.toString().trim().isEmpty()){
                totalEncasementValue = Integer.parseInt(s.toString().trim()) + 0;
                etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
            }*//*
        } else {
            LeaveTypeId_2 = encashItemList.get(pos).getLeaveTypeID();
            encashItemList.get(pos).setEncaseLeaveCount(Integer.parseInt(leaveNumber));
            totalEncasementValue = encashItemList.get(pos).getEncaseLeaveCount();
            etTotalNumberOfEncasement.setText(String.valueOf(totalEncasementValue));
        }
    }*/

    private void login(JSONObject jsonObject) {
        final ProgressDialog pd = new ProgressDialog(getContext());
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

                                JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("CompanyID",pref.getEmpClintId());
                                    jsonObject.put("EmployeeID",pref.getEmpId());
                                    jsonObject.put("ApproverID",pref.getEmpId());
                                    jsonObject.put("SecurityCode",pref.getSecurityCode());
                                    getLeaveAllDetails(jsonObject);
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
}
