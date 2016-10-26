package com.example.guest999.firebasenotification.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harshad on 25-10-2016 at 09:46 AM.
 */
public class DataAdapter_User extends RecyclerView.Adapter {
    private static final int RESULT_LOAD_FILE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 99;
    private static ArrayList<HashMap<String, String>> file_paths = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public DataAdapter_User(Context applicationContext, ArrayList<HashMap<String, String>> hello) {
        context = applicationContext;
        inflater = LayoutInflater.from(context);
        DataAdapter_User.file_paths = hello;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {

        // Log.e("getItemViewType:pos ", String.valueOf(position));
        Log.e("IS this PDF ?", String.valueOf(file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf")));
        if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf")) {
            return RESULT_LOAD_FILE;
        } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".png")) {
            return RESULT_LOAD_IMAGE;
        } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".jpg")) {
            return RESULT_LOAD_IMAGE;
        } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".jpeg")) {
            return RESULT_LOAD_IMAGE;
        } else {
            return REQUEST_CODE_PICK_CONTACTS;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        Log.e("layout code: ", viewType + "");

        if (viewType == RESULT_LOAD_FILE) {
            v = inflater
                    .inflate(R.layout.raw_file, parent, false);
            return new DataAdapter_User.FilePick(v);
        } else if (viewType == RESULT_LOAD_IMAGE){
            v = inflater
                    .inflate(R.layout.raw_image, parent, false);
            return new DataAdapter_User.ImagePick(v);
        }else {
            v = inflater
                    .inflate(R.layout.raw_contact, parent, false);
            return new DataAdapter_User.ContactPick(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String ad_date = file_paths.get(position).get(Config.CURRENT_DATE);
        String ad_time = file_paths.get(position).get(Config.CURRENT_TIME);


        Log.e("onBindViewHolder:pos ", String.valueOf(position));

        if (holder instanceof DataAdapter_User.FilePick) {
            DataAdapter_User.FilePick file = (DataAdapter_User.FilePick) holder;
            String pdfname = file_paths.get(position).get(Config.TAG_DATA).replace("http://www.laxmisecurity.com/android/uploads/", "");

            file.temp.setText(pdfname.substring(10));
            file.textview_time.setText(ad_time);
//            file.textview_date.setText(ad_date);
            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
                //file.status.setText(R.string.reveived);

                file.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                file.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

            } else {
                //file.status.setText(R.string.sent);

                file.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                file.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
            }
        } else if (holder instanceof DataAdapter_User.ImagePick) {
            final String Image_name = file_paths.get(position).get(Config.TAG_DATA);
            DataAdapter_User.ImagePick image = (DataAdapter_User.ImagePick) holder;
            image.textview_time.setText(ad_time);
            image.textview_date.setText(ad_date);
            Picasso.with(context)
                    .load(Image_name)
                    .placeholder(R.drawable.placeholder)
                    .resize(180, 220)
                    .into(image.score);

            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
                //gallery.status.setText(R.string.reveived);

                image.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

            } else {
                //gallery.status.setText(R.string.sent);
                image.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
            }
        }
        else if (holder instanceof DataAdapter_User.ContactPick) {
            final String Contact_name = file_paths.get(position).get(Config.TAG_DATA);
            DataAdapter_User.ContactPick contactPick = (DataAdapter_User.ContactPick) holder;


            String[] splited = Contact_name.split("\\s+");
            String phone_no = splited[0];
            String phone_name = splited[1];

            Log.e("onBindViewHolder: ", phone_no);
            Log.e("onBindViewHolder: ", phone_name);

            contactPick.no.setText(phone_no);
            contactPick.name.setText(phone_name);
            contactPick.textview_time.setText(ad_time);
            contactPick.textview_date.setText(ad_date);
            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
                //file.status.setText(R.string.reveived);

                contactPick.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                contactPick.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                contactPick.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

            } else {
                //file.status.setText(R.string.sent);

                contactPick.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                contactPick.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                contactPick.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
            }
        }

    }


    @Override
    public int getItemCount() {
        return file_paths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

    }

    private class FilePick extends DataAdapter_User.ViewHolder {
        TextView temp;
        TextView status, textview_time, textview_date;
        RelativeLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        FilePick(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.file_name);
            //this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (RelativeLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
        }
    }

    private class ImagePick extends DataAdapter_User.ViewHolder {
        ImageView score;
        TextView status, textview_time, textview_date;
        RelativeLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        ImagePick(View v) {
            super(v);
            this.score = (ImageView) v.findViewById(R.id.image_list);
            //this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (RelativeLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);

        }
    }
    private class ContactPick extends DataAdapter_User.ViewHolder {
        TextView status, textview_time, textview_date, no, name;
        RelativeLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        ContactPick(View v) {
            super(v);
            this.no = (TextView) v.findViewById(R.id.contact_no);
            this.name = (TextView) v.findViewById(R.id.contact_name);
            this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (RelativeLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
        }

    }
}
