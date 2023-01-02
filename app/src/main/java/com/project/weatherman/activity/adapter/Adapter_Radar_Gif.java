package com.project.weatherman.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.weatherman.R;
import com.project.weatherman.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_Radar_Gif extends RecyclerView.Adapter<Adapter_Radar_Gif.ContactViewHolder> {
    private List<HashMap<String, String>> modelExpanseLists = new ArrayList<>();
    private Context mContext;

    public Adapter_Radar_Gif(Context mContext, List<HashMap<String, String>> modelExpanseLists) {
        this.modelExpanseLists = modelExpanseLists;
        this.mContext = mContext;
    }

    @Override
    public Adapter_Radar_Gif.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_radar_list, null);
        Adapter_Radar_Gif.ContactViewHolder contactViewHolder = new Adapter_Radar_Gif.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(Adapter_Radar_Gif.ContactViewHolder holder, int position) {
        HashMap<String, String> modelRefeerList = modelExpanseLists.get(position);

        String id = modelRefeerList.get(HomeFragment.TAG_ID);
        String location_id = modelRefeerList.get(HomeFragment.TAG_LOCATION_ID);
        String gif_image = modelRefeerList.get(HomeFragment.TAG_GIF_IMAGE);
        String title = modelRefeerList.get(HomeFragment.TAG_TITLE);
        String body_content = modelRefeerList.get(HomeFragment.TAG_BODY_CONTENT);
        String image = modelRefeerList.get(HomeFragment.TAG_IMAGE);
        String category_id = modelRefeerList.get(HomeFragment.TAG_CATEGORY_ID);

        holder.txtName.setText(title);
//        Glide.with(mContext).load(image).into(holder.imgView);

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(mContext).load(image).into(HomeFragment.imgGifView);
            }
        });
    }
    @Override
    public int getItemCount() {
        return modelExpanseLists.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgView;
        public ContactViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            imgView = (ImageView) itemView.findViewById(R.id.imgView);
        }

    }
}