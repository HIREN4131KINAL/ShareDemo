package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Harshad on 15-12-2016 at 09:34 AM.
 */

public class Delete_Data  {
    private SwipyRefreshLayout swipeRefreshLayout;
    private Context context;
    private RequestQueue requestQueue;
    private String TAG =getClass().getName();
    private String adid;

    public Delete_Data(Context context, String adid, SwipyRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.adid = adid;
        Log.e(TAG, "Delete_Data: "+ 1 );
        if (CheckConnection.ni != null) {
            DeleteUserData();
        } else {
            Toast.makeText(context, "No Internet Available", Toast.LENGTH_LONG).show();
        }

    }

    private void DeleteUserData() {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DELETE_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        if (response != null) {
                            if(response.contains("success")){

                            }
                            Log.e("onResponse: ",response );
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
                params.put(Config.ad_id, adid);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



}
