package com.universe.ifr.universe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;

public class AccountSettings extends AppCompatActivity {

    Spinner termSpinner;
    Spinner programSpinner;

    EditText username;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //Fetches the action bar and shows the title
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //Allows backwards navigation from the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //loading the views into variables
        termSpinner = (Spinner) findViewById(R.id.term_spinner);
        programSpinner = (Spinner) findViewById(R.id.program_spinner);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        username.setText(UserAccount.getInstance().username);
        email.setText(UserAccount.getInstance().email);
        password.setText(UserAccount.getInstance().password);

        setSpinners();
    }

    //Populate the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_actionbar, menu);
        return true;
    }

    //Listener when the buttons are pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_button:
                // Save settings
                if(!UserAccount.getInstance().username.equals(username.getText().toString())) {
                    File f = new File(username.getText().toString() + "_data");
                    f.delete();
                }
                //Get the data from the input and apply to user account
                UserAccount.getInstance().username = username.getText().toString();
                UserAccount.getInstance().email = email.getText().toString();
                UserAccount.getInstance().password = password.getText().toString();
                UserAccount.getInstance().setTerm(termSpinner.getSelectedItem().toString(),
                        termSpinner.getSelectedItemPosition() + 1);
                UserAccount.getInstance().program = programSpinner.getSelectedItem().toString();
                UserAccount.getInstance().programNumber = programSpinner.getSelectedItemPosition() + 1;

                //Write the data to the file
                UserAccount.getInstance().writeToFile(this);

                //Start the main activity
                Intent i = new Intent(this, BaseActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //Log out clears the user account
    public void logout(View v) {
        UserAccount.getInstance().reset();
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //Set the content of the spinners
    private void setSpinners() {
        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(this,
                R.array.term_array, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this,
                R.array.program_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        termSpinner.setAdapter(termAdapter);
        termSpinner.setSelection(UserAccount.getInstance().termNumber-1);
        programSpinner.setAdapter(programAdapter);
        programSpinner.setSelection(UserAccount.getInstance().programNumber-1);
    }
}
