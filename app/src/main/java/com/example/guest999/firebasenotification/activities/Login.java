package com.example.guest999.firebasenotification.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
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
import com.example.guest999.firebasenotification.utilis.CheckConnection;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joshi Tushar and Modified Harshad
 */

public class Login extends AppCompatActivity implements View.OnClickListener {

	public static final String PREFS_NAME = "Login";
	public static boolean hasLoggedIn;
	public static SharedPreferences settings;
	//for inetenet checking
	public static WebView webview;
	CheckConnection cd;
	IntentFilter filter;
	String regId;
	TextView create_account, forgot_pass, title_name, title_name1, title_or;
	public static String Login_User;
	String TAG = getClass().getName();
	String u_type, u_status;
	public static String type;
	private EditText et_loginphone, et_login_password, editTextConfirmOtp, et_forgot_new_password, et_forgot_con_password;
	private TextInputLayout til_login_phone, til_login_pass, til_forgot_new_pass, til_forgot_con_pass;
	private AppCompatButton login_button;
	private String login_password, login_phone, firebase_id;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private RequestQueue requestQueue;
	//MarshmallowPermissions marsh;

	View parentLayout;
	private static final int REQUEST_PERMISSION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		settings = getSharedPreferences(Login.PREFS_NAME, 0);
		hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
		type = SharedPreferenceManager.getDefaults("type", getApplicationContext());
		Login_User = SharedPreferenceManager.getDefaults("phone", getApplicationContext());

		Log.e(TAG, "onCreate login phone: " + Login_User);
		Log.e(TAG, "onCreate: " + type);

		if (hasLoggedIn) {

			Log.e("onCreate: ", hasLoggedIn + "");

			if (type.contains("admin")) {
				Intent intent = new Intent(Login.this, UserList.class);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(Login.this, DataSharing_forUser.class);
				intent.putExtra("Click_Phone", Login_User);
				startActivity(intent);
				finish();
			}
		}


		requestQueue = Volley.newRequestQueue(this);

		webview = (WebView) findViewById(R.id.webview);
		parentLayout = findViewById(android.R.id.content);


		LoaduiElements();
		LoadUILisners();
	}

	private void LoaduiElements() {

		//Initializing Views
		et_loginphone = (EditText) findViewById(R.id.login_editTextPhone);
		et_login_password = (EditText) findViewById(R.id.login_editTextPassword);

		til_login_phone = (TextInputLayout) findViewById(R.id.til_login_phone);
		til_login_pass = (TextInputLayout) findViewById(R.id.til_login_password);

		login_button = (AppCompatButton) findViewById(R.id.buttonLogin);

		create_account = (TextView) findViewById(R.id.linkSignup);
		create_account.setPaintFlags(create_account.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		forgot_pass = (TextView) findViewById(R.id.forgot_pass);
		title_name = (TextView) findViewById(R.id.app_name);
		title_name1 = (TextView) findViewById(R.id.app_name1);
		title_or = (TextView) findViewById(R.id.tv_or);

		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/royal-serif.ttf");
		title_name.setTypeface(face);
		title_name1.setTypeface(face);
		Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/Demo_ConeriaScript.ttf");
		title_or.setTypeface(face1);
	}

	private void LoadUILisners() {

		create_account.setOnClickListener(this);
		forgot_pass.setOnClickListener(this);
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
		if (CheckConnection.ni == null) {
			registerReceiver(cd, filter);
		} else if (!validatePhoneNo()) {
			return;
		} else if (!validatePassword()) {
			return;
		} else {
			//for Internet
			if (CheckConnection.ni != null) {
				/*if (!marsh.checkIfAlreadyhavePermission()) {
					marsh.requestpermissions();
				} else {
					submitForm();
					//	marsh.requestpermissions();
				}*/

				submitForm();

			} else {
				registerReceiver(cd, filter);
			}
		}
	}

	private void submitForm() {
		//Displaying a progress dialog
		final ProgressDialog loading = ProgressDialog.show(this, "Processing", "Please wait...", false, false);
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
							//Creating the json object from Log.e("OnResponse", response);
							Log.e("full OnResponse", response);
							JSONObject jsonObject = new JSONObject(response);
							JSONArray jsonArray = jsonObject.getJSONArray("logindata");


							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject object = jsonArray.getJSONObject(i);
								u_type = object.getString("type");
								u_status = object.getString("status");
								if (u_type.contains("admin") && u_status.contains("active")) {

									SharedPreferenceManager.setDefaults("phone", login_phone, getApplicationContext());
									SharedPreferenceManager.setDefaults("type", u_type, getApplicationContext());
									SharedPreferenceManager.setDefaults("f_id", firebase_id, getApplicationContext());

									Intent usrlist = new Intent(getApplicationContext(), UserList.class);
									finish();
									startActivity(usrlist);
									Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();

									SharedPreferenceManager.setDefaults_boolean("notification", true, getApplicationContext());

									SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0); // 0 - for private mode
									SharedPreferences.Editor editor = settings.edit();
									editor.putString("phone", login_phone);
									editor.putString("f_id", firebase_id);
									editor.putBoolean("hasLoggedIn", true);
									editor.apply();
								} else if (u_type.contains("user") && u_status.contains("active")) {

									Intent dataToAdmin = new Intent(Login.this, DataSharing_forUser.class);
									dataToAdmin.putExtra("Click_Phone", login_phone);
									finish();
									startActivity(dataToAdmin);
									SharedPreferenceManager.setDefaults("phone", login_phone, getApplicationContext());
									SharedPreferenceManager.setDefaults("type", u_type, getApplicationContext());
									SharedPreferenceManager.setDefaults("f_id", firebase_id, getApplicationContext());

									SharedPreferenceManager.setDefaults_boolean("notification", true, Login.this);
									SharedPreferences settings = getSharedPreferences(Login.PREFS_NAME, 0); // 0 - for private mode
									SharedPreferences.Editor editor = settings.edit();
									editor.putString("phone", login_phone);
									editor.putString("f_id", firebase_id);
									editor.putBoolean("hasLoggedIn", true);
									editor.apply();
								} else if (u_type.contains("no") && u_status.contains("no")) {
									Toast.makeText(Login.this, "Invalid", Toast.LENGTH_SHORT).show();
								}
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
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		if (v == forgot_pass) {
			if (CheckConnection.ni != null) {
				ForgotPasswordClick();
			} else {
				registerReceiver(cd, filter);
			}
		}
	}

	public void ForgotPasswordClick() {
		if (validatePhoneNo()) {
			forgot_otp();
		} else {
			Toast.makeText(Login.this, "Enter Phone No", Toast.LENGTH_LONG).show();
		}

	}

	private void forgot_otp() {

		//Displaying a progress dialog
		final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait...", false, false);

		//Getting user data
		login_phone = et_loginphone.getText().toString().trim();
		Toast.makeText(Login.this, login_phone, Toast.LENGTH_LONG).show();

		//Again creating the string request
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FORGOT_URL,
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
								forgot_confirm_Otp();
							} else {
								//If not successful user may already have registered
								Toast.makeText(Login.this, "Enter Registered Phone no", Toast.LENGTH_LONG).show();
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
						Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
						Log.e("onErrorResponse: ", error + "");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				//Adding the parameters to the request
				params.put(Config.KEY_PHONE, login_phone);
				params.put("User_type", "Client");
				return params;
			}
		};

		//Adding request the the queue
		requestQueue.add(stringRequest);
	}

	//This method would confirm the otp
	private void forgot_confirm_Otp() throws JSONException {
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
					forgot_otp();
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
					final ProgressDialog loading = ProgressDialog.show(Login.this, "Authenticating", "Please wait while we check the entered code", false, false);

					//Getting the user entered otp from edittext
					final String otp = editTextConfirmOtp.getText().toString().trim();

					//Creating an string request
					StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONFIRM_FORGOT_URL,
							new Response.Listener<String>() {
								@RequiresApi(api = Build.VERSION_CODES.M)
								@Override
								public void onResponse(String response) {
									//if the server response is success
									try {
										//Creating the json object from the response
										JSONObject jsonResponse = new JSONObject(response);

										//If it is success
										if (jsonResponse.getString(Config.TAG_RESPONSE).equalsIgnoreCase("Success")) {
											//Asking user to confirm otp
											loading.dismiss();

											Toast.makeText(Login.this, "Successful Verified", Toast.LENGTH_LONG).show();
											EnterForgotPasswordDialoge();
										} else {
											//If not successful user may already have registered
											Toast.makeText(Login.this, "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();
											try {
												//Asking user to enter otp again
												forgot_confirm_Otp();
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							},
							new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									alertDialog.dismiss();
									Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
								}
							}) {
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> params = new HashMap<>();
							//Adding the parameters otp and username
							params.put(Config.KEY_OTP, otp);
							params.put(Config.KEY_PHONE, login_phone);
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

	public void EnterForgotPasswordDialoge() {
		//Creating a LayoutInflater object for the dialog box
		LayoutInflater li = LayoutInflater.from(this);
		//Creating a view to get the dialog box
		View confirmDialog = li.inflate(R.layout.dialoge_forgot_pass, null);

		//Initizliaing confirm button fo dialog box and edittext of dialog box
		AppCompatButton buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
		et_forgot_new_password = (EditText) confirmDialog.findViewById(R.id.forgot_new_editTextPassword);
		et_forgot_con_password = (EditText) confirmDialog.findViewById(R.id.forgot_con_editTextPassword);

		til_forgot_new_pass = (TextInputLayout) confirmDialog.findViewById(R.id.til_forgot_new_pass);
		til_forgot_con_pass = (TextInputLayout) confirmDialog.findViewById(R.id.til_forgot_con_pass);

		et_forgot_new_password.addTextChangedListener(new MyTextWatcher(et_forgot_new_password));
		et_forgot_con_password.addTextChangedListener(new MyTextWatcher(et_forgot_con_password));

		//Creating an alertdialog builder
		AlertDialog.Builder alert = new AlertDialog.Builder(this);


		//Adding our dialog box to the view of alert dialog
		alert.setView(confirmDialog);

		//Creating an alert dialog
		final AlertDialog alertDialog = alert.create();
		alertDialog.setCanceledOnTouchOutside(false);

		//Displaying the alert dialog
		alertDialog.show();
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//On the click of the confirm button from alert dialog
				if (CheckConnection.ni != null) {
					if (!validateForgotNewPassword()) {
						return;
					} else if (!validateForgotConfirmPassword()) {
						return;
					} else {
						//Hiding the alert dialog
						alertDialog.dismiss();

						//Displaying a progressbar
						final ProgressDialog loading = ProgressDialog.show(Login.this, "Authenticating", "Please wait...", false, false);

						if (et_forgot_new_password.getText().toString().equals(et_forgot_con_password.getText().toString())) {
							final String final_pass = et_forgot_con_password.getText().toString();
							//Creating an string request
							StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FORGOT_PASS_UPDATE,
									new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {
											//if the server response is success
											try {
												//Creating the json object from the response
												JSONObject jsonResponse = new JSONObject(response);

												//If it is success
												if (jsonResponse.getString(Config.TAG_RESPONSE).equalsIgnoreCase("Success")) {
													//Asking user to confirm otp
													loading.dismiss();

													Toast.makeText(Login.this, "Password Successfully Updated", Toast.LENGTH_LONG).show();


												} else {
													//If not successful user may already have registered
													Toast.makeText(Login.this, "Please Try Again", Toast.LENGTH_LONG).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									},
									new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											alertDialog.dismiss();
											Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
										}
									}) {
								@Override
								protected Map<String, String> getParams() throws AuthFailureError {
									Map<String, String> params = new HashMap<>();
									//Adding the parameters otp and username
									params.put(Config.KEY_FORGOT, final_pass);
									params.put(Config.KEY_PHONE, login_phone);
									return params;
								}
							};

							//Adding the request to the queue
							requestQueue.add(stringRequest);
						} else {
							loading.dismiss();
							Toast.makeText(Login.this, "Both password does not match", Toast.LENGTH_LONG).show();
							EnterForgotPasswordDialoge();
						}
					}
				} else {
					registerReceiver(cd, filter);
				}
			}
		});
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

	private boolean validateForgotNewPassword() {
		if (et_forgot_new_password.getText().toString().trim().isEmpty() || et_forgot_new_password.getText().toString().length() < 8) {
			til_forgot_new_pass.setError("Enter password must be 8 digit");
			requestFocus(et_forgot_new_password);
			return false;
		} else {
			til_forgot_new_pass.setErrorEnabled(false);
		}
		return true;
	}

	private boolean validateForgotConfirmPassword() {
		if (et_forgot_con_password.getText().toString().trim().isEmpty() || et_forgot_con_password.getText().toString().length() < 8) {
			til_forgot_con_pass.setError("Enter password must be 8 digit");
			requestFocus(et_forgot_con_password);
			return false;
		} else {
			til_forgot_con_pass.setErrorEnabled(false);
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
		//for Internet
		registerReceiver(cd, filter);
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		unregisterReceiver(cd);
		super.onPause();
	}

	public void checkLogin() {

		settings = getSharedPreferences(Login.PREFS_NAME, MODE_PRIVATE);
		final String phone = settings.getString("phone", null);
		final String f_id = settings.getString("f_id", null);
		//Again creating the string request
		RequestQueue requestQueue1 = Volley.newRequestQueue(this);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHECKING_URL,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							//Creating the json object from the response
							Log.e("OnResponse", response);
							//If it is success
							if (response.equalsIgnoreCase(Config.TAG_CHECK)) {

                                /*Toast.makeText(Login.this, "Successful Checking is Done", Toast.LENGTH_LONG).show();*/
								Log.e("onResponse: ", "success");

								if (type.contains("admin")) {
									Intent intent = new Intent(Login.this, UserList.class);
									startActivity(intent);
								} else {
									Intent intent = new Intent(Login.this, DataSharing_forUser.class);
									intent.putExtra("Click_Phone", Login_User);
									startActivity(intent);
									finish();
								}

							} else {

								//If not successful user may already have registered
								/*Toast.makeText(Login.this, "Successful Checking is Not Done", Toast.LENGTH_LONG).show();*/
								Log.e("onResponse: ", "Not success");

								android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Login.this);
								builder.setTitle("Warning For Login");
								builder.setMessage("Are you sure want to logout from another device?");
								builder.setIcon(R.drawable.warning);
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// positive button logic
												SharedPreferenceManager.setDefaults_boolean("notification", false, Login.this);
												settings.edit().clear().apply();
												SharedPreferenceManager.ClearAllPreferences(Login.this);
											}
										});
								android.support.v7.app.AlertDialog dialog = builder.create();
								// display dialog
								dialog.show();

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
						Log.e("onErrorResponse: ", error + "");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				//Adding the parameters to the request
				params.put(Config.KEY_PHONE, phone);
				params.put(Config.KEY_FIREBASE_ID, f_id);
				return params;
			}
		};

		//Adding request the the queue
		requestQueue1.add(stringRequest);
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
				case R.id.forgot_new_editTextPassword:
					validateForgotNewPassword();
					break;
				case R.id.forgot_con_editTextPassword:
					validateForgotConfirmPassword();
					break;
			}

		}
	}
}
