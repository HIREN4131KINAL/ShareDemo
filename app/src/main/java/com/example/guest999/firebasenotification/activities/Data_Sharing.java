package com.example.guest999.firebasenotification.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.guest999.firebasenotification.adapters.DataAdapter;
import com.example.guest999.firebasenotification.utilis.CheckConnection;
import com.example.guest999.firebasenotification.utilis.Clear_Screen_Admin;
import com.example.guest999.firebasenotification.utilis.DownloadCallBack;
import com.example.guest999.firebasenotification.utilis.FilePath;
import com.example.guest999.firebasenotification.utilis.JSONParser;
import com.example.guest999.firebasenotification.utilis.Logout_PLS;
import com.example.guest999.firebasenotification.utilis.MarshmallowPermissions;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.kosalgeek.android.caching.FileCacher;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.PhoneFromDevice;
import static com.example.guest999.firebasenotification.Config.PhoneFromURl;

// Modified by hiren for image compression and image management

public class Data_Sharing extends AppCompatActivity implements View.OnClickListener, SwipyRefreshLayout.OnRefreshListener, OnMenuItemClickListener, DownloadCallBack, OnMenuItemLongClickListener {
	public static final int RESULT_LOAD_FILE = 0;
	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_LOAD_IMAGE_CAPTURE = 2;
	public static final int RESULT_OK = -1;
	private static final int REQUEST_PERMISSION = 1;
	private static final int REQUEST_CODE_PICK_CONTACTS = 99;
	public static ArrayList<HashMap<String, String>> arraylist_ADMIN;
	public static SharedPreferences settings;


	//swipe refresh is used for to load new upcoming data Hiren.
	public SwipyRefreshLayout swipeRefreshLayout;


	public String imageFileName;
	protected String user_Click_Phone, image_external_Url, file_extenal_Url, contact_external_url;
	//
	DataAdapter dataAdapter;
	Uri mCapturedImageURI;
	// FileCacher is used for offline purpose Hiren.
	FileCacher<ArrayList<HashMap<String, String>>> stringCacher = new FileCacher<>(Data_Sharing.this, "cache_tmp.txt");
	RecyclerView recyclerView;
	Toolbar toolbar;
	MarshmallowPermissions marsh;
	boolean hidden = true;
	LinearLayout mRevealView;
	String TAG = getClass().getName();
	String date, time, ampma, Login_User, fileName, phoneNo, name;
	View parentLayout;
	//for Internet
	CheckConnection cd;
	IntentFilter filter;
	private File file = null;
	//Context menu
	private ContextMenuDialogFragment mMenuDialogFragment;
	private FragmentManager fragmentManager;

	private JSONParser jsonParser = new JSONParser();
	private Dialog dialog;
	private String selectedFilePath = null, filename = null;
	private RequestQueue requestQueue;
	private Uri contactData;

	DownloadCallBack downloadCallBack;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		downloadCallBack = this;
		//Context menu
		initMenuFragment();
		fragmentManager = getSupportFragmentManager();

		marsh = new MarshmallowPermissions(Data_Sharing.this);
		dialog = new Dialog(Data_Sharing.this);
		parentLayout = findViewById(android.R.id.content);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//for Internet
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


		if (!marsh.checkIfAlreadyhavePermission()) {
			marsh.requestpermissions();
		}


		VollyRequest();
		settings = getSharedPreferences(Login.PREFS_NAME, 0);
		Loaduiele();
		LoadLisners();
		IntialAdapter();

	}

	private void Loaduiele() {
		recyclerView = (RecyclerView) findViewById(R.id.recycler);

		swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));

		arraylist_ADMIN = new ArrayList<>();

		if (image_external_Url != null) {
			handleImage();
		} else if (contact_external_url != null) {
			handleContact();
		} else if (file_extenal_Url != null) {
			handleFile();
		}

	}

	private void LoadLisners() {

		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);
										LoadUserData();
									}
								}
		);


	}

	private void VollyRequest() {
		String type = SharedPreferenceManager.getDefaults("type", Data_Sharing.this);
		Login_User = SharedPreferenceManager.getDefaults("phone", Data_Sharing.this);

		if (type.contains("admin")) {
			Log.e(TAG, "onCreate external: " + type);
			Intent i = getIntent();
			Bundle extra = i.getExtras();
			user_Click_Phone = extra.getString("Click_Phone");
			image_external_Url = i.getStringExtra("IMG_URL");
			file_extenal_Url = i.getStringExtra("FILE_URL");
			Log.e(TAG, "onCreate: " + user_Click_Phone);
			Log.e(TAG, "onCreate: " + image_external_Url);
			Log.e(TAG, "onCreate: " + file_extenal_Url);
			String user_click_name = extra.getString(Config.KEY_USERNAME);

			getSupportActionBar().setTitle(null);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			toolbar.setTitle(user_click_name);
		} else {
			Toast.makeText(this, TAG + "Sorry Server Cant't Properly Work.", Toast.LENGTH_LONG).show();
		}
		requestQueue = Volley.newRequestQueue(this);


	}

	private void initMenuFragment() {
		MenuParams menuParams = new MenuParams();
		menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
		menuParams.setMenuObjects(getMenuObjects());
		menuParams.setClosableOutside(false);
		mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
		mMenuDialogFragment.setItemClickListener(this);
		mMenuDialogFragment.setItemLongClickListener(this);
	}

	private List<MenuObject> getMenuObjects() {
		// You can use any [resource, bitmap, drawable, color] as image:
		// item.setResource(...)
		// item.setBitmap(...)
		// item.setDrawable(...)
		// item.setColor(...)
		// You can set image ScaleType:
		// item.setScaleType(ScaleType.FIT_XY)
		// You can use any [resource, drawable, color] as background:
		// item.setBgResource(...)
		// item.setBgDrawable(...)
		// item.setBgColor(...)
		// You can use any [color] as text color:
		// item.setTextColor(...)
		// You can set any [color] as divider color:
		// item.setDividerColor(...)

		List<MenuObject> menuObjects = new ArrayList<>();

		MenuObject close = new MenuObject();
		close.setResource(R.drawable.cancel);

		MenuObject send = new MenuObject("Camera");
		send.setResource(R.drawable.camera);

		MenuObject like = new MenuObject("Gallery");
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gallery);
		like.setBitmap(b);

		MenuObject addFr = new MenuObject("Documents");
		BitmapDrawable bd = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(), R.drawable.document));
		addFr.setDrawable(bd);

		MenuObject addFav = new MenuObject("Contact");
		addFav.setResource(R.drawable.contact);

		MenuObject block = new MenuObject("Logout");
		block.setResource(R.drawable.logout);

		menuObjects.add(close);
		menuObjects.add(send);
		menuObjects.add(like);
		menuObjects.add(addFr);
		menuObjects.add(addFav);
		menuObjects.add(block);
		return menuObjects;
	}


	@Override
	protected void onResume() {
		super.onResume();
		//for load data
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);
										Log.d("Runnable method ", "");
										LoadUserData();
									}
								}
		);
		//for Internet
		registerReceiver(cd, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(cd);
			stringCacher.writeCache(arraylist_ADMIN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * For Get Contact Direct to Contact List
	 */
	public void handleContact() {
		try {
			Toast.makeText(this, contact_external_url, Toast.LENGTH_LONG).show();
			Uri uri = Uri.parse(contact_external_url);
			Log.e("handleContact: ", uri + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	 * For Get Image Direct to Gallery Modified by hiren
	 */
	public void handleImage() {
		try {
			Uri uri = Uri.parse(image_external_Url);
			Log.e("handleImage: ", uri + "");
			selectedFilePath = FilePath.getPath(this, uri);
			Log.e("handleImage11: ", selectedFilePath + "");

			//below code changed by hiren for to handle imge management and compress file please ask for if you want to edit it.
			File SELEC_TED_FILE_PATH = new File(selectedFilePath);

			//checking file exitstable or not
			if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

				swipeRefreshLayout.post(new Runnable() {
											@Override
											public void run() {
												swipeRefreshLayout.setRefreshing(true);
												Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_SHORT).show();
												//for compress image
												compressImage(selectedFilePath);
												if (filename != null && !filename.isEmpty()) {


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
															uploadFile(filename);
															new UploadImage().execute(filename);

														}
													}).start();

												} else {
													swipeRefreshLayout.setRefreshing(false);
													Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
															.setAction("CLOSE", new View.OnClickListener() {
																@Override
																public void onClick(View v) {

																}

															})
															.show();
												}
											}
										}
				);

			} else {
				swipeRefreshLayout.setRefreshing(false);
				Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
						.setAction("CLOSE", new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}

						})
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			swipeRefreshLayout.setRefreshing(false);
			Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
					.setAction("CLOSE", new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}

					})
					.show();
		}


	}

	/**
	 * For Get File Direct to File Manager
	 */
	public void handleFile() {
		try {
			Uri uri = Uri.parse(file_extenal_Url);
			Log.e("handleFile: ", uri + "");
			selectedFilePath = FilePath.getPath(this, uri);
			//below code changed by hiren for to handle imge management and compress file please ask for if you want to edit it.
			selectedFilePath = FilePath.getPath(this, uri);
			File SELEC_TED_FILE_PATH = new File(selectedFilePath);
			final long size = SELEC_TED_FILE_PATH.length();
			Log.e(TAG, "size of file: " + size / 1024 + "");
			if (selectedFilePath.toLowerCase().endsWith(".pdf") || selectedFilePath.toLowerCase().endsWith(".docx") || selectedFilePath.toLowerCase().endsWith(".doc") || selectedFilePath.toLowerCase().endsWith(".txt") || selectedFilePath.toLowerCase().endsWith(".ppt") || selectedFilePath.toLowerCase().endsWith(".pptx") || selectedFilePath.toLowerCase().endsWith(".zip") || selectedFilePath.toLowerCase().endsWith(".rar") || selectedFilePath.toLowerCase().endsWith(".xls") || selectedFilePath.toLowerCase().endsWith(".xlsx")) {

				if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

					swipeRefreshLayout.post(new Runnable() {
												@Override
												public void run() {
													swipeRefreshLayout.setRefreshing(true);
													if (size / 1024 > 13798) {
														Toast.makeText(Data_Sharing.this, "Please wait file size is large...", Toast.LENGTH_LONG).show();
													} else {
														Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_LONG).show();
													}
													if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

														//dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending . . .", true);

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
																uploadFile(selectedFilePath);
																new UploadImage().execute(selectedFilePath);

															}
														}).start();

													} else {
														swipeRefreshLayout.setRefreshing(false);
														Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
																.setAction("CLOSE", new View.OnClickListener() {
																	@Override
																	public void onClick(View v) {

																	}
																})
																.show();
													}
												}
											}
					);

				} else {
					swipeRefreshLayout.setRefreshing(false);
					Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
							.setAction("CLOSE", new View.OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							})
							.show();
				}

			} else {
				android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Data_Sharing.this);
				builder.setTitle("Warning For File Choosing");
				builder.setMessage("File selected by you is not appropriate for this application.");
				builder.setIcon(R.drawable.warning);
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								FilePicker();
							}
						});
				android.support.v7.app.AlertDialog dialog = builder.create();
				// display dialog
				dialog.show();
			}


		} catch (Exception e) {
			e.printStackTrace();
			swipeRefreshLayout.setRefreshing(false);
			Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
					.setAction("CLOSE", new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					})
					.show();
		}

	}

	private void IntialAdapter() {

		//Modified by hiren for item inserted and refresh layout
		recyclerView.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(Data_Sharing.this);
		recyclerView.setLayoutManager(mLayoutManager);
		dataAdapter = new DataAdapter(Data_Sharing.this, arraylist_ADMIN, swipeRefreshLayout);
		recyclerView.scrollToPosition(arraylist_ADMIN.size() - 1);
		dataAdapter.notifyItemInserted(arraylist_ADMIN.size() - 1);
		recyclerView.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
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
					activeContact();
				} else {
					marsh.requestpermissions();
				}
				mRevealView.setVisibility(View.INVISIBLE);
				hidden = true;
				break;
		}

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
			if (photoFile != null && photoFile.isFile()) {
			/*	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_CAPTURE);*/

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE, imageFileName);
				mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
				startActivityForResult(intentPicture, RESULT_LOAD_IMAGE_CAPTURE);

			}
		}


	}

	/**
	 * For Save Image
	 */
	private File createImageFile() throws IOException {
		//Modified by hiren for save image in custom folder
		//below code modified by Hiren please ask before change it.
		// Create an gallery file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		imageFileName = "JPEG_" + timeStamp + "";
	/*	File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);*/

		String sdcard_path = Environment.getExternalStorageDirectory().getPath();
		Log.e("Path_CreateImageFile ", " " + sdcard_path);
		// create a File object for the parent directory
		File MyCustomDiractory = new File(sdcard_path + "/P L Shah/P L Shah Images/");
		// have the object build the directory structure, if needed.
		if (!MyCustomDiractory.getParentFile().exists()) {
			MyCustomDiractory.getParentFile().mkdirs();
		}

		File image_file = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				MyCustomDiractory     /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		//	mCurrentPhotoPath = image_file.getAbsolutePath();

		return image_file;
	}

	public void FilePicker() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		startActivityForResult(intent, RESULT_LOAD_FILE);
	}

	/**
	 * For Intent Contact
	 */
	public void activeContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
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

	private void activeGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RESULT_LOAD_IMAGE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_in_data_admin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			Intent back = new Intent(this, UserList.class);
			back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(back);
			return true;
		}

		if (id == R.id.context_menu) {
			if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
				mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
			}
		}
		if (id == R.id.action_call) {

			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + user_Click_Phone));
			Log.e("onClick: ", user_Click_Phone);
			if (ActivityCompat.checkSelfPermission(Data_Sharing.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return true;
			}
			startActivity(callIntent);

		}
		if (id == R.id.action_clr) {
			android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Data_Sharing.this);
			builder.setTitle("Clear Screen");
			builder.setMessage("Are you sure wan't to clear screen?");
			builder.setIcon(R.drawable.error);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Clear_data();
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			android.support.v7.app.AlertDialog dialog = builder.create();
			// display dialog
			dialog.show();
		}

		return super.onOptionsItemSelected(item);
	}

	private void Clear_data() {
		if (CheckConnection.ni != null) {
			Clear_Screen_Admin clear_screen = new Clear_Screen_Admin(Data_Sharing.this, SharedPreferenceManager.getDefaults("phone", Data_Sharing.this), user_Click_Phone, downloadCallBack, swipeRefreshLayout);
			Log.e("Clear_data: ", user_Click_Phone);
			Log.e("Clear_data: ", SharedPreferenceManager.getDefaults("phone", Data_Sharing.this));
			downloadCallBack.onDownloadComplete();

		} else {
			Toast.makeText(Data_Sharing.this, "No Internet Available", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		if (position == 1) {
			if (CheckConnection.ni != null) {
				activeTakePhoto();
			} else {
				registerReceiver(cd, filter);
			}
		} else if (position == 2) {
			if (CheckConnection.ni != null) {
				activeGallery();
			} else {
				registerReceiver(cd, filter);
			}
		} else if (position == 3) {
			if (CheckConnection.ni != null) {

				FilePicker();
			} else {
				registerReceiver(cd, filter);
			}
		} else if (position == 4) {
			if (CheckConnection.ni != null) {
				activeContact();
			} else {
				registerReceiver(cd, filter);
			}
		} else if (position == 5) {
			Logout_PLS logout_pls = new Logout_PLS(this, requestQueue
			);

			logout_pls.get_ready_logout();
		/*	new AlertDialog.Builder(this)
					.setTitle("Logout")
					.setMessage("Would you like to logout?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							try {
								SharedPreferenceManager.setDefaults_boolean("notification", false, getApplicationContext());
								Login.settings.edit().clear().apply();

								SharedPreferenceManager.ClearAllPreferences(getApplicationContext());
								Intent logout = new Intent(Data_Sharing.this, Login.class);

								// this flag prevent back to in to application after logout.
								logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
										Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(logout);
								finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// user doesn't want to logout
						}
					})
					.show();*/

		}


	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {

		if (position == 1) {
			Toast.makeText(this, "Camera ", Toast.LENGTH_SHORT).show();
		} else if (position == 2) {
			Toast.makeText(this, "Gallery ", Toast.LENGTH_SHORT).show();
		} else if (position == 3) {
			Toast.makeText(this, "Document ", Toast.LENGTH_SHORT).show();
		} else if (position == 4) {
			Toast.makeText(this, "Contact", Toast.LENGTH_SHORT).show();
		} else if (position == 5) {
			Toast.makeText(this, "Logout ", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent a = new Intent(this, UserList.class);
			a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CheckConnection.ni != null) {

			switch (requestCode) {
				case RESULT_LOAD_IMAGE:
					if (resultCode == RESULT_OK && null != data) {
						try {
							final Uri filePath = data.getData();
							Log.e("IMAGE", filePath + "");
							//below code changed by hiren for to handle imge management and compress file please ask for if you want to edit it.
							//cheking file is existable or not.

							selectedFilePath = FilePath.getPath(this, filePath);

							File SELEC_TED_FILE_PATH = new File(selectedFilePath);

							if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

								swipeRefreshLayout.post(new Runnable() {
															@Override
															public void run() {
																swipeRefreshLayout.setRefreshing(true);
																Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_SHORT).show();
																//for compress image
																compressImage(selectedFilePath);
																if (filename != null && !filename.isEmpty()) {


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
																			uploadFile(filename);
																			new Data_Sharing.UploadImage().execute(filename);

																		}
																	}).start();

																} else {
																	swipeRefreshLayout.setRefreshing(false);
																	Snackbar.make(parentLayout, "File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
																			.setAction("RETRY", new View.OnClickListener() {
																				@Override
																				public void onClick(View v) {
																					activeGallery();
																				}

																			})
																			.show();
																}
															}
														}
								);

							} else {
								swipeRefreshLayout.setRefreshing(false);
								Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
										.setAction("RETRY", new View.OnClickListener() {
											@Override
											public void onClick(View v) {

												activeGallery();
											}

										})
										.show();

							}
						} catch (Exception e) {
							e.printStackTrace();
							swipeRefreshLayout.setRefreshing(false);
							Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
									.setAction("RETRY", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											activeGallery();
										}

									})
									.show();
						}


					}


				case RESULT_LOAD_IMAGE_CAPTURE:
					if (requestCode == RESULT_LOAD_IMAGE_CAPTURE &&
							resultCode == RESULT_OK) {
						//below code changed by hiren for to handle imge management and compress file please ask for if you want to edit it.
						try {
							selectedFilePath = getRealPathFromURI(String.valueOf(mCapturedImageURI)); // don't use data.getData() as it return null in some device instead  use mCapturedImageUR uri variable statically
							File SELEC_TED_FILE_PATH = new File(selectedFilePath);

							if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

								swipeRefreshLayout.post(new Runnable() {
															@Override
															public void run() {
																swipeRefreshLayout.setRefreshing(true);
																Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_SHORT).show();
																//for compress image
																compressImage(selectedFilePath);
																if (filename != null && !filename.isEmpty()) {

																	//dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending . . .", true);

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
																			uploadFile(filename);
																			new Data_Sharing.UploadImage().execute(filename);

																		}
																	}).start();

																} else {
																	swipeRefreshLayout.setRefreshing(false);
																	Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
																			.setAction("RETRY", new View.OnClickListener() {
																				@Override
																				public void onClick(View v) {
																					activeTakePhoto();
																				}
																			})
																			.show();
																}
															}
														}
								);

							} else {
								swipeRefreshLayout.setRefreshing(false);
								Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
										.setAction("RETRY", new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												activeTakePhoto();
											}
										})
										.show();
							}
						} catch (Exception e) {
							e.printStackTrace();
							swipeRefreshLayout.setRefreshing(false);
							Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
									.setAction("RETRY", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											activeTakePhoto();
										}
									})
									.show();
						}
					}


				case RESULT_LOAD_FILE:
					if (requestCode == RESULT_LOAD_FILE &&
							resultCode == RESULT_OK && null != data) {

						try {
							Uri selectedFileUri = data.getData();
							selectedFilePath = FilePath.getPath(this, selectedFileUri);
							File SELEC_TED_FILE_PATH = new File(selectedFilePath);
							final long size = SELEC_TED_FILE_PATH.length();
							Log.e(TAG, "size of file: " + size / 1024 + "");
							if (selectedFilePath.toLowerCase().endsWith(".pdf") || selectedFilePath.toLowerCase().endsWith(".docx") || selectedFilePath.toLowerCase().endsWith(".doc") || selectedFilePath.toLowerCase().endsWith(".txt") || selectedFilePath.toLowerCase().endsWith(".ppt") || selectedFilePath.toLowerCase().endsWith(".pptx") || selectedFilePath.toLowerCase().endsWith(".zip") || selectedFilePath.toLowerCase().endsWith(".rar") || selectedFilePath.toLowerCase().endsWith(".xls") || selectedFilePath.toLowerCase().endsWith(".xlsx")) {

								if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

									swipeRefreshLayout.post(new Runnable() {
																@Override
																public void run() {
																	swipeRefreshLayout.setRefreshing(true);
																	if (size / 1024 > 13798) {
																		Toast.makeText(Data_Sharing.this, "Please wait file size is large...", Toast.LENGTH_LONG).show();
																	} else {
																		Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_LONG).show();
																	}
																	if (selectedFilePath != null && !selectedFilePath.isEmpty()) {

																		//dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending . . .", true);

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
																				uploadFile(selectedFilePath);
																				new Data_Sharing.UploadImage().execute(selectedFilePath);

																			}
																		}).start();

																	} else {
																		swipeRefreshLayout.setRefreshing(false);
																		Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
																				.setAction("RETRY", new View.OnClickListener() {
																					@Override
																					public void onClick(View v) {
																						FilePicker();
																					}
																				})
																				.show();
																	}
																}
															}
									);

								} else {
									swipeRefreshLayout.setRefreshing(false);
									Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
											.setAction("RETRY", new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													FilePicker();
												}
											})
											.show();
								}

							} else {
								android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Data_Sharing.this);
								builder.setTitle("Warning For File Choosing");
								builder.setMessage("File selected by you is not appropriate for this application.");
								builder.setIcon(R.drawable.warning);
								builder.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												FilePicker();
											}
										});
								android.support.v7.app.AlertDialog dialog = builder.create();
								// display dialog
								dialog.show();
							}


						} catch (Exception e) {
							e.printStackTrace();
							swipeRefreshLayout.setRefreshing(false);
							Snackbar.make(parentLayout, "Source File Doesn't Exist or corrupted", Snackbar.LENGTH_INDEFINITE)
									.setAction("RETRY", new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											FilePicker();
										}
									})
									.show();
						}
					}
				case REQUEST_CODE_PICK_CONTACTS:
					if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK && null != data) {
						try {
							contactData = data.getData();
							Log.e("onActivityResult: ", contactData + "");
							ContactNo();

							if (contactData != null) {

								//dialog = ProgressDialog.show(Data_Sharing.this, "", "Sending . . .", true);

								Date currentDate = Calendar.getInstance().getTime();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
								final String formattedCurrentDate = simpleDateFormat.format(currentDate);

								String[] splited = formattedCurrentDate.split("\\s+");
								date = splited[0];
								time = splited[1];
								ampma = splited[2];

								new Thread(new Runnable() {
									@Override
									public void run() {
										//creating new thread to handle Http Operations
										swipeRefreshLayout.post(new Runnable() {
																	@Override
																	public void run() {
																		swipeRefreshLayout.setRefreshing(true);
																		Toast.makeText(Data_Sharing.this, "Sending . . .", Toast.LENGTH_SHORT).show();
																	}
																}
										);
										new Data_Sharing.UploadContact().execute();

										swipeRefreshLayout.setRefreshing(false);
									}
								}).start();

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}


			}
		} else {
			dialog.dismiss();
			registerReceiver(cd, filter);
		}
	}

	//android upload file or image to server folder
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
			//dialog.dismiss();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(Data_Sharing.this, "Source File Doesn't Exist: " + selectedFilePath, Toast.LENGTH_SHORT).show();
				}
			});
			return serverResponseCode;
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
// send multipart form data necesssary after file
				// data...
				dataOutputStream.writeBytes(lineEnd);
				dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				// Responses from the server (code and message)
				serverResponseCode = connection.getResponseCode();
				String serverResponseMessage = connection.getResponseMessage();

				Log.e(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
				//response code of 200 indicates the server status OK
				if (serverResponseCode == 200) {
					//afterRefresh = true;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Thanks for sending to P.L.Shah", Toast.LENGTH_SHORT).show();
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
				//	Toast.makeText(getApplicationContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
			}
			//dialog.dismiss();
			return serverResponseCode;
		}

	}

	//loading user screen data.
	private void LoadUserData() {
		swipeRefreshLayout.setRefreshing(true);
		/*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOAD_USERDATA,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//loading.dismiss();

						if (swipeRefreshLayout.isRefreshing())
							swipeRefreshLayout.setRefreshing(false);
						arraylist_ADMIN = new ArrayList<>();
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
									map.put(Config.KEY_LOCAL_PATH, object.getString(Config.KEY_LOCAL_PATH));
									map.put(Config.KEY_FILE_SIZE, object.getString(Config.KEY_FILE_SIZE));
									map.put(Config.ad_id, object.getString(Config.ad_id));
									Log.e(TAG, "onResponse: map result " + map);

									arraylist_ADMIN.add(map);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Log.e("ServiceHandler", "Couldn't get any data from the url");
						}


						if (getApplicationContext() != null) {

							IntialAdapter();

						} else {
							Toast.makeText(Data_Sharing.this, "Something went wrong", Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//loading.dismiss();
						Log.e("onErrorResponse: ", error + "");
						swipeRefreshLayout.setRefreshing(false);

					}
				}

		)

		{
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

	@Override
	public void onRefresh(SwipyRefreshLayoutDirection direction) {
		swipeRefreshLayout.setRefreshing(true);

		if (CheckConnection.ni != null) {
			LoadUserData();
		} else {

			registerReceiver(cd, filter);
			swipeRefreshLayout.setRefreshing(false);
		}

	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(final int requestCode, String[] permissions,
										   int[] grantResults) {
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

						marsh.AllowedManually(parentLayout);


					} else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
						// showRationale(permission, R.string.permission_denied);
						// user denied WITHOUT never ask again
						// this is a good place to explain the user
						// why you need the permission and ask if he want
						// to accept it (the rationale)
						marsh.AllowedManually(parentLayout);
					}
				}
			}
		}
	}

	public String compressImage(String imageUri) {

		String filePath = getRealPathFromURI(imageUri);
		Bitmap scaledBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;

			}
		}

//      setting inSampleSize value allows to load a scaled down version of the original image

		options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
		options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
//          load the bitmap from its path
			bmp = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileOutputStream out;
		filename = getFilename();
		try {
			out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return filename;
	}

	public String getFilename() {
	/*	File file = null;*/
		try {
			file = createImageFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String uriSting = (file.getAbsolutePath());
		return uriSting;

	}

	private String getRealPathFromURI(String contentURI) {
		Uri contentUri = Uri.parse(contentURI);
		Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(index);
		}
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	@Override
	public void onBackPressed() {
		if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
			mMenuDialogFragment.dismiss();
		} else {
			finish();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// modified by hiren
		if (swipeRefreshLayout.isRefreshing())
			swipeRefreshLayout.setRefreshing(false);
		/*if (dialog.isShowing())
			dialog.dismiss();*/
		if (!marsh.checkIfAlreadyhavePermission()) {
			marsh.requestpermissions();
		}
	}

	@Override
	public void onDownloadComplete() {
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);
										LoadUserData();
									}
								}
		);
	}

	//for insert image name into db
	public class UploadImage extends AsyncTask<String, Void, String> {

		static final String UPLOAD_KEY = "image";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			swipeRefreshLayout.post(new Runnable() {
										@Override
										public void run() {
											swipeRefreshLayout.setRefreshing(true);
										}
									}
			);

		}


		@Override
		protected String doInBackground(String... params) {

			HashMap<String, String> data = new HashMap<>();
			data.put(Config.KEY_PHONE, user_Click_Phone);
			data.put(UPLOAD_KEY, fileName);
			data.put(Config.KEY_LOCAL_PATH, selectedFilePath);
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
			swipeRefreshLayout.setRefreshing(false);
			LoadUserData();
			//	sendNotification();

		}

		private void sendNotification() {
			try {
				URL url = new URL("http://laxmisecurity.com/android/firebase/index.php?regId=cTw3R_zbDzk%3AAPA91bF4HFkPvf92u7GjAp6jI3qo-Fqnghe8BU50PZet9RwSUl_FszP4DPx5Oq5V1NZgi6xugKGnT0Jo_yUvCfZSPKOclo8NcLeYsJ98jPd95RcGJArRWpH8sr0BtIxECqmMPyr2AsNn&title=You+Have+Message&message=hi+hello+Android&push_type=individual");
				Login.webview.loadUrl(String.valueOf(url));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
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

			LoadUserData();
		}

		@Override
		protected String doInBackground(String... params) {
			HashMap<String, String> data = new HashMap<>();
			data.put(Config.KEY_PHONE, user_Click_Phone);
			data.put(UPLOAD_KEY, phoneNo.replaceAll(" ", "") + "::" + name);
			data.put(Config.KEY_A_PHONE, SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
			data.put(Config.CURRENT_DATE, date);
			data.put(Config.CURRENT_TIME, time + " " + ampma);
			Log.e("HashMap data: ", data + "");
			String result = jsonParser.sendPostRequest(Config.COTACTSEND_ADMIN, data);
			Log.e("result: ", result);
			return result;
		}
	}

}
