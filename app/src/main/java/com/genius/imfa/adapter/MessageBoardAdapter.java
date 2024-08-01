package com.genius.imfa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.genius.imfa.R;

import java.util.ArrayList;

public class MessageBoardAdapter extends RecyclerView.Adapter<MessageBoardAdapter.MyViewHolder>{
    ArrayList<String> messageArray;

    public MessageBoardAdapter(ArrayList<String> messageArray) {
        this.messageArray = messageArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.massage_dashboard_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtMessage.setText(messageArray.get(position));
    }

    @Override
    public int getItemCount() {
        return messageArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}
