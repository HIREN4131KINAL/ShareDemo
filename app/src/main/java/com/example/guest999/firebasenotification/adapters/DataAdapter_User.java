package com.example.guest999.firebasenotification.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.DownloadCallBack;
import com.example.guest999.firebasenotification.utilis.DownloadTaskIMG;
import com.example.guest999.firebasenotification.utilis.DownloadTaskPDF;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.kosalgeek.android.caching.FileCacher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.example.guest999.firebasenotification.Config.PhoneFromDevice;

/**
 * Created by Harshad on 25-10-2016 at 09:46 AM.
 */
public class DataAdapter_User extends RecyclerView.Adapter implements DownloadCallBack {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int RESULT_LOAD_FILE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 99;
    private static DownloadTaskIMG downloadTask;
    private static DownloadTaskPDF downloadPdf;
    private static ArrayList<HashMap<String, String>> file_paths = new ArrayList<>();
    //
    private File extStore;
    private String ImageFileName;
    private DownloadCallBack downloadCallBack;
    private File myFile;
    private LayoutInflater inflater = null;
    private Context context;

    public DataAdapter_User(Context applicationContext, ArrayList<HashMap<String, String>> hello) {
        context = applicationContext;
        inflater = LayoutInflater.from(context);
        DataAdapter_User.file_paths = hello;
        downloadCallBack = this;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {

        // Log.e("getItemViewType:pos ", String.valueOf(position));
        Log.e("IS this PDF ?", String.valueOf(file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf")));
        if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".docx") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".doc") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".txt")) {
            return RESULT_LOAD_FILE;
        } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".png") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".jpg") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".jpeg")) {
            return RESULT_LOAD_IMAGE;
        } else {
            return REQUEST_CODE_PICK_CONTACTS;
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_STORAGE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        Log.e("layout code: ", viewType + "");

        if (viewType == RESULT_LOAD_FILE) {
            v = inflater
                    .inflate(R.layout.raw_file, parent, false);
            return new DataAdapter_User.FilePick(v);
        } else if (viewType == RESULT_LOAD_IMAGE) {
            v = inflater
                    .inflate(R.layout.raw_image, parent, false);
            return new DataAdapter_User.ImagePick(v);
        } else {
            v = inflater
                    .inflate(R.layout.raw_contact, parent, false);
            return new DataAdapter_User.ContactPick(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        String ad_date = file_paths.get(position).get(Config.CURRENT_DATE);
        String ad_time = file_paths.get(position).get(Config.CURRENT_TIME);
        PhoneFromDevice = SharedPreferenceManager.getDefaults("phone", context);

        Log.e("onBindViewHolder:pos ", String.valueOf(position));

        if (holder instanceof DataAdapter_User.FilePick) {
            final DataAdapter_User.FilePick file = (DataAdapter_User.FilePick) holder;
            String pdfname = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");

            if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf")) {
                file.file_type_image.setBackgroundResource(R.drawable.pdf);
            } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".docx") || file_paths.get(position).get(Config.TAG_DATA).endsWith(".doc")) {
                file.file_type_image.setBackgroundResource(R.drawable.word);
            } else if (file_paths.get(position).get(Config.TAG_DATA).endsWith(".txt")) {
                file.file_type_image.setBackgroundResource(R.drawable.txt);
            }

            file.temp.setText(pdfname.substring(10));
            file.textview_time.setText(ad_time);
//            file.textview_date.setText(ad_date);
            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(Config.PhoneFromDevice)) {
                //file.status.setText(R.string.reveived);

                try {
                    file.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    file.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

                    file.outgoing_layout_bubble.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //for downloading pdf 07-10-2016
                            //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();

                            Log.e("Download Button Clicked", "**********");
                            //ask for the permission in android M
                            int permission = ContextCompat.checkSelfPermission(context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            if (permission != PackageManager.PERMISSION_GRANTED) {
                                Log.i(TAG, "Permission to storage denied");

                                Toast.makeText(context, "Goto Settings > Apps > GTUExamPapers Enable Storage permission", Toast.LENGTH_LONG).show();

                                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Permission to access the SD-CARD is required for this app to Download PDF. ")
                                            .setTitle("Permission required");

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {
                                            Log.i(TAG, "Clicked");
                                            makeRequest();
                                        }
                                    });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                } else {
                                    makeRequest();
                                }
                            }


                            if (permission == PackageManager.PERMISSION_GRANTED) {

                                Log.e("Download Button Clicked", "**********");

                                File extStore = Environment.getExternalStorageDirectory();
                                File myFile = new File(extStore.getAbsolutePath() + "/FileSharing/" + file.temp.getText().toString());

                                if (!myFile.exists()) {
                                    // execute this when the downloader must be fired
                                    downloadPdf = new DownloadTaskPDF(context, downloadCallBack);
                                    //   downloadTask.execute("http://ia.tranetech.ae:82/upload/uploads/five-point-someone-chetan-bhagat_ebook.pdf",""+finalHolder.tv_paper_name.getText().toString()+".pdf");
                                    downloadPdf.execute(file_paths.get(position).get(Config.TAG_DATA), "" + file.temp.getText().toString());
                                } else {
                                    try {
                                        // File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
                                        Uri path = Uri.fromFile(myFile);
                                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                        pdfIntent.setDataAndType(path, "application/pdf");
                                        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        context.startActivity(pdfIntent);
                                    } catch (ActivityNotFoundException e) {
                                        Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //file.status.setText(R.string.sent);
                try {
                    file.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    file.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
                    file.outgoing_layout_bubble.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                // File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
                                String abc = file_paths.get(position).get("local_path");
                                File file = new File(abc);
                                Log.e("onClick: check local path", abc);
                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                context.startActivity(pdfIntent);
                                //Toast.makeText(context, "Helloo", Toast.LENGTH_SHORT).show();
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (holder instanceof DataAdapter_User.ImagePick) {
            final String Image_name = file_paths.get(position).get(Config.TAG_DATA);
            DataAdapter_User.ImagePick image = (DataAdapter_User.ImagePick) holder;
            image.textview_time.setText(ad_time);
            image.textview_date.setText(ad_date);
            ImageFileName = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");
            extStore = Environment.getExternalStorageDirectory();
            myFile = new File(extStore.getAbsolutePath() + "/FileSharing/" + ImageFileName);

            Picasso.with(context)
                    .load(Image_name)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .resize(120, 120)
                    .into(image.score);

            PhoneFromDevice = SharedPreferenceManager.getDefaults("phone", context);
            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
                //gallery.status.setText(R.string.reveived);

                image.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

                if (!myFile.exists()) {

                    Picasso.with(context)
                            .load(file_paths.get(position).get(Config.TAG_DATA))
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .resize(50, 50)
                            .transform(new GrayscaleTransformation())
                            .into(image.score);
                }

            } else {
                //gallery.status.setText(R.string.sent);
                image.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
            }

            image.score.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileCacher<ArrayList<HashMap<String, String>>> stringCacher = new FileCacher<>(context, "cache_tmp.txt");
                    ImageFileName = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");
                    extStore = Environment.getExternalStorageDirectory();
                    myFile = new File(extStore.getAbsolutePath() + "/FileSharing/" + ImageFileName);


                    if (!myFile.exists()) {
                        // execute this when the downloader must be fired
                        downloadTask = new DownloadTaskIMG(context, Config.INTERNAL_IMAGE_PATH_URI, "" + ImageFileName, downloadCallBack);
                        // downloadTask.execute("http://ia.tranetech.ae:82/upload/uploads/five-point-someone-chetan-bhagat_ebook.pdf", "" + finalHolder.tv_paper_name.getText().toString() + ".pdf");
                        downloadTask.execute();
                    } else {
                        //	Toast.makeText(context, "File already Exists in " + myFile, Toast.LENGTH_SHORT).show();
                        if (myFile.exists()) {
                            Uri path = Uri.fromFile(myFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(path, "image/*");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            try {
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, "No Application in your device available to view", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
            });

        } else if (holder instanceof DataAdapter_User.ContactPick) {
            final String Contact_name = file_paths.get(position).get(Config.TAG_DATA);
            DataAdapter_User.ContactPick contactPick = (DataAdapter_User.ContactPick) holder;


            String[] splited = Contact_name.split(":");
            String phone_no = splited[0];
            String phone_name = splited[1];

            Log.e("onBindViewHolder: ", phone_no);
            Log.e("onBindViewHolder: ", phone_name);

            contactPick.no.setText(phone_no);
            contactPick.name.setText(phone_name);
            contactPick.textview_time.setText(ad_time);
            contactPick.textview_date.setText(ad_date);
            if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
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

    @Override
    public void onDownloadComplete() {
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

    }

    private class FilePick extends DataAdapter_User.ViewHolder {
        TextView temp;
        TextView status, textview_time, textview_date;
        LinearLayout outgoing_layout_bubble;
        LinearLayout linearLayout;
        ImageView file_type_image;

        FilePick(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.file_name);
            //this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
            this.file_type_image = (ImageView) v.findViewById(R.id.file_image);
        }
    }

    private class ImagePick extends DataAdapter_User.ViewHolder {
        ImageView score;
        TextView status, textview_time, textview_date;
        LinearLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        ImagePick(View v) {
            super(v);
            this.score = (ImageView) v.findViewById(R.id.image_list);
            //this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);

        }
    }

    private class ContactPick extends DataAdapter_User.ViewHolder {
        TextView status, textview_time, textview_date, no, name;
        LinearLayout outgoing_layout_bubble;
        LinearLayout linearLayout;

        ContactPick(View v) {
            super(v);
            this.no = (TextView) v.findViewById(R.id.contact_no);
            this.name = (TextView) v.findViewById(R.id.contact_name);
            this.status = (TextView) v.findViewById(R.id.send_receive);
            this.textview_time = (TextView) v.findViewById(R.id.textview_time);
            this.textview_date = (TextView) v.findViewById(R.id.textview_date);
            this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
        }

    }

}
