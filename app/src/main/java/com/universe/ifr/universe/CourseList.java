package com.universe.ifr.universe;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CourseList extends Fragment {

    View rootView;
    LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_course_list, container, false);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

    private void refreshList() {
        LinearLayout frame = (LinearLayout) rootView.findViewById(R.id.content_frame);
        frame.removeAllViews();
        String[] terms = getResources().getStringArray(R.array.term_array);
        for (int i = 0; i < UserAccount.getInstance().termNumber; i++) {
            LinearLayout cont = new LinearLayout(getActivity());
            LinearLayout termContainer = (LinearLayout) mInflater.inflate(R.layout.term_list_container, frame, false);
            System.out.println(terms[i]);
            ((TextView) termContainer.getChildAt(0)).setText("Term: " + terms[i]);
            TermListView courses = (TermListView) ((LinearLayout) termContainer.getChildAt(1)).getChildAt(0);
            TextView emptyText = (TextView)((LinearLayout)termContainer.
                    findViewById(android.R.id.empty)).getChildAt(0);
            courses.setEmptyView(emptyText);
            courses.setAdapter(new CourseListAdapter(getActivity(),
                    UserAccount.getInstance().coursesTaken.get(i)));
            courses.setOnItemClickListener(new CourseListItemClickListener());

            cont.addView(termContainer);
            frame.addView(cont);
        }
    }

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
            addButton.setVisibility(View.INVISIBLE);

            course.setText(courseList.get(position).subject + courseList.get(position).catalog);
            title.setText(courseList.get(position).title);
            return (row);
        }
    }
}
