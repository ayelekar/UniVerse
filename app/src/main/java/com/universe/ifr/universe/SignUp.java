package com.universe.ifr.universe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class SignUp extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
    }

    public void submit(View view) {
        UserAccount.getInstance().username = username.getText().toString();
        UserAccount.getInstance().email = email.getText().toString();
        UserAccount.getInstance().password = password.getText().toString();

        Intent i = new Intent(this, AccountSettings.class);
        startActivity(i);
        finish();
    }

}
