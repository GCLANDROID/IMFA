package com.genius.imfa.common;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.Utility.RefreshAccessToken;
import com.genius.imfa.Utility.ValidUtils;
import com.genius.imfa.databinding.ActivityProfileBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    ActivityProfileBinding binding;
    Pref pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        onClick();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
        JSONObject object=new JSONObject();
        try {
            object.put("AEMConsultantID",pref.getEmpConId());
            object.put("AEMClientID",pref.getEmpClintId());
            object.put("AEMClientOfficeID",pref.getEmpClintOffId());
            object.put("AEMEmployeeID",pref.getEmpId());
            object.put("WorkingStatus","1");
            object.put("CurrentPage","1");
            object.put("SecurityCode",pref.getSecurityCode());
            profile(object);
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
                Intent intent = new Intent(ProfileActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.llOfficial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (binding.expandableLayout.isExpanded()){
                    binding.expandableLayout.setExpanded(false);
                } else {
                    binding.expandableLayout.setExpanded(true);
                }*/
            }
        });

        binding.llPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eLayoutPersonal.isExpanded()){
                    binding.eLayoutPersonal.setExpanded(false);
                } else {
                    binding.eLayoutPersonal.setExpanded(true);
                }
            }
        });

        binding.llMiscellaneous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eLayoutMiscellaneous.isExpanded()){
                    binding.eLayoutMiscellaneous.setExpanded(false);
                } else {
                    binding.eLayoutMiscellaneous.setExpanded(true);
                }
            }
        });

        binding.llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.eLayoutContact.isExpanded()){
                    binding.eLayoutContact.setExpanded(false);
                } else {
                    binding.eLayoutContact.setExpanded(true);
                }
            }
        });
    }


    private void profile(JSONObject jsonObject) {
        Log.e(TAG, "profile: "+jsonObject.toString());
        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();
        AndroidNetworking.post(Api.sProfileApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject job1 = response;
                        Log.e(TAG, "PROFILE_DETAILS: " + job1);
                        pd.dismiss();

                        int Response_Code = job1.optInt("Response_Code");
                        if (Response_Code == 101) {
                            // Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();

                            //JSONArray responseData = job1.optJSONArray("Response_Data");
                            String responseData = job1.optString("Response_Data");
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(responseData);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.optJSONObject(i);
                                final String AEMEmployeeID = obj.optString("AEMEmployeeID");
                                // tvEmplId.setText(AEMEmployeeID);
                                final String ID = AEMEmployeeID;
                                binding.tvEmplId.setText(ID);


                                //code feild
                                final String Code = obj.optString("Code");
                                pref.saveempCode(Code);
                                if (pref.getLanguage().equals("hi")) {


                                } else {
                                    binding.tvEmpCode.setText(Code);
                                }


                                //Name field
                                final String Name = obj.optString("Name").toUpperCase();
                                pref.saveempName(Name);
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler1 = new Handler();

                                } else {
                                    binding.tvEName.setText(Name);
                                    binding.tvEmpName.setText(Name);
                                }


                                //DOJ

                                final String DateOfJoining = obj.optString("DOJ");
                                binding.tvDOJ.setText(DateOfJoining);


                                final String Department = obj.optString("Department");
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler4 = new Handler();

                                } else {
                                    binding.tvDepartment.setText(Department);
                                }
                                final String Branch = ValidUtils.getFreshValue(obj.optString("Branch"), "-");
                                if (pref.getLanguage().equals("hi")) {


                                } else {
                                    binding.tvBranchName.setText(Branch);
                                }


                                final String Designation = obj.optString("Designation");
                                if (pref.getLanguage().equals("hi")) {

                                } else {
                                    binding.tvDesignation.setText(Designation);
                                }

                                final String Location = obj.optString("Location");

                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler6 = new Handler();

                                } else {
                                    binding.tvLocation.setText(Location);
                                }

                                final String Sex = obj.optString("Sex");
                                if (pref.getLanguage().equals("hi")) {

                                } else {
                                    binding.tvGender.setText(Sex);
                                }

                                final String DateOfBirth = obj.optString("DateOfBirth");
                                binding.tvDateOfBirth.setText(DateOfBirth);


                                final String GuardianName = obj.optString("GuardianName");
                                if (pref.getLanguage().equals("hi")) {


                                } else {
                                    binding.tvGurdianName.setText(GuardianName);
                                }

                                final String RelationShip = obj.optString("RelationShip");
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler10 = new Handler();
                                } else {
                                    binding.tvRealtionShip.setText(RelationShip);
                                }

                                final String GuardName = obj.optString("GuardName");
                                binding.tvEmergencyName.setText(GuardName);
                                String Qualification = obj.optString("Qualification");
                                Log.e(TAG, "Qualification: "+obj.optString("Qualification"));
                                binding.tvQualification.setText(Qualification);
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler11 = new Handler();

                                } else {

                                }

                                final String MaritalStatus = obj.optString("MaritalStatus");
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler12 = new Handler();

                                } else {
                                    binding.tvMarital.setText(MaritalStatus);
                                }

                                final String BloodGroup = obj.optString("BloodGroup");
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler13 = new Handler();

                                } else {
                                    binding.tvBloodGroup.setText(BloodGroup);
                                }

                                final String permanentpincode = obj.optString("PermanentPinCode");

                                //final String PermanentAddress = obj.optString("PermanentAddress");
                                final String PermanentAddress = (obj.optString("PermanentAddress").equals("null"))? "N/A":obj.optString("PermanentAddress");
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler14 = new Handler();

                                } else {
                                    if (permanentpincode.equals("null")) {
                                        Log.d("null", permanentpincode);
                                        binding.tvParAddr.setText(PermanentAddress);
                                    } else {
                                        Log.d("value", permanentpincode);
                                        binding.tvParAddr.setText(PermanentAddress + "," + permanentpincode);
                                    }
                                    // tvParAddr.setText(PermanentAddress+","+permanentpincode);
                                }


                                final String presentpincode = obj.optString("PresentPincode");


                                final String PresentAddress = (obj.optString("PresentAddress").equals(("null"))?"N/A":obj.optString("PresentAddress"));
                                if (pref.getLanguage().equals("hi")) {
                                    final Handler textViewHandler15 = new Handler();
                                } else {
                                    if (presentpincode.equals("null")) {
                                        Log.d("null", presentpincode);
                                        binding.tvPreAddr.setText(PresentAddress);
                                    } else {
                                        Log.d("value", presentpincode);
                                        binding.tvPreAddr.setText(PresentAddress + "," + presentpincode);
                                    }
                                    //tvPreAddr.setText(PresentAddress+","+presentpincode);
                                }

                                String Mobile = obj.optString("Mobile");
                                if (!Mobile.equals("")) {
                                   binding.tvPhnNumber.setText(Mobile);
                                } else {
                                    binding.tvPhnNumber.setText("N/A");
                                }

                                final String EmailID = obj.optString("EmailID");

                                binding.tvEmail.setText(EmailID);


                                String PFNumber = obj.optString("PFNumber");
                                if (!PFNumber.equals("")) {
                                    binding.tvPfNumber.setText(PFNumber);
                                } else {
                                    binding.tvPfNumber.setText("N/A");
                                }

                                String ESINumber = obj.optString("ESINumber");
                                if (!ESINumber.equals("")) {
                                    binding.tvEsiNumber.setText(ESINumber);
                                }

                                final String BankName = obj.optString("BankName");
                                binding.tvBankName.setText(BankName);
                                if (pref.getLanguage().equals("hi")) {

                                } else {

                                }


                                String AccountNumber = obj.optString("AccountNumber");
                                if (!AccountNumber.equals("")) {
                                    binding.tvAcNumber.setText(AccountNumber);
                                } else {
                                    binding.tvAcNumber.setText("N/A");
                                }

                                String AadharCard = obj.optString("AadharCard");
                                if (!AadharCard.equals("")) {
                                    binding.tvAddharNumber.setText(AadharCard);
                                } else {
                                    binding.tvAddharNumber.setText("N/A");
                                }

                                String UanNo = obj.optString("UanNo");
                                if (!UanNo.equals("")) {
                                    binding.tvUanNumber.setText(UanNo);
                                } else if (UanNo.equals("null")){
                                    binding.tvUanNumber.setText("N/A");
                                } else {
                                    binding.tvUanNumber.setText("N/A");
                                }
                                String panNo = obj.optString("PanNo");
                                if (!panNo.equals("")) {
                                    binding.tvPanNumber.setText(panNo);
                                } else {
                                    binding.tvPanNumber.setText("N/A");
                                }

                                String ReportingManager = obj.optString("ReportingManager");
                                binding.tvReportingManager.setText(ReportingManager);
                                String PersonalEmail = obj.optString("PersonalEmail");
                                binding.tvPersonalEmail.setText(PersonalEmail);
                                String GuardContMobile = obj.optString("GuardContMobile");
                                binding.tvGurdianMob.setText(GuardContMobile);

                                final String Level = obj.optString("Grade");
                                binding.tvGrade.setText(Level);
                            }

                           /* if (pref.getSecurityCode().equals("1155")) {
                                //profileImage();
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("EmployeeID", pref.getEmpId());
                                    object.put("SecurityCode", pref.getSecurityCode());
                                    profileImage2(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {}*/


                            // boolean _status = job1.getBoolean("status");


                            // do anything with response
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        pd.dismiss();
                        if (error.getErrorCode()==401){
                            RefreshAccessToken.apiCall(ProfileActivity.this, new RefreshAccessToken.OnResponse() {
                                @Override
                                public void onPostCall() {

                                }

                                @Override
                                public void onSuccess() {
                                    JSONObject object=new JSONObject();
                                    try {
                                        object.put("AEMConsultantID",pref.getEmpConId());
                                        object.put("AEMClientID",pref.getEmpClintId());
                                        object.put("AEMClientOfficeID",pref.getEmpClintOffId());
                                        object.put("AEMEmployeeID",pref.getEmpId());
                                        object.put("WorkingStatus","1");
                                        object.put("CurrentPage","1");
                                        object.put("SecurityCode",pref.getSecurityCode());
                                        profile(object);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                });
    }
}