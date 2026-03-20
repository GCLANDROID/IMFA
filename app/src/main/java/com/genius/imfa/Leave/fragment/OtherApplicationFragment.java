package com.genius.imfa.Leave.fragment;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.developers.imagezipper.ImageZipper;
import com.genius.imfa.Utility.FileUtils;
import com.genius.imfa.common.AndroidXCameraActivity;
import com.genius.imfa.Leave.OtherLeavesActivity;
import com.genius.imfa.Model.AdjustmentModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.FileToBase64Converter;
import com.genius.imfa.Utility.FindDocumentInformation;
import com.genius.imfa.Utility.ImageUtils;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.RealPathUtil;
import com.genius.imfa.Utility.TimeDateConverter;
import com.genius.imfa.adapter.SpinnerAdjustmentAdapter;
import com.genius.imfa.databinding.FragmentOtherApplicationBinding;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import id.zelory.compressor.Compressor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherApplicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherApplicationFragment extends Fragment {
    private static final String TAG = "OtherApplicationFragmen";
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    FragmentOtherApplicationBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherApplicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherApplicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherApplicationFragment newInstance(String param1, String param2) {
        OtherApplicationFragment fragment = new OtherApplicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Pref pref;
    ArrayList<AdjustmentModel> adjustmentList = new ArrayList<>();
    SpinnerAdjustmentAdapter spinnerAdjustmentAdapter;

    String LeaveTypeID = "";
    String startDate="", endDate="";

    ActivityResultLauncher<Intent> mSelectDocumentImages,mAndroidXCameraLunch;
    ActivityResultLauncher<String[]> requestPermissionsLauncher;
    AlertDialog alert4;
    String realPath,fileType;
    File compressedImageFile,file;

    private Uri imageUri;
    String base64image;
    boolean isFileSelectedFlag = false;
    String COMPENSATORY_OFF_ID = "13_2_1",SUBSTITUTE_HOLIDAY_ID="14_2_1";
    AlertDialog alerDialog1;
    String imageName;
    File compressFile;
    File pdffile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtherApplicationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        btnClick();
        registerInitialize();
    }

    private void initView() {
        pref = new Pref(getActivity());

        JSONObject object=new JSONObject();
        try {
            object.put("AEMEmployeeID",pref.getEmpId());
            object.put("CompanyID",pref.getEmpClintId());
            object.put("SecurityCode",pref.getSecurityCode());
            getApproverOrNot(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.tvEmpName.setText("Other leave application of "+pref.getEmpName());

        /*JSONObject object=new JSONObject();
        try {
            object.put("CompanyID",pref.getEmpClintId());
            object.put("EmployeeID",pref.getEmpId());
            object.put("SecurityCode",pref.getSecurityCode());
            getAdjustmentApplicationAllDetails(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void getApproverOrNot(JSONObject object) {
        Log.e(TAG, "getApproverOrNot: called: "+object.toString());
        /*final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();*/
        binding.llLoader.setVisibility(View.VISIBLE);
        AndroidNetworking.post(Api.sApproverCheckApi)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*mApplicantList.clear();
                        applicantList.clear();
                        applicantList.add("Please select");
                        mApplicantList.add(new SpinnerModel("0", "0"));*/

                        //pd.dismiss();
                        binding.llLoader.setVisibility(View.GONE);
                        JSONObject job1 = response;
                        Log.e(TAG, "LEAVE_APPROVER: " + job1);

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                            String responseData = job1.optString("Response_Data");
                            try {
                                JSONArray jsonArray=new JSONArray(responseData);
                                Log.e(TAG, "onResponse: "+jsonArray.length());
                                if (jsonArray.length()>0) {
                                    ((OtherLeavesActivity) getContext()).approverVisibility();
                                } else {
                                    ((OtherLeavesActivity) getContext()).approverHidden();
                                }

                                JSONObject object=new JSONObject();
                                try {
                                    object.put("CompanyID",pref.getEmpClintId());
                                    object.put("EmployeeID",pref.getEmpId());
                                    object.put("SecurityCode",pref.getSecurityCode());
                                    getAdjustmentApplicationAllDetails(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // boolean _status = job1.getBoolean("status");
                            // do anything with response
                        }else {
                            JSONObject object=new JSONObject();
                            try {
                                object.put("CompanyID",pref.getEmpClintId());
                                object.put("EmployeeID",pref.getEmpId());
                                object.put("SecurityCode",pref.getSecurityCode());
                                getAdjustmentApplicationAllDetails(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ((OtherLeavesActivity) getContext()).approverHidden();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        //pd.dismiss();
                        binding.llLoader.setVisibility(View.GONE);
                        JSONObject object=new JSONObject();
                        try {
                            object.put("CompanyID",pref.getEmpClintId());
                            object.put("EmployeeID",pref.getEmpId());
                            object.put("SecurityCode",pref.getSecurityCode());
                            getAdjustmentApplicationAllDetails(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ((OtherLeavesActivity) getContext()).approverHidden();
                    }
                });
    }

    private void btnClick() {
        binding.imgStrtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LeaveTypeID.equals("")) {
                    showStartDatePicker();
                } else {
                    Toast.makeText(getContext(), "Please select Leave type", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.imgEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.tvStrtDate.getText().toString().equals("")) {
                    showEndDatePicker();
                } else {
                    Toast.makeText(getActivity(), "Please Select start date.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.llChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    String[] permissionsArray = {READ_MEDIA_IMAGES,READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED};
                    requestPermissionsLauncher.launch(permissionsArray);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    String[] permissionsArray = {READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
                    requestPermissionsLauncher.launch(permissionsArray);
                } else {
                    //String[] permissionsArray = {READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};
                    //requestPermissionsLauncher.launch(permissionsArray);
                    checkPermissionForFile();
                }
            }
        });

        binding.llPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LeaveTypeID.equals("0")){
                    Toast.makeText(getActivity(), "Please select Adjustment Type", Toast.LENGTH_SHORT).show();
                } else if (startDate.isEmpty()){
                    if (LeaveTypeID.equals(COMPENSATORY_OFF_ID) || LeaveTypeID.equals(SUBSTITUTE_HOLIDAY_ID)){
                        Toast.makeText(getActivity(), "Please select Off Date", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Please select Start Date", Toast.LENGTH_SHORT).show();
                    }
                } else if (endDate.isEmpty()){
                    if (LeaveTypeID.equals(COMPENSATORY_OFF_ID) || LeaveTypeID.equals(SUBSTITUTE_HOLIDAY_ID)){
                        Toast.makeText(getActivity(), "Please select Leave Date", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Please select End Date", Toast.LENGTH_SHORT).show();
                    }
                } else if (binding.etReason.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Please enter your reason", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject object=new JSONObject();
                    try {
                        object.put("CompanyID",pref.getEmpClintId());
                        object.put("EmployeeID",pref.getEmpId());
                        object.put("StartDate",startDate);
                        object.put("EndDate",endDate);
                        object.put("ITime","");
                        object.put("OTime","");
                        object.put("DayMode","0");
                        object.put("ODType", LeaveTypeID);
                        object.put("ClienrName","");
                        object.put("CreatedBy",pref.getEmpId());
                        object.put("Remarks",binding.etReason.getText().toString().trim());
                        object.put("IsmultiDays","1");
                        object.put("AID","0");
                        object.put("imageName",imageName);
                        object.put("byteData",base64image);
                        object.put("contentType",fileType);
                        object.put("SecurityCode",pref.getSecurityCode());
                        Log.e(TAG, "saveLeaveApplication: "+object);
                        saveLeaveApplication(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void saveLeaveApplication(JSONObject object) {
        Log.e(TAG, "saveLeaveApplication: "+object.toString());
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        AndroidNetworking.post(Api.sSaveODApplicationDetails)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "SAVE_OTHER_LEAVE: "+response.toString());
                        pd.dismiss();
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message=job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            //Toast.makeText(getActivity(), Response_Message, Toast.LENGTH_SHORT).show();
                            successAlert(Response_Message);
                        } else {
                            Toast.makeText(getActivity(), Response_Message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pd.dismiss();
                        Log.e(TAG, "SAVE_OTHER_LEAVE_onError: "+anError);
                    }
                });
    }

    private void showEndDatePicker() {
        final Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = (monthOfYear + 1);
                        endDate = month + "/" + dayOfMonth + "/" + year;
                        binding.tvEndDate.setText(TimeDateConverter.convert_Date_MM_DD_YYYY_To_dd_MMM_yyyy(endDate));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();
    }

    private void showStartDatePicker() {
        final Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = (monthOfYear + 1);
                        startDate = month + "/" + dayOfMonth + "/" + year;
                        binding.tvStrtDate.setText(TimeDateConverter.convert_Date_MM_DD_YYYY_To_dd_MMM_yyyy(startDate));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();

    }

    private void getAdjustmentApplicationAllDetails(JSONObject object) {
        /*final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();*/

        binding.llLoader.setVisibility(View.VISIBLE);

        Log.e(TAG, "getGetAdjustmentGrid: "+object.toString());
        AndroidNetworking.post(Api.sGetAdjustmentApplicationAllDetails)
                .addJSONObjectBody(object)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Adjustment_Details: "+response.toString());
                        //pd.dismiss();
                        binding.llLoader.setVisibility(View.GONE);
                        JSONObject job1 = response;
                        int Response_Code = job1.optInt("Response_Code");
                        String Response_Message = job1.optString("Response_Message");
                        if (Response_Code == 101) {
                            String Response_Data = job1.optString("Response_Data");
                            Log.e(TAG, "onResponse: "+Response_Data.toString());
                            adjustmentList.add(new AdjustmentModel("0","Select Adjustment Type"));
                            try {
                                JSONObject responseData = new JSONObject(Response_Data);
                                Log.e(TAG, "onResponse: "+responseData.optString("Table3") );
                                JSONArray jsonTable1 = new JSONArray(responseData.optString("Table1"));
                                JSONArray jsonArray = new JSONArray(responseData.optString("Table3"));
                                Log.e(TAG, "onResponse: jsonArray: "+jsonTable1.length());

                                for (int i = 0; i < jsonTable1.length(); i++) {
                                    JSONObject object = jsonTable1.optJSONObject(i);
                                    binding.tvApproverName.setText("Approver Name: "+object.optString("ApproverName"));
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.optJSONObject(i);
                                    adjustmentList.add(new AdjustmentModel(
                                            object.optString("ID"),
                                            object.optString("NAME")
                                    ));
                                }
                                spinnerAdjustmentAdapter = new SpinnerAdjustmentAdapter(getContext(),adjustmentList);
                                binding.spAdjustment.setAdapter(spinnerAdjustmentAdapter);

                                binding.spAdjustment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        AdjustmentModel adjustmentModel = adjustmentList.get(position);
                                        if (adjustmentModel.id.equals("0")){
                                            LeaveTypeID = adjustmentModel.id;
                                            binding.tvStartDateName.setText("Start Date");
                                            binding.tvEndDateName.setText("End Date");
                                        } else if (adjustmentModel.id.equals(COMPENSATORY_OFF_ID) || adjustmentModel.id.equals(SUBSTITUTE_HOLIDAY_ID)){
                                            LeaveTypeID = adjustmentModel.id;
                                            binding.tvStartDateName.setText("Off Date");
                                            binding.tvEndDateName.setText("Leave Date");
                                        } else {
                                            LeaveTypeID = adjustmentModel.id;
                                            binding.tvStartDateName.setText("Start Date");
                                            binding.tvEndDateName.setText("End Date");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            /*String Table3 = Response_Data.optString("Table3");
                            Log.e(TAG, "onResponse: Table3: "+Table3.length());*/
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        //pd.dismiss();
                        binding.llLoader.setVisibility(View.GONE);
                        Log.e(TAG, "Adjustment_Grid_anError: "+anError);
                    }
                });
    }


    private void registerInitialize() {
        mSelectDocumentImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 100) {
                            isFileSelectedFlag = true;
                            alert4.dismiss();
                            //selectDocument.cancel();
                            String imageURl = result.getData().getStringExtra("image").toString();
                            Uri uri = Uri.parse(imageURl);

                            Log.e(TAG, "onActivityResult: "+imageURl);
                            imageName = FindDocumentInformation.FileNameFromURL(imageURl);
                            Log.e(TAG, "onActivityResult: imageName: "+imageName);

                            boolean isImageTooLarge = ImageUtils.isImageGreaterThan2MB(getActivity(), uri);
                            if (isImageTooLarge) {
                                Log.e(TAG, "isImageTooLarge: true");
                                // Image is larger than 2 MB
                               /* compressedImageFile = new Compressor.Builder(getActivity())
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
                                base64image = ImageUtils.fileToBase64(compressedImageFile).replaceAll("\n","");
                                //Log.e(TAG, "base64Image: ==================="+base64image );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, "mSelectDocumentImages: "+uri.getPath());
                            binding.imgPic.setImageURI(uri);
                            //base64image = FileToBase64Converter.imageToBase642(compressedImageFile.getPath());
                            String encodedImage;
                            Log.e(TAG, "onActivityResult: base64image testing: "+base64image);

                            realPath = getRealPath(getActivity(),uri);
                            //fileType = "png";
                            //fileType = "image/"+FindDocumentInformation.FindFileTypeFromDocumentName(imageName);
                            fileType = FindDocumentInformation.FindFileTypeFromDocumentName(imageName);
                            Log.e(TAG, "onActivityResult: "+fileType);
                            binding.txtPdfUrl.setVisibility(View.GONE);
                            binding.imgPic.setVisibility(View.VISIBLE);

                           /* if (realPath.endsWith(".pdf")) {
                                Log.e(TAG, "FILE TYPE: "+fileType);
                                fileType = ".pdf";
                            } else {
                                Log.e(TAG, "FILE TYPE: "+fileType);

                            }*/
                        } else {
                            if (result.getData() != null){
                                isFileSelectedFlag = true;
                                alert4.dismiss();
                                Uri selectedFileUri = result.getData().getData();
                                Log.e(TAG, "onActivityResult: "+selectedFileUri.getPath());
                                Log.e(TAG, "onActivityResult: REAL PATH: "+getRealPath(getActivity(),selectedFileUri));
                                realPath = getRealPath(getActivity(),selectedFileUri);
                                String docName = FindDocumentInformation.FileNameFromURL(realPath);
                                imageName = FindDocumentInformation.FileNameFromURL(realPath);
                                if (realPath.endsWith("pdf")) {
                                    try {
                                        base64image = FileToBase64Converter.convertToBase64(convertInputStreamToFile(selectedFileUri,docName)).replaceAll("\n","");
                                        //Log.e(TAG, "onActivityResult: base64image: "+base64image);
                                    } catch (IOException e) {
                                        //Log.e(TAG, "onActivityResult: ERROR");
                                        throw new RuntimeException(e);
                                    }

                                    //fileType = "application/pdf";
                                    fileType = "pdf";
                                    Log.e(TAG, "FILE TYPE: "+fileType);
                                    binding.txtPdfUrl.setText(selectedFileUri.getPath());
                                    binding.txtPdfUrl.setVisibility(View.VISIBLE);
                                    binding.imgPic.setVisibility(View.VISIBLE);
                                    Drawable myDrawable = getResources().getDrawable(R.drawable.pdf_1);
                                    binding.imgPic.setImageDrawable(myDrawable);
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
                                                    .compressToFile(compressedImageFile);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                                    } else {
                                        // Image is 2 MB or smaller
                                        Log.e(TAG, "isImageTooLarge: false");
                                        Log.e(TAG, FileUtils.checkFileSize(compressedImageFile.getPath()));
                                    }

                                    try {
                                        base64image = ImageUtils.fileToBase64(compressedImageFile).replaceAll("\n","");
                                        Log.e(TAG, "base64Image: ==================="+base64image);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e(TAG, "FILE TYPE: "+fileType);
                                    //fileType = "png";
                                    fileType = FindDocumentInformation.FindFileTypeFromDocumentName(imageName);
                                    //fileType = "image/"+FindDocumentInformation.FindFileTypeFromDocumentName(imageName);
                                    Log.e(TAG, "onActivityResult: "+fileType);
                                    binding.imgPic.setImageURI(selectedFileUri);
                                    binding.txtPdfUrl.setVisibility(View.GONE);
                                    binding.imgPic.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });




        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            // Check if all permissions were granted
            boolean allPermissionsGranted = true;
            for (Boolean granted : permissions.values()) {
                if (!granted) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions granted, proceed with your logic
                Log.e(TAG, "registerInitialize: called 1");
                showChooseFileDialog();

            } else {
                Log.e(TAG, "registerInitialize: called 2");
                // Permissions were not granted, handle accordingly
            }
        });
    }

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        Log.e("SDK_INT", "= "+ SDK_INT);
        // SDK < API11
        if (SDK_INT < 11) {
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        // SDK >= 11 && SDK < 19
        else if (SDK_INT < 19) {
            realPath = RealPathUtil.getRealPathFromURI_API11to18(context, fileUri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = RealPathUtil.getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }

    private File convertInputStreamToFile(Uri uri, String fileNme) {
        InputStream inputStream;
        try {
            inputStream = getActivity().getContentResolver().openInputStream(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file= new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), fileNme);

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            Log.e(TAG, "convertInputStreamToFile: file: "+file.getPath());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
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
                Intent intent = new Intent(getContext(), AndroidXCameraActivity.class);
                //startActivityForResult(intent,100);
                mSelectDocumentImages.launch(intent);
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

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
        return cropImg;
    }

    private void successAlert(String response_Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomDialogNew);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_success_2, null);
        dialogBuilder.setView(dialogView);
        TextView tvSuccess = (TextView) dialogView.findViewById(R.id.tvSuccess);
        TextView tvSuccessMessage = (TextView) dialogView.findViewById(R.id.tvSuccessMessage);


        tvSuccess.setText(response_Message);
        tvSuccessMessage.setText("Leave application has been saved.");


        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialog1.dismiss();
                //alert3.dismiss();
                ((OtherLeavesActivity) getContext()).loadOtherDetailsFragment();
            }
        });

        alerDialog1 = dialogBuilder.create();
        alerDialog1.setCancelable(true);
        Window window = alerDialog1.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alerDialog1.show();
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
}