package com.example.guest999.firebasenotification.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harshad on 26-10-2016.
 */


public class Search extends AppCompatActivity {
    public static ArrayList<HashMap<String, String>> User_info_search;
    public EditText et_search;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        User_info_search = UserList.hello;

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
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        et_search = (EditText) findViewById(R.id.et_search);


        searchAdapter = new SearchAdapter(Search.this, User_info_search);
        Log.e("Data", String.valueOf(User_info_search));
        rv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(new SearchAdapter(getApplicationContext(), User_info_search));
    }

    private void LoadUILisners() {

        et_search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged: ", "helloo");
                System.out.println(et_search.getText().toString());
                System.out.println("eee " + s);
                searchAdapter.getFilter().filter(et_search.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("saerch ", s + "");
            }
        });
    }
}

