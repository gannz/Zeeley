package com.zeeley;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class profile_photo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(chatactivity.ACTIONBAR_TITLE));
        ImageView imageView= (ImageView) findViewById(R.id.pro_photo);
        imageView.setImageResource(getIntent().getIntExtra(chatactivity.PROFILE_PIC,R.drawable.default_profpic));
    }

}
