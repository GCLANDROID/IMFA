package com.genius.imfa.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.genius.imfa.Leave.fragment.ApplicationFragment;
import com.genius.imfa.Model.EncashmentItemModel;
import com.genius.imfa.R;

import java.util.ArrayList;

public class LeaveEncasementAdapter extends RecyclerView.Adapter<LeaveEncasementAdapter.MyViewHolder>{
    Context context;
    Fragment fContext;
    ArrayList<EncashmentItemModel> encashItemList;
    ApplicationFragment.EncaseValueListener mEncaseValueListener;
    public LeaveEncasementAdapter(Context context, Fragment fContext, ArrayList<EncashmentItemModel> encashItemList) {
        this.context = context;
        this.fContext = fContext;
        this.encashItemList = encashItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.encasement_leave_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.edtLeaveType.setText(encashItemList.get(position).getLeaveTypeName());
        holder.etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mEncaseValueListener.onValueChange(encashItemList.get(position).getLeaveTypeID(),editable.toString().trim(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return encashItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        EditText edtLeaveType,etNumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            edtLeaveType = itemView.findViewById(R.id.edtLeaveType);
            etNumber = itemView.findViewById(R.id.etNumber);
        }
    }

    public void setEncaseValueListener(ApplicationFragment.EncaseValueListener mEncaseValueListener) {
        this.mEncaseValueListener = mEncaseValueListener;
    }
}
