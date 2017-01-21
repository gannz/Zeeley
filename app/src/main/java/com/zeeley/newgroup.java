package com.zeeley;

import android.os.Bundle;
import android.app.Activity;

public class newgroup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgroup);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
