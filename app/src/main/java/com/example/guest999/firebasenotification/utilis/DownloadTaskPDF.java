package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arpit Patel on 11-Apr-16.
 */
public class DownloadTaskPDF extends AsyncTask<String, Integer, String> {
	private static final int MEGABYTE = 1024 * 1024;
	private Context context;
	private PowerManager.WakeLock mWakeLock;
	//private ProgressDialog mProgressDialog;
	private String Name;
	private DownloadCallBack downloadCallBack;
	private SwipyRefreshLayout swipeRefreshLayout;
	private String name;
	private String URL;


	public DownloadTaskPDF(Context context, String internalImagePathUri, String name, DownloadCallBack downloadCallBack, SwipyRefreshLayout swipeRefreshLayout) {
		this.context = context;
		this.downloadCallBack = downloadCallBack;
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.name = name;
		this.URL = internalImagePathUri;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
										swipeRefreshLayout.setRefreshing(true);
									}
								}
		);

	/*
		mProgressDialog = new ProgressDialog(context);
		// Set your progress dialog Title
		mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
		// Set your progress dialog Message
		mProgressDialog.setMessage("Downloading....");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		// Show progress dialog
		mProgressDialog.show();

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getClass().getName());
		//mWakeLock.acquire();
		Window window = mProgressDialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.x = -20;
		params.height = 150;
		params.width = 450;
		params.y = -10;
		//window.setAttributes(params);

		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

				String sdcard_path = Environment.getExternalStorageDirectory().getPath();
				File file = new File(sdcard_path + "/FileSharing/" + Name + ".pdf");
				file.delete();
				Toast.makeText(context, "Download In Background", Toast.LENGTH_SHORT).show();
			}
		});*/
	}

	@Override
	protected String doInBackground(String... str) {

		try {
			String URL = str[0];
			Name = str[1];
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(URL);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP " + connection.getResponseCode()
							+ " " + connection.getResponseMessage();
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				int fileLength = connection.getContentLength();

				// download the file
				input = connection.getInputStream();

				String sdcard_path = Environment.getExternalStorageDirectory().getPath();
				//   Log.d("Path ------ ", " " + sdcard_path);
				// create a File object for the parent directory
				File PapersDiractory = new File(sdcard_path + "/P L Shah/P L Shah File/");
				// have the object build the directory structure, if needed.
				PapersDiractory.mkdirs();
				// create a File object for the output file
				File outputFile = new File(PapersDiractory, "" + Name);
				// now attach the OutputStream to the file object, instead of a String representation
				output = new FileOutputStream(outputFile);
//                 output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/five-point-someone-chetan-bhagat_ebook.pdf");

				byte data[] = new byte[MEGABYTE];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
						publishProgress((int) (total * 100 / fileLength));
				/*	int progress = (int) (total * 100 / fileLength);
					//	Log.e("Progress = ", "" + progress);*/
					output.write(data, 0, count);
				}
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.flush();
					output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}

		} catch (Exception e) {
			// Error Log
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// if we get here, length is known, now set indeterminate to false
		Log.e("hirenUpdate: ", String.valueOf(progress[0]));
	/*	mProgressDialog.setIndeterminate(true);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(progress[0]);*/
	}

	@Override
	protected void onPostExecute(String result) {
//        mWakeLock.release();
/*		mProgressDialog.dismiss();*/

		if (result != null) {
			swipeRefreshLayout.setRefreshing(false);
			Toast.makeText(context, "Connection Error or file not found", Toast.LENGTH_LONG).show();
		} else {

			swipeRefreshLayout.setRefreshing(false);
			downloadCallBack.onDownloadComplete();
			Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
		}
	}

}
