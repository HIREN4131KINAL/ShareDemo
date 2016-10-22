package com.example.guest999.firebasenotification.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.ProgressDialog.CustomDialog;
import com.example.guest999.firebasenotification.ProgressDialog.DotLoader;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.adapters.DataAdapter;
import com.example.guest999.firebasenotification.utilis.FilePath;
import com.example.guest999.firebasenotification.utilis.JSONParser;
import com.example.guest999.firebasenotification.utilis.MarshmallowPermissions;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.PhoneFromDevice;
import static com.example.guest999.firebasenotification.Config.PhoneFromURl;


public class Data_Sharing extends AppCompatActivity implements View.OnClickListener {
    DataAdapter dataAdapter;
    View vg;
    private static final int REQUEST_PERMISSION = 1;
    public static final int RESULT_LOAD_FILE = 0;
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_LOAD_IMAGE_CAPTURE = 2;
    public static final int RESULT_OK = -1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 99;
    public static ArrayList<HashMap<String, String>> hello;
    FileCacher<ArrayList<HashMap<String, String>>> stringCacher = new FileCacher<>(Data_Sharing.this, "cache_tmp.txt");

    RecyclerView recyclerView;
    Toolbar toolbar;
    MarshmallowPermissions marsh;
    boolean hidden = true;
    LinearLayout mRevealView;
    ImageButton ib_camera, ib_gallery, ib_contacts, ib_document;
    int cx, cy;
    int startradius, endradius, reverse_startradius, reverse_endradius;
    Animator animator, animate;
    String TAG = getClass().getName();
    String date, time, ampma;
    private JSONParser jsonParser = new JSONParser();
    //ProgressDialog loading;
    private Dialog dialog;
    private String selectedFilePath = null;
    private String fileName;
    private String user_Click_Phone, user_click_Name;
    private RequestQueue requestQueue;

    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        marsh = new MarshmallowPermissions(Data_Sharing.this);
        dialog = new Dialog(Data_Sharing.this);
        VollyRequest();
        Loaduiele();
        LoadUserData();
        if (stringCacher.hasCache()) {
            try {
                hello = stringCacher.readCache();
                IntialAdapter();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!hidden) {
                    mRevealView.setVisibility(View.INVISIBLE);
                    hidden = true;
                }
                return false;
            }
        });
    }

    private void VollyRequest() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        user_Click_Phone = extras.getString("Click_Phone");
        user_click_Name = extras.getString(Config.KEY_USERNAME);
        Log.e(TAG, "onCreate: " + user_Click_Phone);
        Log.e(TAG, "onCreate: " + user_click_Name);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(user_click_Name);

        requestQueue = Volley.newRequestQueue(this);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

    }

    public void show_dialog() {
        FragmentManager fm = getFragmentManager();
        CustomDialog newFragment = new CustomDialog();
        newFragment.show(fm, "abc");
    }

    private void IntialAdapter() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        Log.e(TAG, "IntialAdapter:called" + hello);
        dataAdapter = new DataAdapter(getApplicationContext(), hello);
        recyclerView.scrollToPosition(hello.size() - 1);
        dataAdapter.notifyItemInserted(hello.size() - 1);
        recyclerView.setAdapter(dataAdapter);
    }

    public void Custom_Dialog() {
        DotLoader textDotLoader;
        dialog = new Dialog(Data_Sharing.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.getWindow().setBackgroundDrawable(null);
        textDotLoader = (DotLoader) findViewById(R.id.text_dot_loader);
        dialog.setCancelable(false);
        dialog.show();

    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            stringCacher.writeCache(hello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Loaduiele() {
        FrameLayout item = (FrameLayout) findViewById(R.id.frame_layout);
        View child = getLayoutInflater().inflate(R.layout.view_menu, null);
        item.addView(child);

        mRevealView = (LinearLayout) child.findViewById(R.id.reveal_items);
        ib_camera = (ImageButton) child.findViewById(R.id.camera);
        ib_contacts = (ImageButton) child.findViewById(R.id.contacts);
        ib_gallery = (ImageButton) child.findViewById(R.id.gallery);
        ib_document = (ImageButton) child.findViewById(R.id.document);

        ib_camera.setOnClickListener(this);
        ib_contacts.setOnClickListener(this);
        ib_gallery.setOnClickListener(this);
        ib_document.setOnClickListener(this);

        mRevealView.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);


    }

    @Override
    public void onClick(View v) {
        vg = v;
        switch (v.getId()) {
            case R.id.camera:

                if (marsh.checkIfAlreadyhavePermission()) {
                    activeTakePhoto();
                } else {
                    marsh.requestpermissions();
                }
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.gallery:
                if (marsh.checkIfAlreadyhavePermission()) {
                    activeGallery();
                } else {
                    marsh.requestpermissions();
                }
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.document:

                if (marsh.checkIfAlreadyhavePermission()) {
                    FilePicker();
                } else {
                    marsh.requestpermissions();
                }

                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.contacts:

                if (marsh.checkIfAlreadyhavePermission()) {
                    //activeContact();
                } else {
                    marsh.requestpermissions();
                }
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
        }

    }

    public void FilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, RESULT_LOAD_FILE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // attachment icon click event
        if (id == R.id.action_attachment) {

            int MyapiVersion = Build.VERSION.SDK_INT;
            if (MyapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                // finding X and Y co-ordinates
                cx = (mRevealView.getLeft() + mRevealView.getRight());
                cy = (mRevealView.getTop());

                // to find  radius when icon is tapped for showing layout
                startradius = 0;
                endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                // performing circular reveal when icon will be tapped
                animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(400);

                //reverse animation
                // to find radius when icon is tapped again for hiding layout

                //  starting radius will be the radius or the extent to which circular reveal animation is to be shown
                reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
                //endradius will be zero
                reverse_endradius = 0;

                // performing circular reveal for reverse animation
                animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);

                if (hidden) {

                    // to show the layout when icon is tapped
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {

                    mRevealView.setVisibility(View.VISIBLE);

                    // to hide layout on animation end
                    animate.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    animate.start();
                }
            } else {
                animateFAB();
            }
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void animateFAB() {
        if (isFabOpen) {
            mRevealView.startAnimation(fab_close);
            isFabOpen = false;
        } else {
            mRevealView.startAnimation(fab_open);
            isFabOpen = true;
        }

    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (!hidden) {
                reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
                //endradius will be zero
                reverse_endradius = 0;

                cx = (mRevealView.getLeft() + mRevealView.getRight());
                cy = (mRevealView.getTop());

                // performing circular reveal for reverse animation
                animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);

                //mRevealView.setVisibility(View.VISIBLE);

                // to hide layout on animation end
                animate.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRevealView.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                animate.start();
            }
        }

        return super.onTouchEvent(event);
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void activeTakePhoto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_CAPTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    final Uri filePath = data.getData();
                    Log.e("IMAGE", filePath + "");
                    selectedFilePath = FilePath.getPath(this, filePath);
                    //setGet.path(String.valueOf(filePath));
                    //FilePaths.add(setGet);
                    if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

                        dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending File ...", true);

                        //get date and time from device and chane it in fix format
                        Date currentDate = Calendar.getInstance().getTime();
                        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        String formattedCurrentDate = simpleDateFormat.format(currentDate);

                        String[] splited = formattedCurrentDate.split("\\s+");
                        date = splited[0];
                        time = splited[1];
                        ampma = splited[2];

                        Log.e(TAG, "onCreate: " + date);
                        Log.e(TAG, "onCreate: " + time + " " + ampma);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                                new UploadImage().execute(selectedFilePath);

                            }
                        }).start();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
                    }
                }

            case RESULT_LOAD_FILE:
                if (requestCode == RESULT_LOAD_FILE &&
                        resultCode == RESULT_OK && null != data) {
                    String FilePath = data.getData().getPath();
                    Log.e("FILEPATH", FilePath);
                    selectedFilePath = FilePath.substring(FilePath.lastIndexOf("/") + 1);
                    Log.e("onActivityResult: ", selectedFilePath);

                    /*show_dialog();*/
                    if (selectedFilePath != null && !selectedFilePath.equals("")) {
                        dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending File ...", true);
                        //for getting current date and time
                        Date currentDate = Calendar.getInstance().getTime();
                        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        String formattedCurrentDate = simpleDateFormat.format(currentDate);

                        String[] splited = formattedCurrentDate.split("\\s+");
                        date = splited[0];
                        time = splited[1];
                        ampma = splited[2];

                        Log.e(TAG, "onCreate: " + date);
                        Log.e(TAG, "onCreate: " + time + " " + ampma);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                                new UploadImage().execute(selectedFilePath);
                            }
                        }).start();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
                    }
                }
            case RESULT_LOAD_IMAGE_CAPTURE:
                if (requestCode == RESULT_LOAD_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK && null != data) {
                    selectedFilePath = data.getData().getPath();
                    Bundle extra = data.getExtras();

                    if (selectedFilePath != null && !selectedFilePath.equals("")) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                                new UploadImage().execute(selectedFilePath);
                            }
                        }).start();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
                    }
                }
        }
     /*   if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
            IntialAdapter();
        }*/
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                String SERVER_URL = "http://www.laxmisecurity.com/android/UploadToServer.php";
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCRYPT", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.e(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Image Sent Sucessfully ", Toast.LENGTH_SHORT).show();

                            //  tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://www.laxmisecurity.com/android/uploads/" + fileName);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }

    private void LoadUserData() {

        /*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOAD_USERDATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        hello = new ArrayList<>();
                        if (response != null) {
                            try {
                                Log.e("Full OnResponse", response);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("userlist");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    PhoneFromURl = object.getString(Config.KEY_PHONE);
                                    PhoneFromDevice = SharedPreferenceManager.getDefaults("phone", getApplicationContext());
                                    Log.e(TAG, "onResponse PhoneFromURl: " + PhoneFromURl);
                                    Log.e(TAG, "onResponse PhoneFromDevice: " + PhoneFromDevice);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(Config.TAG_DATA, object.getString(Config.TAG_DATA));
                                    map.put(Config.KEY_PHONE, object.getString(Config.KEY_PHONE));
                                    map.put(Config.CURRENT_DATE, object.getString(Config.CURRENT_DATE));
                                    map.put(Config.CURRENT_TIME, object.getString(Config.CURRENT_TIME));

                                    Log.e(TAG, "onResponse: map result " + map);

                                    hello.add(map);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                        IntialAdapter();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        Toast.makeText(Data_Sharing.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onErrorResponse: ", error + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_PHONE, user_Click_Phone);
                params.put(Config.KEY_A_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
                Log.e(TAG, "getParams: " + params);
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);
    }

    public class UploadImage extends AsyncTask<String, Void, String> {

        static final String UPLOAD_KEY = "image";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

         /*   loading = new ProgressDialog(getApplicationContext());
            loading.setMessage("Uploading...");
            loading.show();*/
        }


        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();
            data.put(Config.KEY_PHONE, user_Click_Phone);
            data.put(UPLOAD_KEY, "http://www.laxmisecurity.com/android/uploads/" + fileName);
            data.put(Config.KEY_A_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
            data.put(Config.CURRENT_DATE, date);
            data.put(Config.CURRENT_TIME, time + " " + ampma);
            Log.e("HashMap data: ", data + "");
            String result = jsonParser.sendPostRequest(Config.SEND_USERDATA, data);
            Log.e("result: ", result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            LoadUserData();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // for each permission check if the user grantet/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                final String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting

                        marsh.AllowedManually(vg);


                    } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                        // showRationale(permission, R.string.permission_denied);
                        // user denied WITHOUT never ask again
                        // this is a good place to explain the user
                        // why you need the permission and ask if he want
                        // to accept it (the rationale)
                        marsh.AllowedManually(vg);

                    }
                }
            }
        }
    }

}
