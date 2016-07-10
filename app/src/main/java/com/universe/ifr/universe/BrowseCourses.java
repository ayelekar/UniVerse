package com.universe.ifr.universe;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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

public class BrowseCourses extends Fragment {

    private ArrayList<String> subjects;
    private ArrayList<Course> courses;

    Spinner subjectSpinner;
    EditText catalogField;
    ListView resultsView;
    Button searchButton;
    TextView resultLabel;
    LinearLayout resultLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_browse_courses, container, false);

        subjectSpinner = (Spinner) v.findViewById(R.id.subject_spinner);
        catalogField = (EditText) v.findViewById(R.id.course_code);
        searchButton = (Button) v.findViewById(R.id.search_button);
        resultsView = (ListView) v.findViewById(R.id.result_list);
        resultsView.setOnItemClickListener(new ResultsItemClickListener());
        resultLabel = (TextView) v.findViewById(R.id.result_label);
        resultLayout = (LinearLayout) v.findViewById(R.id.result_cont);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
                showview();
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

    //Call the AsyncTask to fecth all courses
    private void getAllCourses() {
        callRequest("http://www.rdlin.com/universe/courses/all");
    }

    //Parse the result from the HTTP call into a JSON object
    private void parseCourses(JSONArray result) {
        for (int i = 0; i < result.length(); i++) {
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

    //Redraw the list of courses
    public void updateView() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, subjects.toArray(new String[subjects.size()]));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(spinnerArrayAdapter);
        subjectSpinner.invalidate();
    }

    //Call the search and execute the HTTP request
    public void search() {
        String subject = subjectSpinner.getSelectedItem().toString();
        String code = catalogField.getText().toString();
        String[] params = {subject, code};
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        new SearchQuery().execute(params);
    }

    public void showview() {

        resultLabel.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.VISIBLE);
    }

    //Launch the course details activity when clicked
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

    //Parse the course result and create the list of course objects in another thread
    private class SearchQuery extends AsyncTask<String, Void, ArrayList<Course>> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Searching");
            this.dialog.show();
        }

        //Check to see if the courses match the query
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

        //Update the list to show the new results
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
                android.R.layout.simple_spinner_item, terms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogTermSpinner.setAdapter(adapter);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_add_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCourse(course, dialogTermSpinner.getSelectedItemPosition());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public int verifyCourse(Course course, int termNum) {
        String preReqUrl = "http://www.rdlin.com/universe/courses/info/meets_reqs/" + course.subject +
                "/" + course.catalog + "?previous_courses=";
        String antiReqUrl = "http://www.rdlin.com/universe/courses/info/meets_antireqs/" + course.subject +
                "/" + course.catalog + "?courses=";
        String coReqUrl = "http://www.rdlin.com/universe/courses/info/meets_reqs/" + course.subject +
                "/" + course.catalog + "?previous_courses=";
        for (int i=0; i<UserAccount.getInstance().coursesTaken.size(); i++) {
            for (int j=0; j<UserAccount.getInstance().coursesTaken.get(i).size(); j++) {
                Course c = UserAccount.getInstance().coursesTaken.get(i).get(j);
                if (i < termNum) {
                    preReqUrl += c.subject + c.catalog + ",";
                    coReqUrl += c.subject + c.catalog + ",";
                }
                if (i == termNum) {
                    coReqUrl += c.subject + c.catalog + ",";
                }
                antiReqUrl += c.subject + c.catalog + ",";
            }
        }

        if (preReqUrl.endsWith(",")) {
            preReqUrl = preReqUrl.substring(0, preReqUrl.length() - 1);
        }
        if (antiReqUrl.endsWith(",")) {
            antiReqUrl = antiReqUrl.substring(0, antiReqUrl.length() - 1);
        }
        if (coReqUrl.endsWith(",")) {
            coReqUrl = coReqUrl.substring(0, coReqUrl.length() - 1);
        }

        System.out.println(preReqUrl);
        System.out.println(antiReqUrl);
        System.out.println(coReqUrl);

        Object[] params = {preReqUrl,antiReqUrl, coReqUrl, course, termNum};

        new CourseCheck().execute(params);


        return 0;
    }

    private class CourseCheck extends AsyncTask<Object, Void, Object[]> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Adding course");
            this.dialog.show();
        }

        @Override
        protected Object[] doInBackground(Object... params) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            StringBuilder sb3 = new StringBuilder();
            boolean prereq = true;
            boolean coreq = true;
            boolean antireq = true;

            try {
                URL url = new URL((String)params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println(sb.toString());
                JSONObject ob = new JSONObject(sb.toString());
                System.out.println("OUTPUT: " + sb.toString());
                if (ob.getString("result").equals("True")) {
                    prereq = true;
                } else {
                    prereq = false;
                }


                URL url2 = new URL((String)params[1]);
                HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());

                line = "";
                BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
                while ((line = br2.readLine()) != null) {
                    sb2.append(line);
                }
                ob = new JSONObject(sb2.toString());
                if (ob.getString("result").equals("True")) {
                    antireq = true;
                } else {
                    antireq = false;
                }


                URL url3 = new URL((String)params[2]);
                HttpURLConnection urlConnection3 = (HttpURLConnection) url3.openConnection();
                InputStream in3 = new BufferedInputStream(urlConnection3.getInputStream());

                line = "";
                BufferedReader br3 = new BufferedReader(new InputStreamReader(in3));
                while ((line = br3.readLine()) != null) {
                    sb3.append(line);
                }
                ob = new JSONObject(sb3.toString());
                if (ob.getString("result").equals("True")) {
                    coreq = true;
                } else {
                    coreq = false;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            Integer valid = 0;
            if (!prereq) {
                valid = 1;
            } else if (antireq) {
                valid = 2;
            } else if (!coreq) {
                valid = 3;
            }
            System.out.println("VALID: " + valid);
            Object[] res = {params[3], params[4], valid};
            return res;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            final Course course = (Course)result[0];
            Integer success = (Integer)result[2];
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            for (int i=0; i<UserAccount.getInstance().coursesTaken.size(); i++) {
                for (int j=0; j<UserAccount.getInstance().coursesTaken.get(i).size(); j++) {
                    if (course.subject.equals(UserAccount.getInstance().coursesTaken.get(i).get(j).subject) &&
                            course.catalog.equals(UserAccount.getInstance().coursesTaken.get(i).get(j).catalog)) {
                        success = 4;
                    }
                }
            }

            int termNum = (Integer)result[1];
            if (success == 0) {
                UserAccount.getInstance().coursesTaken.get(termNum).add(course);
                UserAccount.getInstance().writeToFile(getActivity());
                dialog.dismiss();
            } else {
                AlertDialog.Builder addErrorDialog = new AlertDialog.Builder(getActivity());
                addErrorDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                addErrorDialog.setPositiveButton("Course Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), CourseDetails.class);
                        i.putExtra("COURSE_SUBJECT", course.subject);
                        i.putExtra("COURSE_CATALOG", course.catalog);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                System.out.println("SUCCESS: " + success);
                if (success == 1){
                    addErrorDialog.setMessage("Could not add " + course.subject+course.catalog +
                            " because you do not meet the pre-requisites. See course details for " +
                            "more information.");

                } else if (success == 2){
                    addErrorDialog.setMessage("Could not add " + course.subject+course.catalog +
                            " because you do not meet the anti-requisites. See course details for " +
                            "more information.");
                } else if (success == 3){
                    addErrorDialog.setMessage("Could not add " + course.subject+course.catalog +
                            " because you do not meet the co-requisites. See course details for " +
                            "more information.");
                } else {
                    addErrorDialog.setMessage("Could not add " + course.subject+course.catalog +
                            " because it has already been added.");
                }
                addErrorDialog.show();
            }

        }

    }
}
