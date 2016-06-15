package com.universe.ifr.universe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.FileOutputStream;

public class AccountSettings extends AppCompatActivity {

    Spinner termSpinner;
    Spinner facultySpinner;
    Spinner programSpinner;

    EditText username;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        termSpinner = (Spinner) findViewById(R.id.term_spinner);
        facultySpinner = (Spinner) findViewById(R.id.faculty_spinner);
        programSpinner = (Spinner) findViewById(R.id.program_spinner);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        username.setText(UserAccount.getInstance().username);
        email.setText(UserAccount.getInstance().email);
        password.setText(UserAccount.getInstance().password);

        setTermSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_button:
                // Save settings

                UserAccount.getInstance().username = username.getText().toString();
                UserAccount.getInstance().email = email.getText().toString();
                UserAccount.getInstance().password = password.getText().toString();
                UserAccount.getInstance().setTerm(termSpinner.getSelectedItem().toString(),
                        termSpinner.getSelectedItemPosition() + 1);
//                UserAccount.getInstance().faculty = facultySpinner.getSelectedItem().toString();
//                UserAccount.getInstance().program = programSpinner.getSelectedItem().toString();

                UserAccount.getInstance().writeToFile(this);

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

    private void setTermSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.term_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        termSpinner.setAdapter(adapter);
        termSpinner.setSelection(UserAccount.getInstance().termNumber-1);
    }
}
