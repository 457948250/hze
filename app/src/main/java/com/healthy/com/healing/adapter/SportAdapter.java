package com.healthy.com.healing.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthy.com.healing.R;

import java.util.List;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder> {

    private List<item_SportRecord> mSportRecord;

    private Context context;

    public SportAdapter(Context context,List<item_SportRecord> sportRecord){
        this.context = context;
        this.mSportRecord = sportRecord;
    }
  static class ViewHolder extends RecyclerView.ViewHolder{
      View sportView;
      ImageView IV_record_sportstyle;
      TextView TV_record_distance;
      TextView TV_record_time;
      TextView TV_record_speed;
      TextView TV_record_day;

      public ViewHolder(View view){
          super(view);
          sportView = view;
          IV_record_sportstyle = (ImageView)view.findViewById(R.id.IV_record_sportstyle);
          TV_record_distance = (TextView)view.findViewById(R.id.TV_record_distance);
          TV_record_time = (TextView)view.findViewById(R.id.TV_record_time);
          TV_record_speed = (TextView)view.findViewById(R.id.TV_record_speed);
          TV_record_day = (TextView)view.findViewById(R.id.TV_record_day);
      }
  }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport_record,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.sportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                item_SportRecord item_sportRecord = mSportRecord.get(position);
                Intent intent = new Intent("com.healthy.com.healing.ITEMTRACKQUERY");
                intent.putExtra("starttime",item_sportRecord.getStarttime());
                intent.putExtra("endtime",item_sportRecord.getEndtime());
                context.startActivity(intent);
            }
        });
      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SportAdapter.ViewHolder holder, int position) {
        item_SportRecord item_sportRecord = mSportRecord.get(position);
        holder.IV_record_sportstyle.setImageResource(item_sportRecord.getImageId());
        holder.TV_record_distance.setText(item_sportRecord.getDistance());
        holder.TV_record_time.setText(item_sportRecord.getTime());
        holder.TV_record_speed.setText(item_sportRecord.getSpeed());
        holder.TV_record_day.setText(item_sportRecord.getDay());
    }

    @Override
    public int getItemCount() {
      return mSportRecord.size();
    }
}
