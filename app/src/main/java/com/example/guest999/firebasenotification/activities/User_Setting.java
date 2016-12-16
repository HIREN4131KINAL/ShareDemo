package com.example.guest999.firebasenotification.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.guest999.firebasenotification.utilis.CheckConnection;
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
import java.io.FileOutputStream;
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
public class User_Setting extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
	public static final int RESULT_OK = -1;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int RESULT_LOAD_IMAGE_CAPTURE = 2;
	private final String setting[] = {
			"Change Password",
			"Log out",

	};
	private final int android_image_urls[] = {
			R.drawable.changepass,
			R.drawable.setting_logout,

	};
	public String imageFileName;
	CircleImageView profile_image;
	ProfileAdapter adapter;
	TextView title_name, tv_no, tv_name;
	//for Internet
	CheckConnection cd;
	IntentFilter filter;
	SwipeRefreshLayout swipeRefreshLayout;
	Uri mCapturedImageURI;
	private Dialog dialog;
	private RecyclerView recyclerView;
	private String fileName, path;
	private String selectedFilePath = null, filename = null;
	private JSONParser jsonParser = new JSONParser();
	private RequestQueue requestQueue;
	private File file = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usersetting);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//for Internet
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


		LoaduIelements();
		LoadUserData();
		LoadUIListner();


	}

	private void InitialAdapter() {
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(User_Setting.this);
		recyclerView.setLayoutManager(layoutManager);
		ArrayList<ServiceGetSet> androidVersions = prepareData();
		adapter = new ProfileAdapter(User_Setting.this, androidVersions, requestQueue);
		recyclerView.setAdapter(adapter);
	}

	public void LoaduIelements() {
		profile_image = (CircleImageView) findViewById(R.id.profile_image);
		requestQueue = Volley.newRequestQueue(this);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_profile);
		title_name = (TextView) findViewById(R.id.app_name);
		tv_no = (TextView) findViewById(R.id.tv_pro_no);
		tv_name = (TextView) findViewById(R.id.tv_pro_name);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/royal-serif.ttf");
		title_name.setTypeface(face);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0000"), Color.parseColor("#00ff00"), Color.parseColor("#0000ff"), Color.parseColor("#f234ab"));
		swipeRefreshLayout.animate();
	}

	public void LoadUIListner() {

		profile_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (CheckConnection.ni != null) {
					imgAddOnClick();
				} else {
					registerReceiver(cd, filter);
				}
			}
		});

		swipeRefreshLayout.setRefreshing(false);
		swipeRefreshLayout.setOnRefreshListener(User_Setting.this);
		swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				Log.e("Runnable method ", "helloo");

				// registerReceiver(cd, filter);
				// Loading b_name JSON in Background Thread
				LoadUserData();
			}
		});
	}

	@Override
	public void onRefresh() {
		registerReceiver(cd, filter);
		LoadUserData();
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
				dialog.dismiss();
			}
		}
	}

	//checked

	/**
	 * For Save Image
	 */
	private File createImageFile() throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		imageFileName = "JPEG_" + timeStamp + "";
	/*	File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);*/

		String sdcard_path = Environment.getExternalStorageDirectory().getPath();
		Log.e("Path_CreateImageFile ", " " + sdcard_path);
		// create a File object for the parent directory
		File MyCustomDiractory = new File(sdcard_path + "/FileSharing/");
		// have the object build the directory structure, if needed.
		if (!MyCustomDiractory.exists()) {
			MyCustomDiractory.mkdirs();
		}

		File image_file = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				MyCustomDiractory     /* directory */
		);

		return image_file;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CheckConnection.ni != null) {
			switch (requestCode) {
				case RESULT_LOAD_IMAGE:
					if (resultCode == RESULT_OK && null != data) {
						final Uri filePath = data.getData();
						Log.e("IMAGE", filePath + "");
						selectedFilePath = FilePath.getPath(this, filePath);

						File SELEC_TED_FILE_PATH = new File(selectedFilePath);
						if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

							compressImage(selectedFilePath);
							if (filename != null && !filename.isEmpty()) {
								dialog = ProgressDialog.show(User_Setting.this, "", "Uploading...", true);
								try {
									new Thread(new Runnable() {
										@Override
										public void run() {
											//creating new thread to handle Http Operations
											uploadFile(filename);
											new UploadImage().execute(filename);
										}
									}).start();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
						}
					}

				case RESULT_LOAD_IMAGE_CAPTURE:
					if (requestCode == RESULT_LOAD_IMAGE_CAPTURE &&
							resultCode == RESULT_OK) {
						try {
							selectedFilePath = getRealPathFromURI(String.valueOf(mCapturedImageURI));
							File SELEC_TED_FILE_PATH = new File(selectedFilePath);
							Log.e("onActivityResult: ", selectedFilePath);

							if (SELEC_TED_FILE_PATH.length() > 0 && SELEC_TED_FILE_PATH.isFile()) {

								compressImage(selectedFilePath);
								if (filename != null && !filename.isEmpty()) {
									dialog = ProgressDialog.show(User_Setting.this, "", "Uploading...", true);

									try {
										new Thread(new Runnable() {
											@Override
											public void run() {
												//creating new thread to handle Http Operations
												uploadFile(filename);
												new UploadImage().execute(filename);
											}
										}).start();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Toast.makeText(getApplicationContext(), "Please choose a File First", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}
		} else {
			registerReceiver(cd, filter);
			dialog.dismiss();
		}
	}

	private void LoadUserData() {
		swipeRefreshLayout.setRefreshing(true);
		/*final ProgressDialog loading = ProgressDialog.show(this, "Loading Data", "Please wait...", false, false);*/
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOAD_PROFILE,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//loading.dismiss();
						swipeRefreshLayout.setRefreshing(false);
						if (response != null) {
							try {
								Log.e("Full OnResponse jhgvgh" + "", response);

								JSONObject jsonObject = new JSONObject(response);
								JSONArray jsonArray = jsonObject.getJSONArray("profilepic");

								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = jsonArray.getJSONObject(i);
									path = object.getString(Config.KEY_PROFILE_PATH);
									String u_name = object.getString(Config.KEY_USERNAME);

									Log.e("onResponse: ", path + " " + u_name);

									tv_no.setText(SharedPreferenceManager.getDefaults("phone", getApplicationContext()));
									tv_name.setText(u_name);


									Picasso.with(User_Setting.this)
											.load(path)
											.placeholder(R.drawable.profile)
											.error(R.drawable.profile)
											.into(profile_image, new Callback() {
												@Override
												public void onSuccess() {
													swipeRefreshLayout.setRefreshing(false);
													profile_image.setVisibility(View.VISIBLE);
												}

												@Override
												public void onError() {
													swipeRefreshLayout.setRefreshing(true);
												}
											});

									InitialAdapter();
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
						//   Toast.makeText(User_Setting.this, "No Internet connection", Toast.LENGTH_LONG).show();
						if (error != null) {
							profile_image.setVisibility(View.VISIBLE);
							swipeRefreshLayout.setRefreshing(false);
						}
						swipeRefreshLayout.setRefreshing(false);

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
							Toast.makeText(getApplicationContext(), "Uploaded Sucessfuly", Toast.LENGTH_SHORT).show();

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
				//Toast.makeText(getApplicationContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();
			return serverResponseCode;
		}

	}

	//checked
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

		float maxHeight = 300.0f;
		float maxWidth = 300.0f;
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

	//checked
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

	//checked
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

	//checked
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
			dialog.dismiss();
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
