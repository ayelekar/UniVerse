package com.universe.ifr.universe;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Date;

public class CourseDetails extends AppCompatActivity {

    String subject;
    String catalog;
    String desc;

    TextView courseView;
    TextView titleView;
    TextView descView;
    TextView courseRating;

    TextView prereqsView;
    TextView coreqsView;
    TextView antireqsView;

    TermListView commentListView;

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
        courseRating = (TextView) findViewById(R.id.course_rating);

        prereqsView = (TextView) findViewById(R.id.pre_reqs);
        antireqsView = (TextView) findViewById(R.id.anti_reqs);
        coreqsView = (TextView) findViewById(R.id.co_reqs);

        commentListView = (TermListView) findViewById(R.id.comment_list);
       TextView emptyText = (TextView)((LinearLayout) findViewById(android.R.id.empty)).getChildAt(0);
        commentListView.setEmptyView(emptyText);

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
            StringBuilder sb5 = new StringBuilder();
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


                //get list of comments for this course
                URL url5 = new URL("http://rdlin.com/universe/courses/comments/get/"+ urlString[0] + "/" + urlString[1]);
                HttpURLConnection urlConnection5 = (HttpURLConnection) url5.openConnection();
                InputStream in5 = new BufferedInputStream(urlConnection5.getInputStream());

                line = "";
                BufferedReader br5 = new BufferedReader(new InputStreamReader(in5));
                while ((line = br5.readLine()) != null) {
                    sb5.append(line);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            String[] res = {sb.toString(), sb2.toString(), sb3.toString(), sb4.toString(), sb5.toString()};
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

                //set the list of comments
                JSONArray queryResultComm = new JSONArray(result[4]);
                ArrayList<Comment> commentList = new ArrayList<Comment>();
                double rating = 0;
                for (int i=0; i<queryResultComm.length(); i++) {
                    Comment comm = new Comment (queryResultComm.getJSONObject(i).getString("user"),
                            queryResultComm.getJSONObject(i).getInt("rating"),
                            queryResultComm.getJSONObject(i).getString("date"),
                            queryResultComm.getJSONObject(i).getString("comment"));
                    rating += comm.rating;
                    commentList.add(comm);
                }

                CommentListAdapter commentListAdapter = new CommentListAdapter(CourseDetails.this, commentList);
                commentListView.setAdapter(commentListAdapter);

                if (commentList.size() > 0) {
                    courseRating.setText(commentList.size() + " user(s) rated this course a " + (rating/commentList.size()) + "/5.0");
                } else {
                    courseRating.setText("No users have rated this course yet.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //Get the course details using the HTTP request
    private class HTTPCommPost extends AsyncTask<Comment, Void, Void> {

        //Show the progress dialog while the thread is running
        private ProgressDialog dialog = new ProgressDialog(CourseDetails.this);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Posting comment");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Comment... comm) {

            try {
                //Post comment
                URL url = new URL("http://rdlin.com/universe/courses/comments/add/"+ subject + "/" +
                        catalog + "?comment=" + comm[0].feedback.replace(" ", "%20") + "&rating=" +
                        comm[0].rating + "&user=" + comm[0].username);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }


            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void comment(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_feedback_dialog);
        dialog.setTitle("Feedback");


        final SeekBar ratingBar = (SeekBar) dialog.findViewById(R.id.course_rating);
        final EditText feedback = (EditText) dialog.findViewById(R.id.comment_box);

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.comm_course_name);
        text.setText(subject + catalog);


        Button submitButton = (Button) dialog.findViewById(R.id.submit_comment);
        // if button is clicked, close the custom dialog
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment(UserAccount.getInstance().username, ratingBar.getProgress()+1,
                        "", feedback.getText().toString());

                new HTTPCommPost().execute(comment);
                new HTTPGet().execute(subject, catalog);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Custom adapter to show the list of comments
    private class CommentListAdapter extends BaseAdapter {

        ArrayList<Comment> commentList;
        Context c;

        public CommentListAdapter(Context context, ArrayList<Comment> commentList) {
            c = context;
            this.commentList = commentList;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.comment_list_item, parent, false);
            TextView username;
            TextView rating;
            TextView date;
            TextView feedback;

            username = (TextView) row.findViewById(R.id.comm_user);
            rating = (TextView) row.findViewById(R.id.comm_rating);
            date = (TextView) row.findViewById(R.id.comm_date);
            feedback = (TextView) row.findViewById(R.id.comm_feedback);

            username.setText(commentList.get(position).username);
            rating.setText("Rating: " + commentList.get(position).rating + "/5");
            date.setText("Posted: " + commentList.get(position).date);
            feedback.setText(commentList.get(position).feedback);

            return (row);
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
