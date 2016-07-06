package com.universe.ifr.universe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogIn extends Activity {

    private static String PREFS_FILE = "prefs_file";

    SharedPreferences settings;

    LinearLayout loginForm;
    EditText username;
    EditText password;
    CheckBox remember;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginForm = (LinearLayout) findViewById(R.id.login_form);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember_me);

        // Restore preferences from the prefs file
        settings = getSharedPreferences(PREFS_FILE, 0);
        boolean rememberMe = settings.getBoolean("remember_me", false);
        remember.setChecked(rememberMe);
        //remember me boolean checks to see if the checkbox is checked
        if(rememberMe) {
            username.setText(settings.getString("username", ""));
            password.setText(settings.getString("password", ""));
        }

        UserAccount.getInstance().reset();

        //if the user does not exist or password is wrong
        error = new TextView(this);
        error.setText("Incorrect username or password. Try again.");
        error.setTextSize(10);
        error.setGravity(Gravity.CENTER_HORIZONTAL);
        error.setTextColor(Color.RED);
    }

    //reset the data in the user account if logged out
    @Override
    protected void onResume() {
        UserAccount.getInstance().reset();
        super.onResume();
    }

    //Directly accessed from the layout onlick function
    public void login(View view) {
        String user = "";
        String pass = "";

        user = username.getText().toString();
        pass = password.getText().toString();

        //edit the preferences file
        SharedPreferences.Editor editor = settings.edit();
        if (remember.isChecked()) {
            System.out.println("REMEMBER");
            //put in the username and password if the remember checkbox is checked
            editor.putBoolean("remember_me", true);
            editor.putString("username", user);
            editor.putString("password", pass);
            editor.commit();
        } else {
            //else remove the fields
            System.out.println("FORGET");
            editor.putBoolean("remember_me", false);
            editor.remove("username");
            editor.remove("password");
            editor.commit();
        }

        //load the user data and check the username and password against it
        UserAccount.getInstance().loadData(this, user);

        //Check if the account is valid, if not, fail login
        if (UserAccount.getInstance().validAccount) {
            if (pass.equals(UserAccount.getInstance().password)) {
                loginForm.removeView(error);
                Intent i = new Intent(this, BaseActivity.class);
                startActivity(i);
            } else {
                if (error.getParent() == null) {
                    loginForm.addView(error);
                    UserAccount.getInstance().reset();
                }
            }
        } else {
            if (error.getParent() == null) {
                loginForm.addView(error);
                UserAccount.getInstance().reset();
            }
        }

    }
    //Go to the signup page
    public void signup(View view) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }
}
