package com.zeeley;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

public class Howitworks extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howitworks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("How it works");

        }

        sharedPreferences = getSharedPreferences(constants.MY_SHAREDPREFERENCES, MODE_PRIVATE);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        if (checkBox != null) {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lauch_activity();
                    if (((CheckBox) v).isChecked()) {
                        Log.d(constants.zeeley,"checkbx checkd");
                        editor = sharedPreferences.edit();
                        editor.putBoolean(constants.DntShowCB, true);
                        editor.apply();

                    }
                }
            });
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.circleindicator);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(mViewPager);
        Button continbuton = (Button) findViewById(R.id.continuebutton);
        continbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lauch_activity();
            }
        });

    }


    public void lauch_activity() {
        finish();
       if(sharedPreferences.getBoolean(constants.IS_LOGGEDIN,false)){
           Intent intent=new Intent(this,Interst_List.class);
           intent.putExtra(constants.SOURCE, constants.From_chooseInterest);
           startActivity(intent);

       }
        else{
           Intent intent = new Intent(this, my_login1.class);
           startActivity(intent);
       }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HITW_FRAG.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
