package com.zeeley;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class my_login2 extends AppCompatActivity implements View.OnClickListener {
    private EditText name, pass, email, mob, age;
    private Button fbsignup, signup;
    TextView login;
    RadioButton male, female;
    private static final String urlString = "http://zeeley.com/android_registration";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String MALE = "male";
    private static final String INTEREST = "interest";
    private TextInputLayout nameLayout, emailLayout, passwordLayout, phoneLayout, ageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylogin2);

        nameLayout = (TextInputLayout) findViewById(R.id.name_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.email_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
        age = (EditText) findViewById(R.id.age);
        age.addTextChangedListener(new MyTextWatcher(age));
        ageLayout = (TextInputLayout) findViewById(R.id.age_layout);
        phoneLayout = (TextInputLayout) findViewById(R.id.phoneNo_layout);
        name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(new MyTextWatcher(name));
        pass = (EditText) findViewById(R.id.password);
        pass.addTextChangedListener(new MyTextWatcher(pass));
        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(new MyTextWatcher(email));
        mob = (EditText) findViewById(R.id.mob);
        mob.addTextChangedListener(new MyTextWatcher(mob));
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(this);
        login = (TextView) findViewById(R.id.login);
        fbsignup = (Button) findViewById(R.id.fbsignup);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        login.setOnClickListener(this);
        fbsignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fbsignup:
                return;
            case R.id.login:
                Intent i = new Intent(this, my_login1.class);
                startActivity(i);
                return;
            case R.id.signup:
                submitForm();
                return;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.name:
                    validateName();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
                case R.id.age:
                    break;
                case R.id.mob:
                    break;
            }
        }
    }

    private void submitForm() {
        if (!validateName()) {
            Toast.makeText(my_login2.this, "invalid name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateEmail()) {
            Toast.makeText(my_login2.this, "invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validatePassword()) {
            Toast.makeText(my_login2.this, "in valid password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validateAge()) {
            Toast.makeText(my_login2.this, "invalid age", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validatePhoneNo()) {
            Toast.makeText(my_login2.this, "invalid phno", Toast.LENGTH_SHORT).show();
            return;
        }
        // send details to server
        //signUp(email.getText(),pass.getText(),);
        Toast.makeText(my_login2.this, "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            nameLayout.setError("Enter your name");
            requestFocus(name);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String text = email.getText().toString().trim();

        if (text.isEmpty() || !isValidEmail(text)) {
            emailLayout.setError("Enter valid email address");
            requestFocus(email);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (pass.getText().toString().trim().isEmpty()) {
            passwordLayout.setError("Enter the password");
            requestFocus(pass);
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
            return true;
        }


    }

    private boolean validateAge() {
        if (Integer.parseInt(age.getText().toString()) > 99) {
            ageLayout.setError("Enter valid age");
            requestFocus(age);
            return false;
        } else {
            ageLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePhoneNo() {
        String text = email.getText().toString().trim();
        if (text.isEmpty() || !isValidatePhoneNo(text)) {
            phoneLayout.setError("Enter your PhoneNo");
            requestFocus(mob);
            return false;

        } else {
            phoneLayout.setErrorEnabled(false);
            return true;
        }
    }

    private static boolean isValidatePhoneNo(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void signUp(String email, String password, String first_name, String last_name, boolean isMale) {
        String male = isMale ? "male" : "female";
        String defaultInterest = "Chat";
        new signUpTask().execute(email, password, first_name, last_name, male, defaultInterest);
    }
    private class signUpTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String urlParams = EMAIL + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                        PASSWORD + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                        FIRST_NAME + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                        LAST_NAME + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                        MALE + "=" + URLEncoder.encode(params[4], "UTF-8") + "&" +
                        INTEREST + "=" + URLEncoder.encode(params[5], "UTF-8");

                URL url = new URL(urlString + "?" + urlParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(true);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String s = r.readLine();
                    r.close();
                    return s;
                }else
                    return connection.getResponseMessage();


            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        }
    }

    public void registerUser(String email, String password, String first_name, boolean isMale, String interest, String age){
        new AsyncTask<String,Void,Void>(){
            public Void doInBackground(String... params){
                try{
                    URL url = new URL("http://www.zeeley.com/android_registration/"
                            + "?email=" + params[0]
                            + "&password=" + params[1]
                            + "&first_name=" + params[2]
                            + "&male=" + params[3]
                            + "&interest=" + params[4]
                            + "&age=" + params[5]);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    conn.connect();

                }catch(Exception e){

                }
                return null;
            }
        }.execute(email, password, first_name, (isMale?"1":"0"), interest, age);
    }

}
