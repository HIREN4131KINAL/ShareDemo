package com.example.guest999.firebasenotification.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by Harshad
 */

public class Change_pass extends AppCompatActivity {

    String final_pass;
    private TextInputLayout tin_pass, tin_npass, tin_cpass;
    private EditText et_pass, et_npass, et_cpass;
    private Button btn_change_pass;
    private String cuurent_pass, new_pass, con_pass;
    private RequestQueue requestQueue;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoaduIelements();
        LoadUiListner();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void LoaduIelements() {
        et_pass = (EditText) findViewById(R.id.current_password);
        et_npass = (EditText) findViewById(R.id.new_password);
        et_cpass = (EditText) findViewById(R.id.confirm_password);

        tin_pass = (TextInputLayout) findViewById(R.id.tin_current_password);
        tin_npass = (TextInputLayout) findViewById(R.id.tin_new_password);
        tin_cpass = (TextInputLayout) findViewById(R.id.tin_con_password);

        btn_change_pass = (Button) findViewById(R.id.btn_change);
    }


    private void LoadUiListner() {
        et_pass.addTextChangedListener(new MyTextWatcher(et_pass));
        et_npass.addTextChangedListener(new MyTextWatcher(et_npass));
        et_cpass.addTextChangedListener(new MyTextWatcher(et_cpass));

        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private boolean validateCurrentPassword() {
        if (et_pass.getText().toString().trim().isEmpty() || et_pass.getText().toString().length() < 6) {
            tin_pass.setError(getString(R.string.err_msg_pass_digit));
            requestFocus(et_pass);
            return false;
        } else {
            tin_pass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNewPassword() {
        if (et_npass.getText().toString().trim().isEmpty() || et_npass.getText().toString().length() < 6) {
            tin_npass.setError(getString(R.string.err_msg_pass_digit));
            requestFocus(et_npass);
            return false;
        } else {
            tin_npass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (et_cpass.getText().toString().trim().isEmpty() || et_cpass.getText().toString().length() < 6) {
            tin_cpass.setError(getString(R.string.err_msg_pass_digit));
            requestFocus(et_cpass);
            return false;
        } else {
            tin_cpass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean comparePassword() {
        if (et_npass.getText().toString().equals(et_cpass.getText().toString())) {

            final_pass = et_cpass.getText().toString();

            LoadUserData();
            return false;
        } else {
            tin_cpass.setError("Please Enter Correct Password");
        }
        return true;
    }

    private void checkValidation() {
        if (!validateCurrentPassword()) {
            return;
        } else if (!validateNewPassword()) {
            return;
        } else if (!validateConfirmPassword()) {
            return;
        } else {
            comparePassword();

        }
    }

    private void LoadUserData() {

        /*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/
        dialog = ProgressDialog.show(Change_pass.this, "", "Processing ...", true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        if (response != null) {

                            Log.e("onResponse: ", response);

                            if (response.contains("Success")) {
                                Toast.makeText(Change_pass.this, "Password change sucessfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Change_pass.this, "Fail to change password", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Change_pass.this, "error", Toast.LENGTH_LONG).show();
                        Log.e("onErrorResponse: ", error + "");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
                params.put("old_pass", et_pass.getText().toString());
                params.put("new_pass", final_pass);
                Log.e("getParams: ", params + "");
                return params;
            }
        };
        //Adding request the the queue
        requestQueue.add(stringRequest);
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
                case R.id.current_password:
                    validateCurrentPassword();
                    break;
                case R.id.new_password:
                    validateNewPassword();
                    break;
                case R.id.confirm_password:
                    validateConfirmPassword();
                    break;
            }
        }
    }
}
