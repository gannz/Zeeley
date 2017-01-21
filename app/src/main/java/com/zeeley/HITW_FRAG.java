package com.zeeley;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

/**
 * Created by gannu on 09-07-2016.
 */
public class HITW_FRAG extends android.support.v4.app.Fragment {

    private static   final String ARG_SECTION_NUMBER = "section_number";

    public HITW_FRAG() {
    }


    public static HITW_FRAG newInstance(int sectionNumber) {
        HITW_FRAG fragment = new HITW_FRAG();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_howitworks, container, false);


        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        int i=getArguments().getInt(ARG_SECTION_NUMBER);
        switch (i){
            case 1:
                imageView.setImageResource(R.drawable.userimg);
                break;
            case 2:
                imageView.setImageResource(R.drawable.bg);
                break;
            case 3:
                imageView.setImageResource(R.drawable.arrowmain);
                break;
        }
        return rootView;
    }
}


