package com.example.guest999.firebasenotification.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.SearchGetSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harshad on 26-10-2016.
 */

public class Search extends AppCompatActivity {
    public EditText et_search;
    public ArrayList<SearchGetSet> User_info_search = new ArrayList<SearchGetSet>();
    SearchAdapter myAdapter;
    String TAG = getClass().getName();
    private RecyclerView rv;
    // converting arraylist to string array intializing size
    private String[] full_name = new String[UserList.hello.size()];
    private String[] full_no = new String[UserList.hello.size()];
    private String[] pro_pic = new String[UserList.hello.size()];
    // array list
    private String Login_User, image_external_Url, file_extenal_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        image_external_Url = i.getStringExtra("IMG_URL_SEARCH");
        file_extenal_Url = i.getStringExtra("FILE_URL_SEARCH");
        Log.e(TAG, "onCreate Search: " + image_external_Url);
        Log.e(TAG, "onCreate Search: " + file_extenal_Url);

        LoadUielements();
        LoadUILisners();

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

    private void LoadUielements() {
        rv = (RecyclerView) findViewById(R.id.recyclerview);
        et_search = (EditText) findViewById(R.id.et_search);

        Log.e("Search", String.valueOf(User_info_search));
        for (int i = 0; i < UserList.hello.size(); i++) {
            try {
                full_name[i] = UserList.hello.get(i).get(Config.KEY_USERNAME);
                full_no[i] = UserList.hello.get(i).get(Config.KEY_PHONE);
                pro_pic[i] = UserList.hello.get(i).get(Config.KEY_PROFILE_PATH);

                Log.e("Userlist", full_name[i]);
                Log.e("Userlist", full_no[i]);
                Log.e("Userlist", pro_pic[i] + "");

                SearchGetSet searchGetSet = new SearchGetSet(full_name[i], full_no[i], pro_pic[i]);
                User_info_search.add(searchGetSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        rv.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        Log.e("LoadUielements: ", User_info_search + "");
        myAdapter = new SearchAdapter(Search.this, User_info_search);
        rv.setAdapter(myAdapter);
    }

    private void LoadUILisners() {

        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = et_search.getText().toString().toLowerCase(Locale.getDefault());
                Log.e("afterTextChanged: ", text);
                myAdapter.filter(text);
            }
        });

    }


    class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

        Context context;
        private String User_Click_Phone;
        private List<SearchGetSet> searchlist = null;
        private ArrayList<SearchGetSet> mDataset;

        SearchAdapter(Search search, ArrayList<SearchGetSet> user_info_search) {
            this.context = search;
            this.searchlist = user_info_search;
            this.mDataset = new ArrayList<SearchGetSet>();
            this.mDataset.addAll(searchlist);
            Log.e("MyAdapter: mDataset ", mDataset + "");
            Log.e("MyAdapter: world ", searchlist + "");
        }

        @Override
        public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new SearchAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SearchAdapter.ViewHolder holder, final int position) {
            Log.e("onBindViewHolder: ", searchlist.get(position).getSearchName());
            Log.e("onBindViewHolder: ", searchlist.get(position).getSearchNo());
            Log.e("onBindViewHolder: ", searchlist.get(position).getThumbnail());

            holder.tv_name.setText(searchlist.get(position).getSearchName());
            holder.tv_phone.setText(searchlist.get(position).getSearchNo());

            String path = searchlist.get(position).getThumbnail();

            if (!path.isEmpty()) {
                Glide.with(Search.this)
                        .load(path)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(holder.profile_search);
            } else {
                Picasso.with(context)
                        .load(R.drawable.default_profile)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.profile_search);
            }

            holder.main_rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User_Click_Phone = searchlist.get(position).getSearchNo();
                    Intent i = new Intent(context, Data_Sharing.class);
                    Bundle extras = new Bundle();
                    if (image_external_Url != null) {
                        extras.putString("IMG_URL", image_external_Url);
                        Log.e("onClick: ", image_external_Url);
                    } else if (file_extenal_Url != null) {
                        extras.putString("FILE_URL", file_extenal_Url);
                        Log.e("onClick: ", file_extenal_Url);
                    }
                    image_external_Url = null;
                    file_extenal_Url = null;
                    extras.putString("Click_Phone", User_Click_Phone);
                    extras.putString(Config.KEY_USERNAME, searchlist.get(position).getSearchName());
                    i.putExtras(extras);
                    Log.e("onClick: ", User_Click_Phone + searchlist.get(position).getSearchName());
                    finish();
                    context.startActivity(i);

                }
            });
        }

        void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            searchlist.clear();
            Log.e("filter:1 ", searchlist + "");
            if (charText.length() == 0) {
                searchlist.addAll(mDataset);
                Log.e("filter:2 ", searchlist + "");
                Log.e("filter:3 ", mDataset + "");
            } else {
                for (SearchGetSet wp : mDataset) {
                    if (wp.getSearchName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getSearchNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                        Log.e("filter:4 ", wp + "");
                        searchlist.add(wp);
                        Log.e("filter:5 ", searchlist + "");
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return searchlist.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout main_rel;
            CircleImageView profile_search;
            // each data item is just a string in this case
            private TextView tv_name, tv_phone;

            @SuppressLint("WrongViewCast")
            ViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.m_name);
                tv_phone = (TextView) itemView.findViewById(R.id.phone_number);
                main_rel = (LinearLayout) itemView.findViewById(R.id.main_rel);
                profile_search = (CircleImageView) itemView.findViewById(R.id.profile_image_search);
            }
        }
    }
}



