package com.example.guest999.firebasenotification.activities;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Search extends AppCompatActivity {
    public static ArrayList<HashMap<String, String>> User_info_search;
    public EditText et_search;
    int textlength = 0;
    //listview for branch name
    private RecyclerView rv;
    // converting arraylist to string array intializing size
    private String[] full_name = new String[UserList.hello.size()];
    // array list
    private ArrayList<String> array_sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        User_info_search = UserList.hello;
        Log.e("Movie", String.valueOf(User_info_search));
        // converting arraylist to string array
        for (int i = 0; i < UserList.hello.size(); i++) {
            try {
                full_name[i] = UserList.hello.get(i).get(Config.KEY_USERNAME);
                Log.e("Userlist", full_name[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        //converting string array to Array List
        array_sort = new ArrayList<>(Arrays.asList(full_name));
        // seting custom adapter
        rv.setAdapter(new MyAdapter(array_sort));
    }

    private void LoadUILisners() {

        et_search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Abstract Method of TextWatcher Interface.
            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
                // Abstract Method of TextWatcher Interface.
            }

            //called when user entering text fo search match case
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                //getting text length
                textlength = et_search.getText().length();
                //clear array list
                array_sort.clear();
                //for loop for cnverting text into lower case and adding into array list of array_sort
                for (int i = 0; i < full_name.length; i++) {
                    if (textlength <= full_name[i].length()) {
                        if (full_name[i].toLowerCase().contains(
                                et_search.getText().toString().toLowerCase().trim())) {
                            array_sort.add(full_name[i]);
                        }
                    }
                }
                // after searched text will be saved in array_sort array list
                rv.setAdapter(new MyAdapter(array_sort));
            }
        });

    }

    // adapter class for Fragment home
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<String> mDataset;
        private int lastposition = -1;

        MyAdapter(ArrayList<String> Employee_info) {
            mDataset = Employee_info;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            final String name = mDataset.get(position);
            final String phone = mDataset.get(position);
            Log.e("onBindViewHolder: ", phone);

            holder.tv_name.setText(name);
            holder.tv_phone.setText(phone);


            /*Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    (position > lastposition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastposition = position;*/

         /*   holder.lr_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int PARENT_POSITION;
                    PARENT_POSITION = position;
                    Log.d("onItemClick: ", PARENT_POSITION + "");
                    Toast.makeText(getApplicationContext(), "Parent position is " + PARENT_POSITION, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), EmployeeFullDetails.class);
                    i.putExtra("parent_position", PARENT_POSITION);
                    startActivity(i);

                }
            });*/

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView tv_name, tv_phone;

            @SuppressLint("WrongViewCast")
            ViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.m_name);
                tv_phone = (TextView) itemView.findViewById(R.id.phone_number);
            }
        }


    }
}
