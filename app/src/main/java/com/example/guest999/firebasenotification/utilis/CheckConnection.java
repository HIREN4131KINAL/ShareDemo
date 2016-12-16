package com.example.guest999.firebasenotification.utilis;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.activities.Login;

/**
 * Created by Guest999 on 11/11/2016.
 */
public class CheckConnection extends BroadcastReceiver {
    private static final String TAG = "CheckConnection";
    ConnectivityManager connectivityManager;
    public static NetworkInfo ni;
    View view;
    private ProgressDialog progressBar;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.e(TAG, "Network connectivity change");

        if (intent.getExtras() != null) {
            connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            ni = connectivityManager.getActiveNetworkInfo();
            try {

                Login.webview.loadUrl("http://www.google.com");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ni != null && ni.isConnected()) {

                try {
                    Login.webview.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.e(TAG, "Processing webview url click...");
                            view.loadUrl(url);
                            return true;
                        }

                        public void onPageFinished(WebView view, String url) {
                            Log.e(TAG, "Finished loading URL: " + url);
                            Login.webview.setVisibility(View.GONE);
                        }

                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            Log.e(TAG, "Error: " + errorCode);
                            Toast.makeText(context, "Intenet Connection is low", Toast.LENGTH_LONG).show();
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if(ni == null){
                Log.e(TAG, "There's no network connectivity");
                try {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setTitle("Network problem");
                    builder.setMessage("Please Check you internet connection");
                    builder.setIcon(R.drawable.error);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    // display dialog
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
