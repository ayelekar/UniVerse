package com.universe.ifr.universe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseList extends Fragment {

    View rootView;
    LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        //inflate the layout
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_course_list, container, false);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //When the activity continues, refresh the list (when the user comes back to the page)
    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

    private void refreshList() {
        LinearLayout frame = (LinearLayout) rootView.findViewById(R.id.content_frame);
        //clear the container
        frame.removeAllViews();
        String[] terms = getResources().getStringArray(R.array.term_array);
        //insert the courses back into the container (iterate for 10 terms)
        for (int i = 0; i < 10; i++) {
            LinearLayout cont = new LinearLayout(getActivity());
            LinearLayout termContainer = (LinearLayout) mInflater.inflate(R.layout.term_list_container, frame, false);
            //Put the text 'current term' beside the current term
            if (i == UserAccount.getInstance().termNumber-1) {
                ((TextView) termContainer.getChildAt(0)).setText("Term: " + terms[i] + " (current term)");
            } else {
                ((TextView) termContainer.getChildAt(0)).setText("Term: " + terms[i]);
            }
            TermListView courses = (TermListView) ((LinearLayout)((LinearLayout) termContainer.getChildAt(1)).getChildAt(0)).getChildAt(0);
            TextView emptyText = (TextView)((LinearLayout)termContainer.
                    findViewById(android.R.id.empty)).getChildAt(0);
            courses.setEmptyView(emptyText);
            //indicate the ith term with the tag
            courses.setTag(i);
            courses.setAdapter(new CourseListAdapter(getActivity(),
                    UserAccount.getInstance().coursesTaken.get(i)));
            courses.setOnItemClickListener(new CourseListItemClickListener());

            //Set the bg colour of the term container based on if its before or after the current term
            if (i < UserAccount.getInstance().termNumber-1) {
                ((LinearLayout) termContainer.getChildAt(1)).getChildAt(0).setBackgroundColor(Color.argb(25,0,255,0));
            } else if (i == UserAccount.getInstance().termNumber-1) {
                ((LinearLayout) termContainer.getChildAt(1)).getChildAt(0).setBackgroundColor(Color.argb(25,0,0,255));
            }

            cont.addView(termContainer);

            frame.addView(cont);
        }
    }

    //launch the course details activity when the user clicks on a course
    private class CourseListItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            Course selected = ((CourseListAdapter)parent.getAdapter()).courseList.get(position);
            Intent i = new Intent(parent.getContext(), CourseDetails.class);
            i.putExtra("COURSE_SUBJECT", selected.subject);
            i.putExtra("COURSE_CATALOG", selected.catalog);
            startActivity(i);

        }
    }

    //Custom adapter to show the list of courses in the list views
    private class CourseListAdapter extends BaseAdapter {

        ArrayList<Course> courseList;
        Context c;

        public CourseListAdapter(Context context, ArrayList<Course> courseList) {
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
            addButton.setImageResource(R.drawable.ic_remove);
            addButton.setTag(position);

            //if the button is clicked, remove the course from the list
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCourse(courseList.get((Integer) v.getTag()));
                }
            });

            course.setText(courseList.get(position).subject + courseList.get(position).catalog);
            title.setText(courseList.get(position).title);

            return (row);
        }
    }

    //Shows a dialog box to confirm removing the course
    public void removeCourse(final Course course) {
        AlertDialog.Builder removeCourseDialog = new AlertDialog.Builder(getActivity());
        removeCourseDialog.setMessage("Are you sure you want to remove " + course.subject +
                course.catalog + " from your courses?");
        //if canceled, do nothing
        removeCourseDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //if confirmed, remove course from the user account and refresh
        removeCourseDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i=0; i<UserAccount.getInstance().coursesTaken.size(); i++) {
                    for (int j=0; j<UserAccount.getInstance().coursesTaken.get(i).size(); j++) {
                       //find the course and remove it
                        if (UserAccount.getInstance().coursesTaken.get(i).get(j).subject ==
                                course.subject && UserAccount.getInstance().coursesTaken.get(i).get(j).catalog ==
                                course.catalog) {
                            UserAccount.getInstance().coursesTaken.get(i).remove(j);

                        }
                    }
                }
                //write the data to the file and close the dialog
                UserAccount.getInstance().writeToFile(getActivity());
                Activity thisActivity = getActivity();
                thisActivity.finish();
                Intent i = thisActivity.getIntent();
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                dialog.dismiss();
            }
        });

        removeCourseDialog.show();
    }
}
