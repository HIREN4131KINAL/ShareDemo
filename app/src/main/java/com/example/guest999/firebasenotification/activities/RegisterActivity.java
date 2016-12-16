package com.example.guest999.firebasenotification.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.CheckConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joshi Tushar on 10/7/2016.
 */

public class RegisterActivity extends AppCompatActivity {
	//for Internet
	CheckConnection cd;
	IntentFilter filter;
	/**
	 * Notification
	 */
	private TextView txtRegId, txtMessage, linkLogin, title_name, title_name1, title_or;
	/**
	 * OTP
	 */
	private EditText editTextUsername, editTextConfirmOtp, editTextPassword, editTextPhone;
	private TextInputLayout til_uname, til_u_pass, til_phone;
	private AppCompatButton buttonRegister;
	//Volley RequestQueue
	private RequestQueue requestQueue;
	//String variables to hold username password and phone
	private String username;
	private String password;
	private String phone;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		//Initializing the RequestQueue
		requestQueue = Volley.newRequestQueue(this);
		LoaduiElements();
		LoadUILisners();
	}

	private void LoaduiElements() {
		 /*txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);*/

		//Initializing Views
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);

		til_uname = (TextInputLayout) findViewById(R.id.til_reg_uname);
		til_u_pass = (TextInputLayout) findViewById(R.id.til_reg_password);
		til_phone = (TextInputLayout) findViewById(R.id.til_reg_phone);
		//til_confirmotp=(TextInputLayout)findViewById(R.id.til_confirm_otp);

		buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

		linkLogin = (TextView) findViewById(R.id.linkLogin);
		linkLogin.setPaintFlags(linkLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		title_name = (TextView) findViewById(R.id.app_name);
		title_name1 = (TextView) findViewById(R.id.app_name1);
		title_or = (TextView) findViewById(R.id.tv_or);


		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/royal-serif.ttf");
		title_name.setTypeface(face);
		title_name1.setTypeface(face);
		Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/Demo_ConeriaScript.ttf");
		title_or.setTypeface(face1);
		//for Internet
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	}

	private void LoadUILisners() {

		editTextUsername.addTextChangedListener(new MyTextWatcher(editTextUsername));
		editTextPassword.addTextChangedListener(new MyTextWatcher(editTextPassword));
		editTextPhone.addTextChangedListener(new MyTextWatcher(editTextPhone));

		buttonRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkValidation();
			}
		});

		linkLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, Login.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		//for Internet
		registerReceiver(cd, filter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(cd);
		super.onPause();
	}

	private void checkValidation() {
		if (CheckConnection.ni == null) {
			registerReceiver(cd, filter);
		} else if (!validateUserName()) {
			return;
		} else if (!validatePassword()) {
			return;
		} else if (!validatePhoneNo()) {
			return;
		} else {

			if (CheckConnection.ni != null) {
				register();
			} else {
				registerReceiver(cd, filter);
			}

		}
	}

	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}

	private boolean validateUserName() {
		if (editTextUsername.getText().toString().trim().isEmpty()) {
			til_uname.setError("Please Enter UserName");
			requestFocus(editTextUsername);
			return false;
		} else {
			til_uname.setErrorEnabled(false);
		}
		return true;

	}

	private boolean validatePhoneNo() {
		if (editTextPhone.getText().toString().trim().isEmpty() || editTextPhone.getText().toString().length() < 10) {
			til_phone.setError("Enter Valid Phone No");
			requestFocus(editTextPhone);
			return false;
		} else {
			til_phone.setErrorEnabled(false);
		}

		return true;

	}

	private boolean validatePassword() {
		if (editTextPassword.getText().toString().trim().isEmpty() || editTextPassword.getText().toString().length() < 8) {
			til_u_pass.setError("Enter password must be 8 digit");
			requestFocus(editTextPassword);
			return false;
		} else {
			til_u_pass.setErrorEnabled(false);
		}
		return true;
	}

	//this method will register the user
	private void register() {

		//Displaying a progress dialog
		final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);


		//Getting user data
		username = editTextUsername.getText().toString().trim();
		password = editTextPassword.getText().toString().trim();
		phone = editTextPhone.getText().toString().trim();

		//Again creating the string request
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						loading.dismiss();
						try {
							//Creating the json object from the response
							JSONObject jsonResponse = new JSONObject(response);

							//If it is success
							if (jsonResponse.getString(Config.TAG_RESPONSE).equalsIgnoreCase("Success")) {
								//Asking user to confirm otp

								confirmOtp();
							} else {
								//If not successful user may already have registered
								Toast.makeText(RegisterActivity.this, "Username or Phone number already registered", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						loading.dismiss();
						Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
						Log.e("onErrorResponse: ", error + "");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				//Adding the parameters to the request
				params.put(Config.KEY_USERNAME, username);
				params.put(Config.KEY_PASSWORD, password);
				params.put(Config.KEY_PHONE, phone);
				params.put("User_type", "Client");
				return params;
			}
		};

		//Adding request the the queue
		requestQueue.add(stringRequest);
	}

	//This method would confirm the otp
	private void confirmOtp() throws JSONException {
		//Creating a LayoutInflater object for the dialog box
		LayoutInflater li = LayoutInflater.from(this);
		//Creating a view to get the dialog box
		View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

		//Initizliaing confirm button fo dialog box and edittext of dialog box
		AppCompatButton buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
		editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);
		TextView textView_resend_otp = (TextView) confirmDialog.findViewById(R.id.tv_resend_otp);

		//Creating an alertdialog builder
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		//Adding our dialog box to the view of alert dialog
		alert.setView(confirmDialog);

		//Creating an alert dialog
		final AlertDialog alertDialog = alert.create();
		alertDialog.setCanceledOnTouchOutside(false);

		//Displaying the alert dialog
		alertDialog.show();

		textView_resend_otp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CheckConnection.ni != null) {
					alertDialog.dismiss();
					register();
				} else {
					registerReceiver(cd, filter);
				}
			}
		});

		//On the click of the confirm button from alert dialog
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CheckConnection.ni != null) {
					//Hiding the alert dialog
					alertDialog.dismiss();

					//Displaying a progressbar
					final ProgressDialog loading = ProgressDialog.show(RegisterActivity.this, "Authenticating", "Please wait while we check the entered code", false, false);

					//Getting the user entered otp from edittext
					final String otp = editTextConfirmOtp.getText().toString().trim();

					//Creating an string request
					StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONFIRM_URL,
							new Response.Listener<String>() {
								@Override
								public void onResponse(String response) {
									//if the server response is success
									if (response.equalsIgnoreCase("success")) {
										//dismissing the progressbar
										loading.dismiss();

										Toast.makeText(RegisterActivity.this, "Successful Verified", Toast.LENGTH_LONG).show();
										//Starting a new activity
										startActivity(new Intent(RegisterActivity.this, Login.class));
									} else {
										//Displaying a toast if the otp entered is wrong
										Toast.makeText(RegisterActivity.this, "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();
										try {
											//Asking user to enter otp again
											confirmOtp();
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
							},
							new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									alertDialog.dismiss();
									Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
								}
							}) {
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> params = new HashMap<>();
							//Adding the parameters otp and username
							params.put(Config.KEY_OTP, otp);
							params.put(Config.KEY_PHONE, phone);
							return params;
						}
					};

					//Adding the request to the queue
					requestQueue.add(stringRequest);
				} else {
					registerReceiver(cd, filter);
				}
			}
		});
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
				case R.id.editTextUsername:
					validateUserName();
					break;
				case R.id.editTextPassword:
					validatePassword();
					break;
				case R.id.editTextPhone:
					validatePhoneNo();
					break;
			}

		}
	}

}
