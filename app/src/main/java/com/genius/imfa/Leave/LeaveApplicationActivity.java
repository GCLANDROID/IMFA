package com.genius.imfa.Leave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.genius.imfa.Leave.fragment.ApplicationFragment;
import com.genius.imfa.Leave.fragment.ApproverFragment;
import com.genius.imfa.Leave.fragment.DetailsFragment;

import com.genius.imfa.common.UserDashboardActivity;
import com.genius.imfa.R;
import com.genius.imfa.databinding.ActivityLeaveApplicationBinding;


public class LeaveApplicationActivity extends AppCompatActivity {
    ActivityLeaveApplicationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_leave_application);
        binding = ActivityLeaveApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnClick();
        loadApplicationFragment();
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
                Intent intent = new Intent(LeaveApplicationActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.llApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadApplicationFragment();
            }
        });

        binding.llDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDetailsFragment();
            }
        });

        binding.llApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadApproverFragment();
            }
        });
    }

    public void loadApplicationFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ApplicationFragment pfragment=new ApplicationFragment();
        transaction.replace(R.id.frameLayout, pfragment);
        transaction.commit();
    }

    public void loadApproverFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ApproverFragment efr=new ApproverFragment();
        transaction.replace(R.id.frameLayout, efr);
        transaction.commit();
    }

    public void loadDetailsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        DetailsFragment htfragment=new DetailsFragment();
        transaction.replace(R.id.frameLayout, htfragment);
        transaction.commit();
    }

    public void  approverVisibility(){
        binding.llApproval.setVisibility(View.VISIBLE);
    }

    public void  approverHidden(){
        binding.llApproval.setVisibility(View.GONE);
    }
}