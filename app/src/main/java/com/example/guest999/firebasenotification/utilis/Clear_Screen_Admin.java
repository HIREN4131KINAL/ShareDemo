package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.guest999.firebasenotification.Config;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harshad on 15-12-2016 at 04:27 PM.
 */

public class Clear_Screen_Admin {
    private SwipyRefreshLayout swipeRefreshLayout;
    private DownloadCallBack deleteCallback;
    private Context context;
    private JSONParser jsonParser = new JSONParser();
    private RequestQueue requestQueue;
    private String TAG = getClass().getName();
    private String phoneFromDevice, phonefromurl;

    public Clear_Screen_Admin(Context context, String phoneFromDevice, String phonefromurl, DownloadCallBack downloadCallBack, SwipyRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.context = context;
        this.deleteCallback = downloadCallBack;
        this.phoneFromDevice = phoneFromDevice;
        this.phonefromurl = phonefromurl;
        requestQueue = Volley.newRequestQueue(context);
        Log.e(TAG, "Delete_Data: " + 1);
        ClearData();

    }

    private void ClearData() {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ClearScreenData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        if (response != null) {
                            if (response.contains("success")) {

                            }
                            Log.e("onResponse: ", response);
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Config.KEY_PHONE, phonefromurl);
                params.put(Config.KEY_A_PHONE, SharedPreferenceManager.getDefaults("phone",context));
                Log.e(TAG, "getParams: "+params );
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
