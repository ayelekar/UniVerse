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

    TextView prereqsView;
    TextView coreqsView;
    TextView antireqsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        //Set the toolbar and UI components
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseView = (TextView) findViewById(R.id.course_code);
        titleView = (TextView) findViewById(R.id.course_title);
        descView = (TextView) findViewById(R.id.course_desc);

        prereqsView = (TextView) findViewById(R.id.pre_reqs);
        antireqsView = (TextView) findViewById(R.id.anti_reqs);
        coreqsView = (TextView) findViewById(R.id.co_reqs);

        //Get the course from the intent data
        subject = getIntent().getStringExtra("COURSE_SUBJECT");
        catalog = getIntent().getStringExtra("COURSE_CATALOG");
        System.out.println("QUERY: " + "http://rdlin.com/universe/courses/info/"+ subject + "/" + catalog);

        //Get the details of the course using HTTP request
        new HTTPGet().execute(subject, catalog);
    }

    //Get the course details using the HTTP request
    private class HTTPGet extends AsyncTask<String, Void, String[]> {

        //Show the progress dialog while the thread is running
        private ProgressDialog dialog = new ProgressDialog(CourseDetails.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Fetching data");
            this.dialog.show();
        }

        @Override
        protected String[] doInBackground(String... urlString) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            StringBuilder sb4 = new StringBuilder();
            //Fetch the data
            try {
                //Get course basic info
                URL url = new URL("http://rdlin.com/universe/courses/info/"+ urlString[0] + "/" + urlString[1]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                //Get info about the prereqs
                URL url2 = new URL("http://rdlin.com/universe/courses/info/prereqs/"+ urlString[0] + "/" + urlString[1]);
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());

                line = "";
                BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
                while ((line = br2.readLine()) != null) {
                    sb2.append(line);
                }

                //get info about the antireqs
                URL url3 = new URL("http://rdlin.com/universe/courses/info/antireqs/"+ urlString[0] + "/" + urlString[1]);
                HttpURLConnection urlConnection3 = (HttpURLConnection) url3.openConnection();
                InputStream in3 = new BufferedInputStream(urlConnection3.getInputStream());

                line = "";
                BufferedReader br3 = new BufferedReader(new InputStreamReader(in3));
                while ((line = br3.readLine()) != null) {
                    sb3.append(line);
                }

                //get info about the coreqs
                URL url4 = new URL("http://rdlin.com/universe/courses/info/coreqs/"+ urlString[0] + "/" + urlString[1]);
                HttpURLConnection urlConnection4 = (HttpURLConnection) url4.openConnection();
                InputStream in4 = new BufferedInputStream(urlConnection4.getInputStream());

                line = "";
                BufferedReader br4 = new BufferedReader(new InputStreamReader(in4));
                while ((line = br4.readLine()) != null) {
                    sb4.append(line);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            String[] res = {sb.toString(), sb2.toString(), sb3.toString(), sb4.toString()};
            return res;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                //parse the json data and set the result to the views
                JSONArray queryResultArray = new JSONArray(result[0]);
                JSONObject queryResult = queryResultArray.getJSONObject(0);
                courseView.setText(queryResult.getString("subject") +
                        queryResult.getString("catalog_number"));
                titleView.setText(queryResult.getString("title"));
                descView.setText(queryResult.getString("description"));

                //Set the list og prereqs
                JSONObject queryResultPre = new JSONObject(result[1]);
                if (queryResultPre.getString("prereqs_desc").equals("")) {
                    prereqsView.setText("None");
                } else {
                    prereqsView.setText(queryResultPre.getString("prereqs_desc"));
                }

                //set the list of antireqs
                JSONObject queryResultAnti = new JSONObject(result[2]);
                if (queryResultAnti.getString("antireqs").equals("")) {
                    antireqsView.setText("None");
                } else {
                    antireqsView.setText(queryResultAnti.getString("antireqs"));
                }

                //set the list of coreqs
                JSONObject queryResultCo = new JSONObject(result[3]);
                if (queryResultCo.getString("coreqs").equals("")) {
                    coreqsView.setText("None");
                } else {
                    coreqsView.setText(queryResultCo.getString("corecs"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //Close the activity if the home button is clicked
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
