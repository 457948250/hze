package com.healthy.com.healing.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.healthy.com.healing.R;

import java.util.List;

public class ActivityPushAdapter extends RecyclerView.Adapter<ActivityPushAdapter.ViewHolder> {

    private List<item_Push_Activity> mActivityPush;

    private Context context;

    public ActivityPushAdapter(Context context, List<item_Push_Activity> mActivityPush){
        this.context = context;
        this.mActivityPush = mActivityPush;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        View pushView;
        SimpleDraweeView iv_push_activity;
        TextView tv_title_activity;
        TextView tv_join;

        public ViewHolder(View view){
            super(view);
            pushView = view;
            iv_push_activity = (SimpleDraweeView)view.findViewById(R.id.iv_push_activity);
            tv_title_activity = (TextView)view.findViewById(R.id.tv_title_activity);
            tv_join = (TextView)view.findViewById(R.id.tv_join);
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_push_activity,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.pushView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Toast.makeText(v.getContext(),"第"+position+"个活动",Toast.LENGTH_SHORT).show();
//                item_Push item_Push = mHealthyPush.get(position);
//                Intent intent = new Intent("com.healthy.com.healing.ITEMTRACKQUERY");
//                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        item_Push_Activity item_Push_Activity = mActivityPush.get(position);
        holder.iv_push_activity.setImageResource(item_Push_Activity.getImageId());
        holder.tv_title_activity.setText(item_Push_Activity.getTitle());
        holder.tv_join.setText(item_Push_Activity.getJoin());
    }

    @Override
    public int getItemCount() {
        return mActivityPush.size();
    }
}