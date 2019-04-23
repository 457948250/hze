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

public class FitnessPushAdapter extends RecyclerView.Adapter<FitnessPushAdapter.ViewHolder> {

    private List<item_Push> mFitnessPush;

    private Context context;

    public FitnessPushAdapter(Context context, List<item_Push> mFitnessPush){
        this.context = context;
        this.mFitnessPush = mFitnessPush;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        View pushView;
        SimpleDraweeView iv_push_healthy;
        TextView tv_title_healthy;
        TextView tv_browse;

        public ViewHolder(View view){
            super(view);
            pushView = view;
            iv_push_healthy = (SimpleDraweeView)view.findViewById(R.id.iv_push_healthy);
            tv_title_healthy = (TextView)view.findViewById(R.id.tv_title_healthy);
            tv_browse = (TextView)view.findViewById(R.id.tv_browse);
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_push,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.pushView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Toast.makeText(v.getContext(),"你点击了第"+position+"个item",Toast.LENGTH_SHORT).show();
//                item_Push item_Push = mHealthyPush.get(position);
//                Intent intent = new Intent("com.healthy.com.healing.ITEMTRACKQUERY");
//                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        item_Push item_Push = mFitnessPush.get(position);
        holder.iv_push_healthy.setImageResource(item_Push.getImageId());
        holder.tv_title_healthy.setText(item_Push.getTitle());
        holder.tv_browse.setText(item_Push.getBrowse());
    }

    @Override
    public int getItemCount() {
        return mFitnessPush.size();
    }
}
