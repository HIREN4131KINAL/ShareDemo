package com.example.guest999.firebasenotification.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guest999.firebasenotification.R;

/**
 * Created by Harshad on 21-10-2016 at 11:08 AM.
 */
public class User_Setting extends AppCompatActivity implements View.OnClickListener
{
    ImageView profile_image;
    TextView change_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersetting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image=(ImageView)findViewById(R.id.profile_image);
        change_password=(TextView)findViewById(R.id.change_password);

        change_password.setOnClickListener(this);
        profile_image.setOnClickListener(this);
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

    @Override
    public void onClick(View v)
    {
        if(v==change_password)
        {
            Intent change=new Intent(User_Setting.this,Change_pass.class);
            startActivity(change);
        }
        if(v==profile_image)
        {

        }
    }
}
