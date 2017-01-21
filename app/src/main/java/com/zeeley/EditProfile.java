package com.zeeley;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {
    EditText name, age, phone, gender;
    RadioButton male, female;
    int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phoneno);
        age = (EditText) findViewById(R.id.age);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        Bundle b = getIntent().getBundleExtra(constants.BUNDLE);
        name.setText(b.getString(MyProfile.NAME));
        age.setText(b.getString(MyProfile.AGE));
        phone.setText(b.getString(MyProfile.PHNO));
        sex = b.getInt(MyProfile.GENDER, 18);
        if (sex == 1)
            male.setChecked(true);
        else if (sex == 0)
            female.setChecked(true);


    }

    public void done(View v) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString(MyProfile.NAME, name.getEditableText().toString());
        b.putString(MyProfile.AGE, age.getEditableText().toString());
        if (male.isEnabled())
            b.putString(MyProfile.GENDER, "Male");
        else if (female.isEnabled())
            b.putString(MyProfile.GENDER, "Female");
        b.putString(MyProfile.PHNO, phone.getEditableText().toString());
        i.putExtra(constants.BUNDLE, b);
        setResult(RESULT_OK, i);
        finish();

    }
}
