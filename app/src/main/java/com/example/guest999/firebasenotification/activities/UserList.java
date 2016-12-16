package com.example.guest999.firebasenotification.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.guest999.firebasenotification.adapters.DataAdapterUserList;
import com.example.guest999.firebasenotification.utilis.CheckConnection;
import com.example.guest999.firebasenotification.utilis.Logout_PLS;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.example.guest999.firebasenotification.utilis.SqlHandler;
import com.kosalgeek.android.caching.FileCacher;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.KEY_PHONE;

/**
 * Created by Harshad and Modified Joshi Tushar and hiren
 */


public class UserList extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {
	//swipe refresh is used for to load new upcoming data Hiren.
	public SwipyRefreshLayout swipeRefreshLayout;
	DataAdapterUserList dataAdapterUserList;
	public static ArrayList<HashMap<String, String>> Array_user_list;
	RecyclerView lv;
	String Login_User, admin_type;
	String TAG = getClass().getName();
	String sql_username;
	SqlHandler sqlHandler;
	ContentValues cv = null;
	//for Internet
	CheckConnection cd;
	IntentFilter filter;
	private RequestQueue requestQueue;
	public Uri imageUri, FilePath;
	private boolean afterRefresh = false;
	boolean doubleBackToExitPressedOnce = false;

	// FileCacher is used for offline purpose Hiren.
	FileCacher<ArrayList<HashMap<String, String>>> UserCacher = new FileCacher<>(UserList.this, "userlist_cache_tmp.txt");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);

		//for Internet
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(null);
		toolbar.setTitle("P. L. Shah & Co.");


		loadUIelements();
		LOadLisners();


	}

	private void LOadLisners() {
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);
										Log.d("Runnable method ", "");
										CheckUserType();

									}
								}
		);
		swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));


	}

	private void loadUIelements() {
		lv = (RecyclerView) findViewById(R.id.recyclerview_userlist);
		swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		sqlHandler = new SqlHandler(UserList.this);

		Login_User = SharedPreferenceManager.getDefaults("phone", getApplicationContext());
		admin_type = SharedPreferenceManager.getDefaults("type", getApplicationContext());
		Log.e(TAG, "onCreate login phone: " + Login_User);
		Log.e(TAG, "onCreate login phone: " + admin_type);
		requestQueue = Volley.newRequestQueue(this);
		OutSideIntentCondition();

		Array_user_list = new ArrayList<>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);
										Log.d("Runnable method ", "");
										CheckUserType();

									}
								}
		);
		registerReceiver(cd, filter);
	}

	@Override
	protected void onPause() {

		super.onPause();
		unregisterReceiver(cd);
		try {
			UserCacher.writeCache(Array_user_list);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void OutSideIntentCondition() {
		Login_User = SharedPreferenceManager.getDefaults("phone", getApplicationContext());
		admin_type = SharedPreferenceManager.getDefaults("type", getApplicationContext());
		Log.e(TAG, "onCreate login phone: " + Login_User);
		Log.e(TAG, "onCreate login phone: " + admin_type);

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		Log.e(TAG, "onCreate external: " + admin_type);
		if (Intent.ACTION_SEND.equals(action) && type != null && admin_type.contains("admin")) {
			if (type.startsWith("image/")) {
				imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				Log.e("handleSendImage admin: ", imageUri + "");
			} else {
				FilePath = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				Log.e("FILEPATH admin", FilePath + "");
			}
		} else if (admin_type.contains("user")) {
			if (type.startsWith("image/")) {
				imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				Log.e("handleSendImage user: ", imageUri + "");
			} else {
				FilePath = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				Log.e("FILEPATH user", FilePath + "");
			}
		} else {
		}

		if (admin_type.contains("admin")) {
			//CheckUserType();
		} else if (admin_type.contains("user")) {
			Intent intent1 = new Intent(this, DataSharing_forUser.class);
			intent1.putExtra("Click_Phone user", Login_User);
			if (imageUri != null) {
				intent1.putExtra("U_IMG_URL", imageUri + "");
				Log.e("onClick user: ", imageUri + "");
			} else {
				intent1.putExtra("U_FILE_URL", FilePath + "");
				Log.e("onClick user: ", FilePath + "");
			}
			startActivity(intent1);
			finish();
			imageUri = null;
			FilePath = null;
		} else {
			Toast.makeText(this, TAG + "Sorry Server Cant't Properly Work.", Toast.LENGTH_LONG).show();
		}
	}

	private void IntialAdapter() {


		lv.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		lv.setLayoutManager(mLayoutManager);
		Log.e(TAG, "IntialAdapter:called" + Array_user_list);
		dataAdapterUserList = new DataAdapterUserList(UserList.this, Array_user_list, sqlHandler, imageUri, FilePath);
		if (afterRefresh) {
			dataAdapterUserList.notifyItemInserted(Array_user_list.size() - 1);
			lv.scrollToPosition(Array_user_list.size() - 1);
		}
		lv.setAdapter(dataAdapterUserList);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_in_userlistscreen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_search:
				Intent intent = new Intent(UserList.this, Search.class);
				if (imageUri != null) {
					intent.putExtra("IMG_URL_SEARCH", imageUri + "");
					Log.e("onOptionsItem admin: ", imageUri + "");
				} else if (FilePath != null) {
					intent.putExtra("FILE_URL_SEARCH", FilePath + "");
					Log.e("onOptionsItem admin: ", FilePath + "");
				}
				startActivity(intent);
				return true;

			case R.id.change_pass:

				Intent setting = new Intent(UserList.this, Change_pass.class);
				startActivity(setting);

				return true;


			case R.id.action_logout:
				Logout_PLS logout_pls = new Logout_PLS(this, requestQueue
				);

				logout_pls.get_ready_logout();
			/*	new AlertDialog.Builder(this)
						.setTitle("Logout")
						.setMessage("Would you like to logout?")
						.setIcon(R.drawable.logo)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

								Intent logout = new Intent(UserList.this, Login.class);
								// this flag prevent back to in to application after logout.
								logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
										Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
								SharedPreferenceManager.ClearAllPreferences(getApplicationContext());
								Login.settings.edit().clear().apply();
								startActivity(logout);
								finish();

								Toast.makeText(UserList.this, "Logout sucessfully", Toast.LENGTH_SHORT).show();

							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// user doesn't want to logout
							}
						})
						.show();*/

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * This method is called when swipe refresh is pulled down
	 */

	private void CheckUserType() {

		final StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHECK_USERTYPE,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (swipeRefreshLayout.isRefreshing())
							swipeRefreshLayout.setRefreshing(false);
						Array_user_list = new ArrayList<>();
						if (!response.isEmpty()) {

							try {

								Log.e("full OnResponse", response);
								JSONObject jsonObject = new JSONObject(response);
								JSONArray jsonArray = jsonObject.getJSONArray("userlist");

								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = jsonArray.getJSONObject(i);
									sql_username = object.getString(Config.KEY_USERNAME);
									HashMap<String, String> map = new HashMap<>();
									map.put("username", object.getString(Config.KEY_USERNAME));
									map.put("phone", object.getString(Config.KEY_PHONE));
									map.put("profile_path", object.getString(Config.KEY_PROFILE_PATH));
									map.put("count", object.getString("count"));
									map.put("total", object.getString("total"));


									Log.e(TAG, "onResponse: map result " + map);

									Array_user_list.add(map);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (getApplicationContext() != null) {

								IntialAdapter();

							}
						} else {
							Log.e("ServiceHandler", "Couldn't get any data from the url");
						}

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							if (UserCacher.hasCache()) {
								Array_user_list = UserCacher.readCache();
								IntialAdapter();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						swipeRefreshLayout.setRefreshing(false);
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<>();
				//Adding the parameters to the request
				params.put(KEY_PHONE, Login_User);
				Log.e(TAG, "getParams: " + params);
				return params;
			}
		};


		//Adding request the the queue
		requestQueue.add(stringRequest);

	}


	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}


	@Override
	public void onRefresh(SwipyRefreshLayoutDirection direction) {

		afterRefresh = true;
		registerReceiver(cd, filter);
		CheckUserType();
	}


}
