package com.genius.imfa.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.genius.imfa.R;
import com.genius.imfa.databinding.RawBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    Context context;
    JSONArray itemList;
    Date date1,date2;
    int flag;

    public AttendanceAdapter(Context context, JSONArray itemList, int flag) {
        this.context=context;
        this.itemList=itemList;
        this.flag=flag;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RawBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.raw, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final JSONObject jsonObject = itemList.optJSONObject(position);
        holder.binding.tvDate.setText(jsonObject.optString("EmpAttendanceDate"));
        holder.binding.tvInTime.setText(jsonObject.optString("EmpInTime"));
        holder.binding.tvOutTime.setText(jsonObject.optString("EmpOutTime"));
        if (jsonObject.optString("EmpInRemarks").equalsIgnoreCase("QR")){
            holder.binding.tvType.setText("QR Attendance");
        }else {
            holder.binding.tvType.setText("Geo Attendance");
        }

        holder.binding.tvReason.setText(jsonObject.optString("PunchFromReason"));
        holder.binding.tvStatus.setText(jsonObject.optString("EmpApprovalStatus"));
        if (!jsonObject.optString("EmpInAddress").equalsIgnoreCase("")){
            holder.binding.tvLocation.setText(jsonObject.optString("EmpInAddress"));

            holder.binding.lnLocation.setVisibility(View.VISIBLE);
        }else {
            holder.binding.tvLocation.setText(jsonObject.optString("EmpOutAddress"));
            holder.binding.lnLocation.setVisibility(View.GONE);
        }

        holder.binding.tvOutLocation.setText(jsonObject.optString("EmpOutAddress"));
        holder.binding.tvInImage.setText(jsonObject.optString("EmpInFname"));
        holder.binding.tvOutImage.setText(jsonObject.optString("EmpOutFname"));


        if (jsonObject.optString("PunchFrom")==null || jsonObject.optString("PunchFrom").equals("")){
            holder.binding.llType.setVisibility(View.GONE);
        }else {
            holder.binding.llType.setVisibility(View.VISIBLE);
        }

        if (jsonObject.optString("PunchFromReason")==null || jsonObject.optString("PunchFromReason").equals("")){
            holder.binding.llReason.setVisibility(View.GONE);
        }else {
            holder.binding.llReason.setVisibility(View.VISIBLE);
        }

        if (jsonObject.optString("AttendanceNature")==null || jsonObject.optString("AttendanceNature").equals("")){

        }else {


            holder.binding.llInImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jsonObject.optString("EmpInFnameUrl")!=null ||!jsonObject.optString("EmpInFnameUrl").equals("")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.optString("EmpInFnameUrl")));
                        context.startActivity(browserIntent);

                    }
                }
            });

            holder.binding.llInImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jsonObject.optString("EmpOutFnameUrl")!=null ||!jsonObject.optString("EmpOutFnameUrl").equals("")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.optString("EmpOutFnameUrl")));
                        context.startActivity(browserIntent);

                    }
                }
            });
        }

        if (!jsonObject.optString("EmpOutFnameUrl").equals("")) {
            holder.binding.llOutImage.setVisibility(View.VISIBLE);
        }else {
            holder.binding.llOutImage.setVisibility(View.GONE);
        }

        if (!jsonObject.optString("EmpInFnameUrl").equals("")) {
            holder.binding.llInImage.setVisibility(View.VISIBLE);
        }else {
            holder.binding.llInImage.setVisibility(View.GONE);
        }



        if (!jsonObject.optString("EmpOutTime").equals("")){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");


            try {
               date1 = simpleDateFormat.parse(jsonObject.optString("EmpInTime"));
               date2 = simpleDateFormat.parse(jsonObject.optString("EmpOutTime"));
                long difference = date2.getTime() - date1.getTime();
                int days = (int) (difference / (1000*60*60*24));
                int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                hours = (hours < 0 ? -hours : hours);
                holder.binding.tvTotalHrs.setText("Total "+hours+" Hrs.");
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }else {
            holder.binding.tvTotalHrs.setText("");
        }

        if (flag==1){
            holder.binding.lnPunchingStatus.setVisibility(View.GONE);
        }else {
            holder.binding.lnPunchingStatus.setVisibility(View.VISIBLE);
            holder.binding.tvPunchingStatus.setText(jsonObject.optString("PunchFromStatus"));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RawBinding binding;
        public ViewHolder(@NonNull RawBinding binding) {
            super(binding.lnMin);
            this.binding=binding;
        }
    }
}
