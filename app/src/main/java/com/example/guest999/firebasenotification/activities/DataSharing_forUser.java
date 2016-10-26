package com.example.guest999.firebasenotification.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.adapters.DataAdapter_User;
import com.example.guest999.firebasenotification.utilis.FilePath;
import com.example.guest999.firebasenotification.utilis.JSONParser;
import com.example.guest999.firebasenotification.utilis.MarshmallowPermissions;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.PhoneFromDevice;
import static com.example.guest999.firebasenotification.Config.PhoneFromURl;
/**
 * Created by Harshad and Modified Joshi Tushar
 */

public class DataSharing_forUser extends AppCompatActivity implements View.OnClickListener {

    public static final int RESULT_LOAD_FILE = 0;
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_LOAD_IMAGE_CAPTURE = 2;
    public static final int RESULT_OK = -1;
    private static final int REQUEST_CODE_PICK_CONTACTS = 99;

    public static ArrayList<HashMap<String, String>> hello;
    public static SharedPreferences settings;
    protected String user_Click_Phone, image_external_Url, file_extenal_Url, contact_external_url;
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
    String date, time, ampma, Login_User, type, fileName, mCurrentPhotoPath, phoneNo, name;
    private JSONParser jsonParser = new JSONParser();
    //ProgressDialog loading;
    private Dialog dialog;
    private String selectedFilePath = null;
    private RequestQueue requestQueue;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close;
    private Uri contactData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        type = SharedPreferenceManager.getDefaults("type", DataSharing_forUser.this);
        Login_User = SharedPreferenceManager.getDefaults("phone", getApplicationContext());

        if (type.contains("user")) {
            Intent i = getIntent();
            Bundle extra = i.getExtras();
            user_Click_Phone = extra.getString("Click_Phone");
            Log.e(TAG, "onCreatedsfu: " + user_Click_Phone);

           /* if (user_Click_Phone == null) {
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();*/
            //user_Click_Phone = extras.getString("Click_Phone");
            image_external_Url = i.getStringExtra("U_IMG_URL");
            file_extenal_Url = i.getStringExtra("U_FILE_URL");
            contact_external_url = i.getStringExtra("U_CONTACT_URL");

            Log.e(TAG, "onCreatedsfu: " + image_external_Url);
            Log.e(TAG, "onCreatedsfu: " + file_extenal_Url);
            Log.e(TAG, "onCreatedsfu: " + contact_external_url);
            //  }
            getSupportActionBar().setTitle(null);
            toolbar.setTitle("P. L. Shah & Co.");
            toolbar.setPadding(20, 0, 0, 0);
        } else {
            Toast.makeText(this, TAG + "Sorry Server Cant't Properly Work.", Toast.LENGTH_LONG).show();
        }


        requestQueue = Volley.newRequestQueue(this);
        settings = getSharedPreferences(Login.PREFS_NAME, 0);

        marsh = new MarshmallowPermissions(DataSharing_forUser.this);
        marsh.chkpermissions();
        hello = new ArrayList<>();
        dialog = new Dialog(DataSharing_forUser.this);
        Loaduiele();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        LoadUserData();

        if (image_external_Url != null) {
            handleImage();
        } else if (contact_external_url != null) {
            handleContact();
        } else if (file_extenal_Url != null) {
            handleFile();
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

    /**
     * For Get Contact Direct to Contact List
     */
    public void handleContact() {
        Toast.makeText(this, contact_external_url, Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(contact_external_url);
        Log.e("handleContact: ", uri + "");

       /* Cursor c = managedQuery(uri, null, null, null, null);
        if (c.moveToFirst()) {

            Log.e("handleContact: ", uri + "");
            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = c.getString(phoneIndex);
            Log.e("handleContact: ", name);
            Log.e("handleContact: ", phoneNo);
        }*/
    }

    /**
     * For Get Image Direct to Gallery
     */
    public void handleImage() {
        Uri uri = Uri.parse(image_external_Url);
        Log.e("handleImage: ", uri + "");
        selectedFilePath = FilePath.getPath(this, uri);
        Log.e("handleImage11: ", selectedFilePath + "");

        if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

            dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);

            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            String formattedCurrentDate = simpleDateFormat.format(currentDate);

            String[] splited = formattedCurrentDate.split("\\s+");
            date = splited[0];
            time = splited[1];
            ampma = splited[2];


            new Thread(new Runnable() {
                @Override
                public void run() {
                    //creating new thread to handle Http Operations
                    uploadFile(selectedFilePath);
                    new DataSharing_forUser.UploadImage().execute(selectedFilePath);

                }
            }).start();

        } else {
            Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * For Get File Direct to File Manager
     */
    public void handleFile() {
        Uri uri = Uri.parse(file_extenal_Url);
        Log.e("handleFile: ", uri + "");
        selectedFilePath = FilePath.getPath(this, uri);
        //setGet.path(String.valueOf(filePath));
        //FilePaths.add(setGet);
        if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

            dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);
            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            String formattedCurrentDate = simpleDateFormat.format(currentDate);

            String[] splited = formattedCurrentDate.split("\\s+");
            date = splited[0];
            time = splited[1];
            ampma = splited[2];


            new Thread(new Runnable() {
                @Override
                public void run() {
                    //creating new thread to handle Http Operations
                    uploadFile(selectedFilePath);
                    new DataSharing_forUser.UploadImage().execute(selectedFilePath);

                }
            }).start();

        } else {
            Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
        }


    }

    private void IntialAdapter() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(hello.size() - 1);
        recyclerView.setAdapter(new DataAdapter_User(getApplicationContext(), hello));
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                Snackbar.make(v, "Camera Clicked", Snackbar.LENGTH_SHORT).show();
                activeTakePhoto();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.gallery:
                Snackbar.make(v, "Gallery Clicked", Snackbar.LENGTH_SHORT).show();
                activeGallery();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.document:
                Snackbar.make(v, "Document Clicked", Snackbar.LENGTH_SHORT).show();
                FilePicker();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;

            case R.id.contacts:
                Snackbar.make(v, "Contacts Clicked", Snackbar.LENGTH_SHORT).show();
                activeContact();
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                break;
        }

    }

    /**
     * For sending file intent
     */
    public void FilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, RESULT_LOAD_FILE);
    }

    /**
     * for gallery intent
     */
    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /**
     * For Intent Camera
     */
    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * For Intent Contact
     */
    public void activeContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_datascreen, menu);
        return true;
    }

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
        if (id == R.id.action_logout) {

            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Would you like to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //SharedPreferenceManager.getDefaults_boolean("notification", false, DataSharing_forUser.this);
                            //settings.edit().clear().apply();

                            Intent logout = new Intent(DataSharing_forUser.this, Login.class);
                            // this flag prevent back to in to application after logout.
                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SharedPreferenceManager.ClearAllPreferences(getApplicationContext());
                            Login.settings.edit().clear().apply();
                            startActivity(logout);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // user doesn't want to logout
                        }
                    })
                    .show();

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

    /**
     * For Get Contact No and Name
     */
    private void ContactNo() {
        Cursor c = managedQuery(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = c.getString(phoneIndex);
            Log.e("ContactNo: ", name);
            Log.e("ContactNo: ", phoneNo);
            Log.e(TAG, "ContactNo: " + phoneNo + " " + name);
        }

    }

    /**
     * For Save Image
     */
    private File createImageFile() throws IOException {
        // Create an gallery file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    final Uri filePath = data.getData();
                    Log.e("IMAGE", filePath + "");
                    selectedFilePath = FilePath.getPath(this, filePath);

                    if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

                        dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);

                        //get date and time from device and chane it in fix format
                        Date currentDate = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
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
                        dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);
                        //for getting current date and time
                        Date currentDate = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
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
            case REQUEST_CODE_PICK_CONTACTS:
                if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK && null != data) {
                    contactData = data.getData();
                    Log.e("onActivityResult: ", contactData + "");
                    ContactNo();
                    //Call();

                    if (contactData != null) {

                        dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);
                        Date currentDate = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        String formattedCurrentDate = simpleDateFormat.format(currentDate);

                        String[] splited = formattedCurrentDate.split("\\s+");
                        date = splited[0];
                        time = splited[1];
                        ampma = splited[2];

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                new UploadContact().execute();
                                dialog.dismiss();

                            }
                        }).start();

                    }
                }
            case RESULT_LOAD_IMAGE_CAPTURE:
                if (requestCode == RESULT_LOAD_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    selectedFilePath = mCurrentPhotoPath;
                    Log.e(TAG, "onActivityResult: " + selectedFilePath);
                    if (selectedFilePath != null && !selectedFilePath.equals("")) {
                        dialog = ProgressDialog.show(DataSharing_forUser.this, "", "Sending File ...", true);
                        Date currentDate = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                        String formattedCurrentDate = simpleDateFormat.format(currentDate);

                        String[] splited = formattedCurrentDate.split("\\s+");
                        date = splited[0];
                        time = splited[1];
                        ampma = splited[2];
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
        if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
            IntialAdapter();
        }
    }

    //android upload file or image to servee folder
    //this class is used to move image or file from mobile device to server folder.
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

    //loading user screen data.
    private void LoadUserData() {

        /*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.USER_SCREEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        hello = new ArrayList<>();
                        if (response != null) {
                            try {
                                Log.e("Full OnResponse Data for user", response);
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
                        Toast.makeText(DataSharing_forUser.this, "error", Toast.LENGTH_LONG).show();
                        Log.e("onErrorResponse: ", error + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
                Log.e(TAG, "getParams: " + params);
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);
    }

    //for insert image name into db
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            marsh = new MarshmallowPermissions(DataSharing_forUser.this);

            if (marsh.chkpermissions())
                LoadUserData();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();
            data.put(UPLOAD_KEY, fileName);
            data.put(Config.KEY_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
            data.put(Config.CURRENT_DATE, date);
            data.put(Config.CURRENT_TIME, time + " " + ampma);
            Log.e("HashMap data: ", data + "");
            String result = jsonParser.sendPostRequest(Config.FILESEND_USER, data);
            Log.e("result: ", result);
            return result;
        }
    }

    //for insert contact into db
    public class UploadContact extends AsyncTask<String, Void, String> {

        static final String UPLOAD_KEY = "image";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            marsh = new MarshmallowPermissions(DataSharing_forUser.this);

            if (marsh.chkpermissions())
                LoadUserData();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();
            data.put(Config.KEY_PHONE, user_Click_Phone);
            data.put(UPLOAD_KEY, phoneNo.replaceAll(" ", "") + " " + name);
            data.put(Config.KEY_A_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
            data.put(Config.CURRENT_DATE, date);
            data.put(Config.CURRENT_TIME, time + " " + ampma);
            Log.e("HashMap data: ", data + "");
            String result = jsonParser.sendPostRequest(Config.COTACTSEND_USER, data);
            Log.e("result: ", result);
            return result;
        }
    }
}
