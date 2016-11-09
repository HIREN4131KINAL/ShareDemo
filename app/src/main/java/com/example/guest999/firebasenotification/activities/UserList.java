package com.example.guest999.firebasenotification.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.guest999.firebasenotification.Config.KEY_PHONE;

/**
 * Created by Harshad and Modified Joshi Tushar
 */


public class UserList extends AppCompatActivity {
    public static ArrayList<HashMap<String, String>> hello;
    RecyclerView lv;
    String Login_User, User_Click_Phone, admin_type;
    String TAG = getClass().getName();
    private RequestQueue requestQueue;
    public Uri imageUri, FilePath;


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
        admin_type = SharedPreferenceManager.getDefaults("type", getApplicationContext());
        Log.e(TAG, "onCreate login phone: " + Login_User);
        Log.e(TAG, "onCreate login phone: " + admin_type);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.e(TAG, "onCreate external: " + admin_type);
        if (Intent.ACTION_SEND.equals(action) && type != null && admin_type.contains("admin")) {
            if (type.startsWith("image/")) {
                imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.e("handleSendImage admin: ", imageUri + "");
            } else {
                FilePath = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.e("FILEPATH admin", FilePath + "");
            }
        } else if (admin_type.contains("user")) {
            if (type.startsWith("image/")) {
                imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.e("handleSendImage user: ", imageUri + "");
            } else {
                FilePath = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.e("FILEPATH user", FilePath + "");
            }
        } else {
        }

        if (admin_type.contains("admin")) {
            CheckUserType();
        } else if (admin_type.contains("user")) {
            Intent intent1 = new Intent(this, DataSharing_forUser.class);
            intent1.putExtra("Click_Phone user",Login_User);
            if (imageUri != null) {
                intent1.putExtra("U_IMG_URL", imageUri + "");
                Log.e("onClick user: ", imageUri + "");
            }  else if(FilePath!=null){
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
        lv.setLayoutManager(mLayoutManager);
        lv.setAdapter(new MyAdapter(UserList.this, hello));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_in_userlistscreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Intent intent = new Intent(UserList.this, Search.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:

                Intent setting = new Intent(UserList.this, User_Setting.class);
                startActivity(setting);

                return true;

            case R.id.action_logout:

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Would you like to logout?")
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


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();

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
                        hello = new ArrayList<>(); //helloo
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
                                    map.put("profile_path", object.getString(Config.KEY_PROFILE_PATH));
                                    //map.put("type",object.getString("type"));

                                    /*if(Objects.equals(Config.KEY_TYPE, "admin"))
                                    {
                                        Intent dataToAdmin=new Intent(UserList.this,Data_Sharing.class);
                                        startActivity(dataToAdmin);
                                    }
*/
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
            final String path = mDataset.get(position).get(Config.KEY_PROFILE_PATH);

            holder.name.setText(username);
            holder.phon.setText(phone);


            if (!path.isEmpty()) {
                Picasso.with(UserList.this)
                        .load(path)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            }else{
                Picasso.with(UserList.this)
                        .load(R.drawable.default_profile)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            }

            holder.main_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User_Click_Phone = mDataset.get(position).get(Config.KEY_PHONE);
                    Intent i = new Intent(UserList.this, Data_Sharing.class);
                    Bundle extras = new Bundle();
                    extras.putString("Click_Phone", User_Click_Phone);
                    extras.putString(Config.KEY_USERNAME, mDataset.get(position).get(Config.KEY_USERNAME));
                    i.putExtras(extras);
                    if (admin_type.contains("admin")) {
                        if (imageUri != null) {
                            i.putExtra("IMG_URL", imageUri + "");
                            Log.e("onClick admin: ", imageUri + "");
                        }else if(FilePath!=null){
                            i.putExtra("FILE_URL", FilePath + "");
                            Log.e("onClick admin: ", FilePath + "");
                        }
                    }
                    startActivity(i);
                    imageUri = null;
                    FilePath = null;
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
            private ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.m_name);
                phon = (TextView) itemView.findViewById(R.id.phone_number);
                main_lin = (LinearLayout) itemView.findViewById(R.id.main_lin);
                imageView = (ImageView) itemView.findViewById(R.id.profile_image_list);
            }
        }
    }
}

