package com.example.edairycodinground.adapter;

/*
class SpeedListAdapter {
}
*/


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.edairycodinground.R;
import com.example.edairycodinground.model.SpeedModel;

import java.util.ArrayList;

public class SpeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SpeedModel> speedModelArrayList = new ArrayList<>();
    Context context;

    public SpeedListAdapter(ArrayList<SpeedModel> topicsArrayList, Context activity) {
        this.speedModelArrayList = topicsArrayList;
        this.context = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speed_list_item, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemeView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
        try {
            classHolder.internetSpeedTxt.setText("Internet Speed = " + speedModelArrayList.get(position).getTotalSpeed());
            classHolder.timeStampTxt.setText("Time Stamp = " + speedModelArrayList.get(position).getTimeStamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return speedModelArrayList.size();
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView internetSpeedTxt, timeStampTxt;

        public Holder(View itemView) {
            super(itemView);
            internetSpeedTxt = (TextView) itemView.findViewById(R.id.internet_speed_txt);
            timeStampTxt = (TextView) itemView.findViewById(R.id.time_stamp_txt);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
