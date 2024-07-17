package com.genius.imfa.wfh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.genius.imfa.common.UserDashboardActivity;
import com.genius.imfa.R;
import com.genius.imfa.databinding.ActivityWfhApplicationBinding;


public class WFHActivity extends AppCompatActivity {
    ActivityWfhApplicationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_leave_application);
        binding = ActivityWfhApplicationBinding.inflate(getLayoutInflater());
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
                Intent intent = new Intent(WFHActivity.this, UserDashboardActivity.class);
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
        WFHApplication pfragment=new WFHApplication();
        transaction.replace(R.id.frameLayout, pfragment);
        transaction.commit();
    }

    public void loadApproverFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        WFHApproverFragment efr=new WFHApproverFragment();
        transaction.replace(R.id.frameLayout, efr);
        transaction.commit();
    }

    public void loadDetailsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        WFHDetailsFragment htfragment=new WFHDetailsFragment();
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