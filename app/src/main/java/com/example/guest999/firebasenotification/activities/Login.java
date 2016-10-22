package com.example.guest999.firebasenotification.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.example.guest999.firebasenotification.NotificationUtils;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "Login";
    public static SharedPreferences settings;
    public static boolean hasLoggedIn;
    String regId;
    TextView create_account;
    private EditText et_loginphone, et_login_password;
    private TextInputLayout til_login_phone, til_login_pass;
    private AppCompatButton login_button;
    private String login_password, login_phone, firebase_id;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(Login.PREFS_NAME, 0);
        hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if (hasLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), UserList.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(this);


        LoaduiElements();
        LoadUILisners();
    }

    private void LoaduiElements() {
         /*txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);*/

        //Initializing Views
        et_loginphone = (EditText) findViewById(R.id.login_editTextPhone);
        et_login_password = (EditText) findViewById(R.id.login_editTextPassword);

        til_login_phone = (TextInputLayout) findViewById(R.id.til_login_phone);
        til_login_pass = (TextInputLayout) findViewById(R.id.til_login_password);
        //til_confirmotp=(TextInputLayout)findViewById(R.id.til_confirm_otp);

        login_button = (AppCompatButton) findViewById(R.id.buttonLogin);

        create_account = (TextView) findViewById(R.id.linkSignup);
    }

    private void LoadUILisners() {

        create_account.setOnClickListener(this);
        et_loginphone.addTextChangedListener(new MyTextWatcher(et_loginphone));
        et_login_password.addTextChangedListener(new MyTextWatcher(et_login_password));

        //Initializing the RequestQueue

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
    }

    private void checkValidation() {
        if (!validatePassword()) {
            return;
        } else if (!validatePhoneNo()) {
            return;
        } else {
            submitForm();
            //  Toast.makeText(Login.this,"Success",Toast.LENGTH_LONG).show();
        }
    }

    private void submitForm() {
        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);
        //Getting user data
        login_phone = et_loginphone.getText().toString().trim();
        login_password = et_login_password.getText().toString().trim();
        firebase_id = regId;
        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            //Creating the json object from the response
                            Log.e("OnResponse", response);
                            //If it is success
                            if (response.equalsIgnoreCase(Config.TAG_Active)) {
                                //Asking user to confirm otp
                                //confirmOtp();
                                SharedPreferenceManager.setDefaults("phone", login_phone, getApplicationContext());
                                Log.e("onResponse Login User: ", login_phone);
                                Intent intent = new Intent(Login.this, UserList.class);
                                startActivity(intent);

                                //Log.e("onResponse Login User: ", login_phone);

                                SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0); // 0 - for private mode
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("hasLoggedIn", true);
                                editor.apply();
                            } else {
                                //If not successful user may already have registered
                                Toast.makeText(Login.this, "Username or Phone number invalid", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onErrorResponse: ", error + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_PASSWORD, login_password);
                params.put(Config.KEY_PHONE, login_phone);
                params.put(Config.KEY_FIREBASE_ID, firebase_id);
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == create_account) {
            Intent intent = new Intent(Login.this, RegisterActivity.class);
            startActivity(intent);
        }

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private boolean validatePhoneNo() {
        if (et_loginphone.getText().toString().trim().isEmpty() || et_loginphone.getText().toString().length() < 10) {
            til_login_phone.setError("Enter Valid Phone No");
            requestFocus(et_loginphone);
            return false;
        } else {
            til_login_phone.setErrorEnabled(false);
        }

        return true;

    }

    private boolean validatePassword() {
        if (et_login_password.getText().toString().trim().isEmpty() || et_login_password.getText().toString().length() < 8) {
            til_login_pass.setError("Enter password must be 8 digit");
            requestFocus(et_login_password);
            return false;
        } else {
            til_login_pass.setErrorEnabled(false);
        }
        return true;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

//        Log.e("Firebase reg id: ", regId);

       /* if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.login_editTextPhone:
                    validatePhoneNo();
                    break;
                case R.id.login_editTextPassword:
                    validatePassword();
                    break;
            }

        }
    }
}
