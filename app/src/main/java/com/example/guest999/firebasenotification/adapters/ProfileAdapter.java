package com.example.guest999.firebasenotification.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.activities.Change_pass;
import com.example.guest999.firebasenotification.activities.Login;
import com.example.guest999.firebasenotification.utilis.ServiceGetSet;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;

import java.util.ArrayList;

/**
 * Created by Guest999 on 9/22/2016.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {
    private   Context mContext;
    private ArrayList<ServiceGetSet> albumList;

    public ProfileAdapter(Context context, ArrayList<ServiceGetSet> androidVersions) {
        this.mContext = context;
        this.albumList = androidVersions;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageView thumbnail;
        LinearLayout linearLayout;


        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.rec_pro_name);
            thumbnail = (ImageView) view.findViewById(R.id.rec_pro_icon);
            linearLayout = (LinearLayout) view.findViewById(R.id.profile_layout);
        }
    }

    @Override
    public ProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_profile, parent, false);

        return new ProfileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.MyViewHolder holder, final int position) {
        ServiceGetSet album = albumList.get(position);
        holder.title.setText(album.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {
                    Intent intent = new Intent(mContext, Change_pass.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else if (position == 1) {
                    logout();
                }else if(position==2){

                }
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(mContext)
                .setTitle("Logout")
                .setMessage("Would you like to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //SharedPreferenceManager.getDefaults_boolean("notification", false, DataSharing_forUser.this);
                        //settings.edit().clear().apply();

                        Intent logout = new Intent(mContext, Login.class);
                        // this flag prevent back to in to application after logout.
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SharedPreferenceManager.ClearAllPreferences(mContext);
                        Login.settings.edit().clear().apply();
                        mContext.startActivity(logout);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();

    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
