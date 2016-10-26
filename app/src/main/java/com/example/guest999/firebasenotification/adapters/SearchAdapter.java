package com.example.guest999.firebasenotification.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harshad on 26-10-2016 at 09:50 AM.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    // Declare Variables
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> dataTemp;
    private ArrayList<HashMap<String, String>> data;
    private ImageLoader imageLoader;
    private HashMap<String, String> resultp = new HashMap<>();

    private AssociationFilter filter;


    public SearchAdapter(Context context, ArrayList<HashMap<String, String>> user_info) {
        dataTemp = user_info;
        this.context=context;
        Log.e( "SearchAdapter: ",dataTemp+" ");
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new SearchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {

        resultp = dataTemp.get(position);
       holder.tv_name.setText(resultp.get(Config.KEY_USERNAME));
       holder.tv_phone.setText(resultp.get(Config.KEY_PHONE));
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new AssociationFilter();
        }
        return filter;
    }

    private class AssociationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = data;
                results.count = data.size();
            } else {
                ArrayList<HashMap<String, String>> preferenceAssociationList = new ArrayList<>();
                for (HashMap<String, String> assocation : data) {

                    if (assocation.get(Config.KEY_USERNAME).toLowerCase().trim()
                            .startsWith(constraint.toString().toLowerCase().trim())) {
                        preferenceAssociationList.add(assocation);
                    }
                    if (assocation.get(Config.KEY_PHONE).toLowerCase().trim()
                            .startsWith(constraint.toString().toLowerCase().trim())) {
                        preferenceAssociationList.add(assocation);
                    }
                }
                results.values = preferenceAssociationList;
                results.count = preferenceAssociationList.size();
            }
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("count "+results.count);
            if (results.count == 0) {
                notifyDataSetChanged();
            } else {
                dataTemp = (ArrayList<HashMap<String, String>>) results.values;
                System.out.println("size "+dataTemp.size());
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataTemp.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView tv_name, tv_phone;

        ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.m_name);
            tv_phone = (TextView) itemView.findViewById(R.id.phone_number);
        }
    }
}
