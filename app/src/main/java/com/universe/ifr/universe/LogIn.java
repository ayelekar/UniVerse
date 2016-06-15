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

        // Restore preferences
        settings = getSharedPreferences(PREFS_FILE, 0);
        boolean rememberMe = settings.getBoolean("remember_me", false);
        System.out.println("REMEMBER: " + rememberMe);
        remember.setChecked(rememberMe);
        if(rememberMe) {
            username.setText(settings.getString("username", ""));
            password.setText(settings.getString("password", ""));
        }

        UserAccount.getInstance().reset();

        error = new TextView(this);
        error.setText("Incorrect username or password. Try again.");
        error.setTextSize(10);
        error.setGravity(Gravity.CENTER_HORIZONTAL);
        error.setTextColor(Color.RED);
    }

    @Override
    protected void onResume() {
        UserAccount.getInstance().reset();
        super.onResume();
    }

    public void login(View view) {
        String user = "";
        String pass = "";

        user = username.getText().toString();
        pass = password.getText().toString();

        SharedPreferences.Editor editor = settings.edit();
        if (remember.isChecked()) {
            System.out.println("REMEMBER");

            editor.putBoolean("remember_me", true);
            editor.putString("username", user);
            editor.putString("password", pass);
            editor.commit();
        } else {
            System.out.println("FORGET");
            editor.putBoolean("remember_me", false);
            editor.remove("username");
            editor.remove("password");
            editor.commit();
        }

        UserAccount.getInstance().loadData(this, user);

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

    public void signup(View view) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }
}
