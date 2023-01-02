package com.project.weatherman.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.weatherman.R;
import com.project.weatherman.activity.BlogDetailsActivity;
import com.project.weatherman.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter_Blog extends RecyclerView.Adapter<Adapter_Blog.ContactViewHolder> implements Filterable {
    private List<HashMap<String, String>> modelExpanseLists = new ArrayList<>();
    private Context mContext;
    private ItemFilter mFilter = new ItemFilter();

    public Adapter_Blog(Context mContext, List<HashMap<String, String>> modelExpanseLists) {
        this.modelExpanseLists = modelExpanseLists;
        this.mContext = mContext;
    }
    @Override
    public Adapter_Blog.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_blog_list, null);
        Adapter_Blog.ContactViewHolder contactViewHolder = new Adapter_Blog.ContactViewHolder(view);
        return contactViewHolder;
    }
    @Override
    public void onBindViewHolder(Adapter_Blog.ContactViewHolder holder, int position) {
        HashMap<String, String> modelRefeerList = modelExpanseLists.get(position);

        String id = modelRefeerList.get(HomeFragment.TAG_ID);
        String location_id = modelRefeerList.get(HomeFragment.TAG_LOCATION_ID);
        String gif_image = modelRefeerList.get(HomeFragment.TAG_GIF_IMAGE);
        String title = modelRefeerList.get(HomeFragment.TAG_TITLE);
        String body_content = modelRefeerList.get(HomeFragment.TAG_BODY_CONTENT);
        String image = modelRefeerList.get(HomeFragment.TAG_IMAGE);
        String category_id = modelRefeerList.get(HomeFragment.TAG_CATEGORY_ID);

        holder.name.setText(title);
        holder.description.setText(body_content);
        Glide.with(mContext).load(image).into(holder.image);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = modelRefeerList.get(HomeFragment.TAG_TITLE);
                String body_content = modelRefeerList.get(HomeFragment.TAG_BODY_CONTENT);
                String image = modelRefeerList.get(HomeFragment.TAG_IMAGE);

                Intent intent = new Intent(mContext, BlogDetailsActivity.class);

                intent.putExtra("title", title);
                intent.putExtra("body_content", body_content);
                intent.putExtra("image", image);

                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return modelExpanseLists.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;

    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name,description;
        ImageView image;
        LinearLayout lyt_parent;
        public ContactViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.lyt_parent);
        }

    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            List<String> list = new ArrayList<String>(modelExpanseLists.size());
            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);
            String filterableString ;
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            modelExpanseLists = (List<HashMap<String, String>>)filterResults.values;
            notifyDataSetChanged();
        }
    }
    public void filterList(List<HashMap<String,String>> filteredList) {
        modelExpanseLists = filteredList;
        notifyDataSetChanged();
    }
}