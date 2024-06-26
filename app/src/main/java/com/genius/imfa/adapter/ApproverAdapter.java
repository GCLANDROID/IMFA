package com.genius.imfa.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.genius.imfa.Leave.fragment.ApproverFragment;
import com.genius.imfa.Model.ApprovalModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.FindDocumentInformation;
import com.genius.imfa.Utility.Pref;

import java.io.IOException;
import java.util.ArrayList;

public class ApproverAdapter extends RecyclerView.Adapter<ApproverAdapter.MyViewHolder> {
    ArrayList<ApprovalModel>itemList=new ArrayList<>();
    Fragment context;
    Context mContex;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approver_raw,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        final Pref pref=new Pref(mContex);
        final ProgressDialog pd=new ProgressDialog(mContex);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        final ApprovalModel approvalModel = itemList.get(i);

        myViewHolder.tvStrtDate.setText(itemList.get(i).getStartDate());
        myViewHolder.tvEndDate.setText(itemList.get(i).getEndDate());
        myViewHolder.tvValue.setText(itemList.get(i).getValue());
        myViewHolder.llApprovedBy.setVisibility(View.GONE);
        myViewHolder.tvAvlbalanceValue.setText(itemList.get(i).getAvlbalance());


        if (itemList.get(i).getApprovalStatus().equals("Pending") ||itemList.get(i).getApprovalStatus().contains("Cancel") ){
            myViewHolder.llTick.setVisibility(View.VISIBLE);
            myViewHolder.llGreen.setVisibility(View.GONE);
            myViewHolder.llYellow.setVisibility(View.GONE);
        }else {
            myViewHolder.llTick.setVisibility(View.GONE);
            myViewHolder.llGreen.setVisibility(View.GONE);
            myViewHolder.llYellow.setVisibility(View.GONE);
        }

        if (approvalModel.isSelected()){
            myViewHolder.imgTick.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.imgTick.setVisibility(View.GONE);
        }

        myViewHolder.llTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                approvalModel.setSelected(!approvalModel.isSelected());
                // holder.view.setBackgroundColor(attandanceModel.isSelected() ? Color.CYAN : Color.WHITE);

                if (approvalModel.isSelected()) {

                    myViewHolder.imgTick.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();

                    ((ApproverFragment) context).updateAttendanceStatus(i, true );



                } else {
                    /*myViewHolder.imgFrstHalf.setVisibility(View.GONE);
                    myViewHolder.imgScndHalf.setVisibility(View.GONE);
                    myViewHolder.imgFull.setVisibility(View.GONE);*/
                    myViewHolder.imgTick.setVisibility(View.GONE);
                    ((ApproverFragment) context).updateAttendanceStatus(i, false);
                    itemList.get(i).setSelected(false);
                    notifyDataSetChanged();
                }


            }
        });

        if (pref.getLanguage().equals("hi")){
            myViewHolder.tvName.setText("कर्मचारी का नाम:");
            myViewHolder.tvType.setText("छुट्टी का प्रकार:");
            myViewHolder.tvleaveStrtDate.setText("आरंभ तिथि:");
            myViewHolder.tvLeaveEndDate.setText("अंतिम तिथि:");
            myViewHolder.tvLeaveValue.setText("मूल्य:");
            myViewHolder.tvLeaveReason.setText("कारण:");
        }else {
            myViewHolder.tvName.setText("Emp. Name:");
            myViewHolder.tvType.setText("Leave:");
            myViewHolder.tvleaveStrtDate.setText("Start date:");
            myViewHolder.tvLeaveEndDate.setText("End date:");
            myViewHolder.tvLeaveValue.setText("Value:");
            myViewHolder.tvLeaveReason.setText("Reason:");

        }

        //empname

        if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler2 = new Handler();
            //new AsyncTask<Void, Void, Void>() .execute();
        } else {
            myViewHolder.tvEmpName.setText(itemList.get(i).getEmpName());
        }

        //type

        if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler2 = new Handler();
            //new AsyncTask<Void, Void, Void>() .execute();
        } else {
            myViewHolder.tvLeaveType.setText(itemList.get(i).getLeave());
        }

        //reason

        if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler2 = new Handler();
            //new AsyncTask<Void, Void, Void>() .execute();
        } else {
            myViewHolder.tvReason.setText(itemList.get(i).getReason());
        }

        if (itemList.get(i).getApprovalStatus().contains("Cancel")){
            myViewHolder.tvStatus.setText("Cancel request from applicant");
            myViewHolder.llTick.setVisibility(View.GONE);
            myViewHolder.lnDelete.setVisibility(View.VISIBLE);
        }else if (itemList.get(i).getApprovalStatus().contains("Approved")){
            myViewHolder.tvStatus.setText(itemList.get(i).getApprovalStatus());
            myViewHolder.llTick.setVisibility(View.GONE);
            myViewHolder.lnDelete.setVisibility(View.GONE);
        }else if (itemList.get(i).getApprovalStatus().contains("Rejected")){
            myViewHolder.tvStatus.setText(itemList.get(i).getApprovalStatus());
            myViewHolder.llTick.setVisibility(View.GONE);
            myViewHolder.lnDelete.setVisibility(View.GONE);
        }else {
            myViewHolder.tvStatus.setText(itemList.get(i).getApprovalStatus());
            myViewHolder.llTick.setVisibility(View.VISIBLE);
            myViewHolder.lnDelete.setVisibility(View.VISIBLE);
        }


        if (itemList.get(i).getIsLink()==1){
            myViewHolder.tvDocument.setVisibility(View.VISIBLE);
        }else {
            myViewHolder.tvDocument.setVisibility(View.GONE);
        }

        myViewHolder.tvDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((ApproverFragment)context).imageAlert(itemList.get(i).getDocumentlink());
                Log.e("E", "onClick: "+itemList.get(i).getDocumentlink());

                try {
                    ((ApproverFragment)context).showPdfView(
                            FindDocumentInformation.getFileType(itemList.get(i).getDocumentlink()),
                            FindDocumentInformation.getBase64Url(itemList.get(i).getDocumentlink()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        myViewHolder.lnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ApproverFragment)context).deleteFunction(itemList.get(i).getmId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmpName,tvLeaveType,tvStrtDate,tvEndDate,tvValue,tvReason,tvStatus,tvDocument,tvAvlbalanceValue;
        LinearLayout llTick,llGreen,llYellow,lnDelete,llApprovedBy;
        ImageView imgTick;
        TextView tvLeaveReason,tvLeaveValue,tvLeaveEndDate,tvleaveStrtDate,tvType,tvName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmpName=(TextView)itemView.findViewById(R.id.tvEmpName);
            tvLeaveType=(TextView)itemView.findViewById(R.id.tvLeaveType);
            tvStrtDate=(TextView)itemView.findViewById(R.id.tvStrtDate);
            tvValue=(TextView)itemView.findViewById(R.id.tvValue);
            tvEndDate=(TextView)itemView.findViewById(R.id.tvEndDate);
            tvReason=(TextView)itemView.findViewById(R.id.tvReason);
            tvDocument=(TextView)itemView.findViewById(R.id.tvDocument);
            imgTick=(ImageView)itemView.findViewById(R.id.imgTick);
            llTick=(LinearLayout)itemView.findViewById(R.id.llTick);
            llGreen=(LinearLayout)itemView.findViewById(R.id.llGreen);
            llYellow=(LinearLayout)itemView.findViewById(R.id.llYellow);
            lnDelete=(LinearLayout)itemView.findViewById(R.id.lnDelete);

            tvLeaveReason=(TextView)itemView.findViewById(R.id.tvLeaveReason);
            tvLeaveValue=(TextView)itemView.findViewById(R.id.tvLeaveValue);
            tvLeaveEndDate=(TextView)itemView.findViewById(R.id.tvLeaveEndDate);
            tvleaveStrtDate=(TextView)itemView.findViewById(R.id.tvleaveStrtDate);
            tvType=(TextView)itemView.findViewById(R.id.tvType);
            tvName=(TextView)itemView.findViewById(R.id.tvName);
            tvStatus=(TextView)itemView.findViewById(R.id.tvStatus);
            llApprovedBy=(LinearLayout) itemView.findViewById(R.id.llApprovedBy);
            tvAvlbalanceValue=(TextView) itemView.findViewById(R.id.tvAvlbalanceValue);
        }
    }

    public ApproverAdapter(ArrayList<ApprovalModel> itemList, Fragment context, Context mContext) {
        this.itemList = itemList;
        this.context = context;
        this.mContex=mContext;
    }
}
