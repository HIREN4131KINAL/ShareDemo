package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HIREN AMALIYAR on 23/10/2016.
 */
public class DownloadTaskIMG extends AsyncTask<String, Integer, String> {
	private Context context;
	private DownloadCallBack downloadCallBack;

	//  private PowerManager.WakeLock mWakeLock;
	//ProgressDialog mProgressDialog;

	private static final int MEGABYTE = 1024 * 1024;
	private String Name;
	private String URL;


	public DownloadTaskIMG(Context context, String internalImagePathUri, String Name, DownloadCallBack downloadCallBack) {
		this.context = context;
		this.Name = Name;
		URL = internalImagePathUri;
		this.downloadCallBack = downloadCallBack;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();

/*
		mProgressDialog = new ProgressDialog((Activity)context);
        // Set the progress dialog background color transparent
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        mProgressDialog.setMessage("Downloading....");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
        Window window = mProgressDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = -20;
        params.height = 150;
        params.width = 450;
        params.y = -10;
        window.setAttributes(params);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                String sdcard_path = Environment.getExternalStorageDirectory().getPath();
                File file = new File(sdcard_path + "/FileSharing/" + Name + ".jpg");
                file.delete();
                Toast.makeText(context, "Download cancle", Toast.LENGTH_SHORT).show();
            }
        });*/
	}


	@Override
	protected String doInBackground(String... str) {

		// String URL = str[0];
		// Name = str[1];
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			java.net.URL url = new URL(URL.concat(Name));
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
			Log.e("Path ------ ", " " + sdcard_path);
			// create a File object for the parent directory
			File PapersDiractory = new File(sdcard_path + "/FileSharing/");
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
				int progress = (int) (total * 100 / fileLength);
				Log.e("Progress = ", "" + progress);
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			return e.toString();
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// if we get here, length is known, now set indeterminate to false
	/*    mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);*/
	}

	@Override
	protected void onPostExecute(String result) {
		//  mWakeLock.release();
		//  mProgressDialog.dismiss();
		if (result != null)
			Toast.makeText(context, "Connection Error or file not found", Toast.LENGTH_LONG).show();
		else {
			downloadCallBack.onDownloadComplete();
			Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show();

		}
	}
}
