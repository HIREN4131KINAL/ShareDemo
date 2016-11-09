package com.example.guest999.firebasenotification.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.guest999.firebasenotification.adapters.ProfileAdapter;
import com.example.guest999.firebasenotification.utilis.FilePath;
import com.example.guest999.firebasenotification.utilis.JSONParser;
import com.example.guest999.firebasenotification.utilis.ServiceGetSet;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harshad on 21-10-2016 at 11:08 AM.
 */
public class User_Setting extends AppCompatActivity {
    public static final int RESULT_OK = -1;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_IMAGE_CAPTURE = 2;
    private final String setting[] = {
            "Change Password",
            "Log out",

    };
    private final int android_image_urls[] = {
            R.mipmap.changepass,
            R.mipmap.logout,

    };
    CircleImageView profile_image;
    ProfileAdapter adapter;
    TextView title_name, tv_no, tv_name;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String mCurrentPhotoPath, fileName;
    private String selectedFilePath = null;
    private JSONParser jsonParser = new JSONParser();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersetting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoaduIelements();
        LoadUIListner();

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/royal-serif.ttf");
        title_name.setTypeface(face);

        requestQueue = Volley.newRequestQueue(this);
        LoadUserData();
    }

    public void LoaduIelements() {
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        profile_image.setVisibility(View.GONE);

        title_name = (TextView) findViewById(R.id.app_name);
        tv_no = (TextView) findViewById(R.id.tv_pro_no);
        tv_name = (TextView) findViewById(R.id.tv_pro_name);

        progressBar = (ProgressBar) findViewById(R.id.progress_pro_pic);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_profile);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(User_Setting.this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<ServiceGetSet> androidVersions = prepareData();
        adapter = new ProfileAdapter(User_Setting.this, androidVersions);
        recyclerView.setAdapter(adapter);

    }

    public void LoadUIListner() {

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAddOnClick();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<ServiceGetSet> prepareData() {

        ArrayList<ServiceGetSet> android_version = new ArrayList<>();
        for (int i = 0; i < setting.length; i++) {
            ServiceGetSet androidVersion = new ServiceGetSet();
            androidVersion.setName(setting[i]);
            androidVersion.setThumbnail(android_image_urls[i]);
            android_version.add(androidVersion);
        }
        return android_version;
    }

    public void imgAddOnClick() {
        dialog = new Dialog(User_Setting.this);
        dialog.setContentView(R.layout.dialoge_pro_pic);
        dialog.setTitle("Select Photo");

        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeGallery();
            }
        });
        dialog.findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTakePhoto();
            }
        });

        // show dialog on screen
        dialog.show();
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
        dialog.dismiss();
    }

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
                dialog.dismiss();
            }
        }
    }

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

                        dialog = ProgressDialog.show(User_Setting.this, "", "Sending File ...", true);

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
                        resultCode == RESULT_OK) {
                    selectedFilePath = mCurrentPhotoPath;
                    Log.e("onActivityResult: ", selectedFilePath);

                    if (selectedFilePath != null && !selectedFilePath.equals("")) {
                        dialog = ProgressDialog.show(User_Setting.this, "", "Sending File ...", true);
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
    }

    private void LoadUserData() {

        /*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOAD_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        if (response != null) {
                            try {
                                Log.e("Full OnResponse jhgvgh" + "", response);

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("profilepic");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String path = object.getString(Config.KEY_PROFILE_PATH);
                                    String u_name = object.getString(Config.KEY_USERNAME);

                                    Log.e("onResponse: ", path + " " + u_name);

                                    tv_no.setText(SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
                                    tv_name.setText(u_name);

                                    Picasso.with(User_Setting.this)
                                            .load(path)
                                            .placeholder(R.drawable.placeholder)
                                            .into(profile_image, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    progressBar.setVisibility(View.GONE);
                                                    profile_image.setVisibility(View.VISIBLE);
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        Toast.makeText(User_Setting.this, "No Internet connection", Toast.LENGTH_LONG).show();

                        Log.e("onErrorResponse: ", error + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
                Log.e("getParams: ", params + "");
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);
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
                String SERVER_URL = "http://laxmisecurity.com/android/firebase/uploadprofile.php";
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

                Log.e("Server Response is: ", serverResponseMessage + ": " + serverResponseCode);
                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Image Upload Sucessfully ", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.VISIBLE);
                            profile_image.setVisibility(View.GONE);
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

    public class UploadImage extends AsyncTask<String, Void, String> {

        static final String UPLOAD_KEY = "path";

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
            LoadUserData();
        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> data = new HashMap<>();
            data.put(UPLOAD_KEY, fileName);
            data.put(Config.KEY_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
            // data.put(LOCAL_PATH, LocalfilePath);
            Log.e("HashMap data: ", fileName);
            Log.e("HashMap data: ", data + "");
            String result = jsonParser.sendPostRequest(Config.SEND_PROFILE, data);
            Log.e("result: ", result);
            return result;
        }
    }

}
