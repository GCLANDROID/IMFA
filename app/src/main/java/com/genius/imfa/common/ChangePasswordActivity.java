package com.genius.imfa.common;

import static com.genius.imfa.Utility.Util.SECRET_KEY;
import static com.genius.imfa.Utility.Util.encrypt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.genius.imfa.Utility.Api;
import com.genius.imfa.Utility.Pref;
import com.genius.imfa.databinding.ActivityChangePasswordBinding;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    ActivityChangePasswordBinding binding;
    Pref pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_change_password);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        onClick();
    }

    private void initView() {
        pref = new Pref(getApplicationContext());
    }

    private void onClick() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etNewPassword.getText().toString().length() > 0) {
                    if (binding.etConfirmPassword.getText().toString().length() > 0) {
                        if (binding.etNewPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())) {
                            if (binding.etOLDPassword.getText().toString().length()>0) {
                                JSONObject jsonObject=new JSONObject();
                                try {
                                    Log.e(TAG, "onClick: OLD: "+binding.etOLDPassword.getText().toString().trim());
                                    Log.e(TAG, "onClick: NEW: "+binding.etNewPassword.getText().toString().trim());
                                    jsonObject.put("EmployeeId",pref.getEmpId());
                                    jsonObject.put("NewPassword",encrypt(binding.etNewPassword.getText().toString().trim(), SECRET_KEY));
                                    jsonObject.put("ExistingPassword",encrypt(binding.etOLDPassword.getText().toString().trim(), SECRET_KEY));
                                    jsonObject.put("SecurityCode",pref.getSecurityCode());
                                    changePassword(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                binding.etOLDPassword.setError("Please Enter Old Password");
                                binding.etOLDPassword.requestFocus();
                            }
                        } else {
                            binding.etConfirmPassword.setError("Confirm password should be same with new password");
                            binding.etConfirmPassword.requestFocus();
                        }
                    } else {
                        binding.etConfirmPassword.setError("Please enter confirm Password");
                        binding.etConfirmPassword.requestFocus();
                    }
                } else {
                    binding.etNewPassword.setError("Please enter new Password");
                    binding.etNewPassword.requestFocus();
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etConfirmPassword.setText("");
                binding.etNewPassword.setText("");
            }
        });

        binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void changePassword(JSONObject jsonObject) {
        Log.e(TAG, "changePassword: "+jsonObject.toString());
        final ProgressDialog pd = new ProgressDialog(ChangePasswordActivity.this);
        pd.setMessage("Loading..");
        pd.setCancelable(false);
        pd.show();

        AndroidNetworking.post(Api.sChangePasswordApi)
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization", "Bearer "+pref.getAccessToken())
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        pd.show();
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "CHANGE_PASSWORD: "+ response.toString());
                        pd.dismiss();
                        JSONObject job = response;
                        int  Response_Code = job.optInt("Response_Code");
                        String responseText = job.optString("Response_Message");
                        if (Response_Code==101) {
                            Intent intent=new Intent(ChangePasswordActivity.this,LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //finish();
                        } else {
                            Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
                        }
                        // boolean _status = job1.getBoolean("status");
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pd.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                });
    }
}