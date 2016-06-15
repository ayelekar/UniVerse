package com.universe.ifr.universe;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CourseDetails extends AppCompatActivity {

    String subject;
    String catalog;
    String desc;

    TextView courseView;
    TextView titleView;
    TextView descView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseView = (TextView) findViewById(R.id.course_code);
        titleView = (TextView) findViewById(R.id.course_title);
        descView = (TextView) findViewById(R.id.course_desc);

        subject = getIntent().getStringExtra("COURSE_SUBJECT");
        catalog = getIntent().getStringExtra("COURSE_CATALOG");

        new HTTPGet().execute("http://rdlin.com/universe/courses/info/"+ subject + "/" + catalog);
    }


    private class HTTPGet extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(CourseDetails.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Fetching data");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urlString) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlString[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject queryResult = new JSONObject(result);
                courseView.setText(queryResult.getString("subject") +
                        queryResult.getString("catalog_number"));
                titleView.setText(queryResult.getString("title"));
                descView.setText(queryResult.getString("description"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
