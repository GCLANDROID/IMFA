package com.genius.imfa.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.genius.imfa.Leave.fragment.OtherDetailsFragment;
import com.genius.imfa.Model.AdjustmentDetailsModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.TimeDateConverter;
import com.genius.imfa.wfh.WFHDetailsFragment;

import java.util.ArrayList;

public class WFHDetailsAdapter extends RecyclerView.Adapter<WFHDetailsAdapter.MyViewHolder>{

    Context context;
    ArrayList<AdjustmentDetailsModel> adjustmentList;
    Fragment fContext;
    public WFHDetailsAdapter(Context context, ArrayList<AdjustmentDetailsModel> adjustmentList, Fragment fContext) {
        this.context = context;
        this.adjustmentList = adjustmentList;
        this.fContext = fContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.adjustment_report_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {


        String[] split = adjustmentList.get(position).AppliedDate.split("T");
        String firstSubString = split[0];
        holder.tvAdjustmentType.setText(adjustmentList.get(position).AdjustmentType);

            holder.tvLeaveStrtDate.setText("Start Date:");
            holder.tvLeaveEndDate.setText("End Date:");

        holder.tvAppliedDate.setText(TimeDateConverter.convert_Date_YYYY_MM_DD_To_dd_MMM_yyyy(firstSubString));

        holder.tvStrtDate.setText(adjustmentList.get(position).StartDate);
        holder.llNoOfDays.setVisibility(View.VISIBLE);
        holder.tvNoOfDays.setText(adjustmentList.get(position).NoOfDays);

        holder.tvEndDate.setText(adjustmentList.get(position).EndDate);
        holder.tvReason.setText(adjustmentList.get(position).Reason);
        holder.tvStatus.setText(adjustmentList.get(position).ApprovalStatus);
        holder.tvApprovedBy.setText((adjustmentList.get(position).ApprovedBY.equals("null"))?"--":adjustmentList.get(position).ApprovedBY);
        holder.tvApprover.setText((adjustmentList.get(position).ApprovedBY.equals("null"))?"--":adjustmentList.get(position).ApprovedBY);
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WFHDetailsFragment) fContext).deleteAdjustments(adjustmentList.get(position).AID);
            }
        });

        if (adjustmentList.get(position).Isdelete==0){
            holder.imgDelete.setVisibility(View.VISIBLE);
        }else {
            holder.imgDelete.setVisibility(View.GONE);
        }

        if (adjustmentList.get(position).ApprovalStatus.equals("Rejected")){
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return adjustmentList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvAdjustmentType,tvAppliedDate,tvStrtDate,tvEndDate,tvLeaveStrtDate,tvLeaveEndDate,tvReason,tvStatus,tvApprovedBy,tvApprover,tvNoOfDays;
        ImageView imgDelete;
        LinearLayout llNoOfDays;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdjustmentType = itemView.findViewById(R.id.tvAdjustmentType);
            tvAppliedDate = itemView.findViewById(R.id.tvAppliedDate);
            tvStrtDate = itemView.findViewById(R.id.tvStrtDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvApprovedBy = itemView.findViewById(R.id.tvApprovedBy);
            tvLeaveStrtDate = itemView.findViewById(R.id.tvLeaveStrtDate);
            tvLeaveEndDate = itemView.findViewById(R.id.tvLeaveEndDate);
            tvApprover = itemView.findViewById(R.id.tvApprover);
            imgDelete = itemView.findViewById(R.id.imgDelete);

            llNoOfDays=(LinearLayout) itemView.findViewById(R.id.llNoOfDays);
            tvNoOfDays=(TextView) itemView.findViewById(R.id.tvNoOfDays);
        }
    }
}
