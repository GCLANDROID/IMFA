package com.genius.imfa.Leave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.genius.imfa.common.UserDashboardActivity;
import com.genius.imfa.Leave.fragment.OtherApplicationFragment;
import com.genius.imfa.Leave.fragment.OtherDetailsFragment;
import com.genius.imfa.Leave.fragment.OtherLeaveApproverFragment;
import com.genius.imfa.R;
import com.genius.imfa.databinding.ActivityOtherLeavesBinding;

public class OtherLeavesActivity extends AppCompatActivity {
    ActivityOtherLeavesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_leaves);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        binding = ActivityOtherLeavesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadOtherApplicationFragment();

        btnClick();



    }

    private void btnClick() {
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherLeavesActivity.this, UserDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        binding.llApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOtherApplicationFragment();
            }
        });

        binding.llDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOtherDetailsFragment();
            }
        });

        binding.llApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOtherLeaveApproverFragment();
            }
        });

    }


    public void loadOtherApplicationFragment() {
        binding.llApplication.setBackgroundResource(R.drawable.background_1);
        binding.llDetails.setBackgroundResource(R.drawable.background_5);
        binding.llApproval.setBackgroundResource(R.drawable.background_5);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OtherApplicationFragment pfragment=new OtherApplicationFragment();
        transaction.replace(R.id.frameLayout, pfragment);
        transaction.commit();
    }

    public void loadOtherDetailsFragment() {
        binding.llApplication.setBackgroundResource(R.drawable.background_5);
        binding.llDetails.setBackgroundResource(R.drawable.background_1);
        binding.llApproval.setBackgroundResource(R.drawable.background_5);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OtherDetailsFragment pfragment=new OtherDetailsFragment();
        transaction.replace(R.id.frameLayout, pfragment);
        transaction.commit();
    }
    //OtherLeaveApproverFragment

    public void loadOtherLeaveApproverFragment() {
        binding.llApplication.setBackgroundResource(R.drawable.background_5);
        binding.llDetails.setBackgroundResource(R.drawable.background_5);
        binding.llApproval.setBackgroundResource(R.drawable.background_1);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        OtherLeaveApproverFragment pfragment=new OtherLeaveApproverFragment();
        transaction.replace(R.id.frameLayout, pfragment);
        transaction.commit();
    }

    public void  approverHidden(){
        binding.llApproval.setVisibility(View.GONE);
    }

    public void  approverVisibility(){
        binding.llApproval.setVisibility(View.VISIBLE);
    }
}