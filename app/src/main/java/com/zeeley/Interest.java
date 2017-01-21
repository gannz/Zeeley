package com.zeeley;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interest extends AppCompatActivity {
    ExpandableListView expandableListView;
    List<String> parentTitles;
    HashMap<String, List<String>> childTitles;
    HashMap<String, int[]> childImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Change Interest");
        }
        expandableListView = (ExpandableListView) findViewById(R.id.explist);
        setParentTitles();
        setChildTitles();
        setChildImages();
        int[] parentimages={R.drawable.food,R.drawable.outdoor,R.drawable.rockband,R.drawable.company,R.drawable.travel,R.drawable.dating,R.drawable.chating};
        ExpandableListAdapter adapter = new ExpandableListAdapter(this, parentTitles,parentimages, childTitles, childImages,expandableListView);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Intent i = new Intent(Interest.this, MainActivity.class);

                startActivity(i);
                return true;
            }
        });


    }

    public void setParentTitles() {
        parentTitles =new ArrayList<>();
        parentTitles.add("Order Food");
        parentTitles.add("Outdoor");
        parentTitles.add("Rock Band");
        parentTitles.add("Find Company for");
        parentTitles.add("Travelling");
        parentTitles.add("Dating");
        parentTitles.add("Chating");
    }

    public void setChildTitles() {
        List<String> food = new ArrayList<String>();
        food.add("Pizza");
        food.add("Burger");
        food.add("Roll");
        food.add("Biryani");
        //--------------------------------------
        List<String> outdoor = new ArrayList<String>();
        outdoor.add("Cricket");
        outdoor.add("FootBall");
        outdoor.add("BasketBall");
        outdoor.add("Badminton");
        outdoor.add("Tennis");
        outdoor.add("Gym");
        outdoor.add("Athelitics");
        //---------------------------------------
        List<String> music = new ArrayList<String>();
        music.add("Electric Guitarist");
        music.add("Base Guitarist");
        music.add("Singer");
        music.add("Drummer");
        //-----------------------------------------
        List<String> company = new ArrayList<String>();
        company.add("Studying");
        company.add("Movie");
        company.add("Restaurant");
        //------------------------------------------
        List<String> travelling = new ArrayList<String>();
        travelling.add("Cab Sharing");
        travelling.add("Auto Sharing");
        //----------------------------------------------
        childTitles = new HashMap<String, List<String>>();
        childTitles.put(parentTitles.get(0), food);
        childTitles.put(parentTitles.get(1), outdoor);
        childTitles.put(parentTitles.get(2), music);
        childTitles.put(parentTitles.get(3), company);
        childTitles.put(parentTitles.get(4), travelling);
        childTitles.put(parentTitles.get(5), null);
        childTitles.put(parentTitles.get(6), null);
    }

    public void setChildImages() {
        int[] food = {R.drawable.pizza, R.drawable.hamburger, R.drawable.wrap, R.drawable.porridge};
        int[] outdoor = {R.drawable.cricket, R.drawable.football, R.drawable.basketball, R.drawable.tennis, R.drawable.tennis, R.drawable.gym, R.drawable.running};
        int[] rockband = {R.drawable.elec_guitar, R.drawable.guitar, R.drawable.singer, R.drawable.drummer};
        int[] company = {R.drawable.study, R.drawable.movie, R.drawable.restaurant};
        int[] travel = {R.drawable.taxi, R.drawable.auto};
        childImages=new HashMap<String, int[]>();
        childImages.put(parentTitles.get(0),food);
        childImages.put(parentTitles.get(1),outdoor);
        childImages.put(parentTitles.get(2),rockband);
        childImages.put(parentTitles.get(3),company);
        childImages.put(parentTitles.get(4),travel);
        childImages.put(parentTitles.get(5),null);
        childImages.put(parentTitles.get(6),null);
    }
}
