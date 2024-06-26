package com.genius.imfa.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.genius.imfa.Model.HoliDayModel;
import com.genius.imfa.R;
import com.genius.imfa.Utility.Pref;

import java.util.ArrayList;


public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.MyViewHolder> {
    ArrayList<HoliDayModel>holidayList=new ArrayList<>();
    Context context;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.holiday_raw,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final Pref pref=new Pref(context);
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Loading..");
        pd.setCancelable(true);

        myViewHolder.tvPurpose.setText(holidayList.get(i).getPurpose());
        myViewHolder.tvDay.setText(holidayList.get(i).getHolidayDay());
        myViewHolder.tvDate.setText(holidayList.get(i).getHolidayDate());

        /*if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler3 = new Handler();

        }else {
            myViewHolder.tvPurpose.setText(holidayList.get(i).getPurpose());
        }

        if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler3 = new Handler();

        }else {
            myViewHolder.tvDay.setText(holidayList.get(i).getHolidayDay());
        }

        if (pref.getLanguage().equals("hi")) {
            final Handler textViewHandler3 = new Handler();

        }else {
            myViewHolder.tvDate.setText(holidayList.get(i).getHolidayDate());
        }*/
    }

    @Override
    public int getItemCount() {
        return holidayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate,tvPurpose,tvDay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate=(TextView)itemView.findViewById(R.id.tvDate);
            tvPurpose=(TextView)itemView.findViewById(R.id.tvPurpose);
            tvDay=(TextView)itemView.findViewById(R.id.tvDay);


        }
    }

    public HolidayAdapter(ArrayList<HoliDayModel> holidayList, Context context) {
        this.holidayList = holidayList;
        this.context = context;
    }
}
