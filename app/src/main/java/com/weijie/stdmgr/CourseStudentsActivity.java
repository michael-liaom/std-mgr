package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseStudentsActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "code",
            "name",
            "class"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_class_text_tiew
    };

    private AuthUserData authUser;
    private CourseData courseData;
    private int courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_students);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();
        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        courseData = new CourseData();
        listMap = new ArrayList<>();

        Intent intent = getIntent();
        courseId = intent.getIntExtra(CourseData.COL_ID, 0);
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_course_students_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        /*
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StudentData studentData = arrayListStudent.get(position);
                Intent intent = new Intent(CourseStudentsActivity.this,
                        StudentsDetailActivity.class);
                intent.putExtra(CourseData.COL_ID, studentData.id);
                CourseStudentsActivity.this.startActivity(intent);
            }
        });
        */
    }

    private void requestData() {
        if(courseId > 0) {
            CourseDataUtils.getInstance()
                    .requestFetchCourseData(courseId, courseData,
                            dbHandler, CourseDataUtils.TAG_FETCH_COURSES_AS_TEACHER);
            showBusyProgress(true);
        }
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
            //applyBotton.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.GONE);
            //applyBotton.setEnabled(true);
        }
    }

    private void refreshData() {
        TextView courseCodeTextView
                = (TextView) findViewById(R.id.course_code_text_view);
        TextView courseNameTextView
                = (TextView) findViewById(R.id.course_name_text_view);
        courseCodeTextView.setText(courseData.code);
        courseNameTextView.setText(courseData.name);

        listMap.clear();
        if (courseData.arrayListStudent.size() > 0) {
            for (int idx = 0; idx < courseData.arrayListStudent.size(); idx++) {
                StudentData studentData = courseData.arrayListStudent.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], studentData.code);
                items.put(mapKey[2], studentData.name);
                items.put(mapKey[3], studentData.className);
                listMap.add(items);
            }
        }
        else {
            Map<String, Object> items = new HashMap<String, Object>();
            items.put(mapKey[0], "");
            items.put(mapKey[1], "");
            items.put(mapKey[2], "无数据");
            items.put(mapKey[3], "");
            items.put(mapKey[4], "");
            items.put(mapKey[5], "");
            listMap.add(items);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<CourseStudentsActivity> mActivity;

        DBHandler(CourseStudentsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CourseStudentsActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case CourseDataUtils.TAG_FETCH_COURSES_AS_STUDENT:
                        case CourseDataUtils.TAG_FETCH_COURSES_AS_TEACHER:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshData();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
