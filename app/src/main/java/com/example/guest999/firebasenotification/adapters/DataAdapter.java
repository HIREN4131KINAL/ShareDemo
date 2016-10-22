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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private static final int RESULT_LOAD_FILE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static ArrayList<HashMap<String, String>> file_paths = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;

    public DataAdapter(Context mcontext, ArrayList<HashMap<String, String>> file_paths) {
        context = mcontext;
        inflater = LayoutInflater.from(context);
        DataAdapter.file_paths = file_paths;
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

        } else {
            return RESULT_LOAD_IMAGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        Log.e("layout code: ", viewType + "");

        if (viewType == RESULT_LOAD_FILE) {
            v = inflater
                    .inflate(R.layout.raw_file, parent, false);
            return new FilePick(v);
        } else {
            v = inflater
                    .inflate(R.layout.raw_image, parent, false);
            return new ImagePick(v);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String ad_date = file_paths.get(position).get(Config.CURRENT_DATE);
        String ad_time = file_paths.get(position).get(Config.CURRENT_TIME);


        Log.e("onBindViewHolder:pos ", String.valueOf(position));

        if (holder instanceof FilePick) {
            FilePick file = (FilePick) holder;
            String pdfname = file_paths.get(position).get(Config.TAG_DATA).replace("http://www.laxmisecurity.com/android/uploads/", "");

            file.temp.setText(pdfname);
            file.textview_time.setText(ad_time);
//            file.textview_date.setText(ad_date);
            if (file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
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
        } else if (holder instanceof ImagePick) {
            ImagePick image = (ImagePick) holder;
            image.textview_time.setText(ad_time);
            image.textview_date.setText(ad_date);
        /*    Picasso.with(context)
                    .load(file_paths.get(position).get(Config.TAG_DATA))
                    .placeholder(R.drawable.placeholder)
                    .resize(180, 220)
                    .into(image.score);*/

            Glide.with(context)
                    .load(file_paths.get(position).get(Config.TAG_DATA))
                    .override(180, 220).centerCrop().placeholder(R.drawable.circular_progress_bar)
                    .into(image.score);

            if (file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
                image.status.setText(R.string.received);

                image.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

            } else {
                image.status.setText(R.string.sent);
                image.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
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

    private class FilePick extends ViewHolder {
        TextView temp;
        TextView status, textview_time, textview_date;
        LinearLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        FilePick(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.file_name);
            this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
        }
    }

    private class ImagePick extends ViewHolder {
        ImageView score;
        TextView status, textview_time, textview_date;
        LinearLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        ImagePick(View v) {
            super(v);
            this.score = (ImageView) v.findViewById(R.id.image_list);
            this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);

        }
    }
}
