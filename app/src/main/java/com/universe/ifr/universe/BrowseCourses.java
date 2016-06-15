package com.universe.ifr.universe;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class BrowseCourses extends Fragment {

    private ArrayList<String> subjects;
    private ArrayList<Course> courses;

    Spinner subjectSpinner;
    EditText catalogField;
    ListView resultsView;
    Button searchButton;

    private LinearLayout drawerContainer;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_browse_courses, container, false);

        subjectSpinner = (Spinner) v.findViewById(R.id.subject_spinner);
        catalogField = (EditText) v.findViewById(R.id.course_code);
        searchButton = (Button) v.findViewById(R.id.search_button);
        resultsView = (ListView) v.findViewById(R.id.result_list);
        resultsView.setOnItemClickListener(new ResultsItemClickListener());
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subjects = new ArrayList<String>();
        subjects.add("ALL");
        courses = new ArrayList<Course>();

        getAllCourses();
    }

    private void getAllCourses() {
        callRequest("http://www.rdlin.com/universe/courses/all");
    }

    private void parseCourses(JSONArray result) {
        for (int i=0; i<result.length(); i++) {
            try {
                JSONObject jObject = result.getJSONObject(i);
                Course course = new Course(jObject.getInt("course_id"),
                        jObject.getString("subject"),
                        jObject.getString("catalog_number"),
                        jObject.getString("title"));
                if (course.valid) {
                    courses.add(course);
                    if (!subjects.contains(course.subject)) {
                        subjects.add(course.subject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateView() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, subjects.toArray(new String[subjects.size()]));
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        subjectSpinner.setAdapter(spinnerArrayAdapter);
        subjectSpinner.invalidate();
    }

    public void search() {
        String subject = subjectSpinner.getSelectedItem().toString();
        String code = catalogField.getText().toString();
        String[] params = {subject, code};
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        new SearchQuery().execute(params);
    }

    private class ResultsItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            Course selected = ((ResultListAdapter)parent.getAdapter()).courseList.get(position);
            Intent i = new Intent(parent.getContext(), CourseDetails.class);
            i.putExtra("COURSE_SUBJECT", selected.subject);
            i.putExtra("COURSE_CATALOG", selected.catalog);
            startActivity(i);

        }
    }

    private class SearchQuery extends AsyncTask<String, Void, ArrayList<Course>> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Searching");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Course> doInBackground(String... params) {
            ArrayList<Course> result = new ArrayList<Course>();

            for (int i=0; i<courses.size(); i++) {
                if (params[0].equals("ALL") || params[0].equals(courses.get(i).subject)) {
                    if (params[1].equals("") || params[1].equals(courses.get(i).catalog)) {
                        result.add(courses.get(i));
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Course> result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            ResultListAdapter resultAdapter = new ResultListAdapter(getActivity(), result);
            resultsView.setAdapter(resultAdapter);
            resultsView.invalidate();
        }

    }

    private void callRequest(String url) {
        new HTTPGet().execute(url);
    }

    private class HTTPGet extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Fetching data");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... urlString) {
            System.out.println("START");
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlString[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    System.out.println("STREAM OUTPUT: " + line);
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
                JSONArray queryResult = (JSONArray) new JSONTokener(result).nextValue();
                parseCourses(queryResult);
                updateView();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ResultListAdapter extends BaseAdapter {

        ArrayList<Course> courseList;
        Context c;

        public ResultListAdapter(Context context, ArrayList<Course> courseList) {
            c = context;
            this.courseList = courseList;
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
            return courseList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.query_list_item, parent, false);
            TextView course;
            TextView title;
            ImageView addButton;
            course = (TextView) row.findViewById(R.id.course);
            title = (TextView) row.findViewById(R.id.title);
            addButton = (ImageView) row.findViewById(R.id.add_button);
            addButton.setTag(position);
            course.setText(courseList.get(position).subject + courseList.get(position).catalog);
            title.setText(courseList.get(position).title);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCourse(courseList.get((Integer) v.getTag()));
                }
            });

            return (row);
        }
    }

    public void addCourse(final Course course) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_course_dialog);
        dialog.setTitle("Add Course");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.dialog_course);
        text.setText(course.subject + course.catalog);
        final Spinner dialogTermSpinner = (Spinner) dialog.findViewById(R.id.dialog_term_spinner);

        String[] terms = getResources().getStringArray(R.array.term_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                Arrays.copyOfRange(terms, 0, UserAccount.getInstance().termNumber));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogTermSpinner.setAdapter(adapter);



        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_add_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount.getInstance().coursesTaken.get(
                        dialogTermSpinner.getSelectedItemPosition()).add(course);
                UserAccount.getInstance().writeToFile(getActivity());
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
