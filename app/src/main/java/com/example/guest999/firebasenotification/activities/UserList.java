package com.example.guest999.firebasenotification.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
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
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.KEY_PHONE;

public class UserList extends AppCompatActivity {
    public static ArrayList<HashMap<String, String>> hello;
    RecyclerView lv;
    String Login_User, User_Click_Phone;
    String TAG = getClass().getName();
    MyAdapter myAdapter;
    private RequestQueue requestQueue;
    String date, time, ampma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        lv = (RecyclerView) findViewById(R.id.recyclerview_userlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitle("P. L. Shah & Co.");


        requestQueue = Volley.newRequestQueue(this);

        Login_User = SharedPreferenceManager.getDefaults("phone", getApplicationContext());
        Log.e(TAG, "onCreate login phone: " + Login_User);

        CheckUserType();

    }

    private void IntialAdapter() {
        lv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        lv.setLayoutManager(mLayoutManager);
        lv.setAdapter(new MyAdapter(UserList.this, hello));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Intent intent = new Intent(UserList.this, Search.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CheckUserType() {

        /*final ProgressDialog loading = ProgressDialog.show(this, "Requesting", "Please wait...", false, false);*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHECK_USERTYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        hello = new ArrayList<>();
                        if (response != null) {
                            try {
                                Log.e("full OnResponse", response);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("userlist");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("username", object.getString(Config.KEY_USERNAME));
                                    map.put("phone", object.getString(Config.KEY_PHONE));

                                    Log.e(TAG, "onResponse: map result " + map);

                                    hello.add(map);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                        IntialAdapter();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    loading.dismiss();
                        Toast.makeText(UserList.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onErrorResponse: ", error + "");
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

    

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<HashMap<String, String>> mDataset;
        private Context context;
        private int lastposition = -1;

        MyAdapter(Context list, ArrayList<HashMap<String, String>> userList) {
            context = list;
            mDataset = userList;
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_userlist, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            final String username = mDataset.get(position).get(Config.KEY_USERNAME);
            final String phone = mDataset.get(position).get(Config.KEY_PHONE);

            holder.name.setText(username);
            holder.phon.setText(phone);

            holder.main_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User_Click_Phone = mDataset.get(position).get(Config.KEY_PHONE);
                    Intent i = new Intent(UserList.this, Data_Sharing.class);
                    Bundle extras = new Bundle();
                    extras.putString("Click_Phone",User_Click_Phone);
                    extras.putString(Config.KEY_USERNAME,mDataset.get(position).get(Config.KEY_USERNAME));
                    i.putExtras(extras);
                    startActivity(i);

                }
            });

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastposition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastposition = position;
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name, phon;
            private LinearLayout main_lin;
            /*private ImageView imageView;*/

            ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.m_name);
                phon = (TextView) itemView.findViewById(R.id.phone_number);
                main_lin = (LinearLayout) itemView.findViewById(R.id.main_lin);
            }
        }
    }
}

