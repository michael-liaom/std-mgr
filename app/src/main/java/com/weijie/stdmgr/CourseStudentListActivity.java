package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weijie on 2018/5/22.
 */
public class CourseStudentListActivity extends AppCompatActivity {
    private int taskCount;
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
    private ArrayList<StudentData> arrayListStudent;
    private CourseData courseData;
    private int courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_student_list);

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
        arrayListStudent = new ArrayList<>();
        listMap = new ArrayList<>();

        Intent intent = getIntent();
        courseId = intent.getIntExtra(CourseData.COL_ID, 0);
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        courseData  = new CourseData();
        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_course_student_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        /*
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StudentData studentData = arrayListStudent.get(position);
                Intent intent = new Intent(CourseStudentListActivity.this,
                        StudentsDetailActivity.class);
                intent.putExtra(CourseData.COL_ID, studentData.id);
                CourseStudentListActivity.this.startActivity(intent);
            }
        });
        */
    }

    private void requestData() {
        if(courseId > 0) {
            CourseDataUtils.getInstance().requestFetchCourseData(courseId, courseData,
                            dbHandler, CourseDataUtils.TAG_FETCH_COURSE_DATA);
            StudentDataUtils.getInstance().requestFetchStudentListOfCourse(courseId, true,
                    arrayListStudent, dbHandler, StudentDataUtils.TAG_FETCH_LIST);
            taskCount = 2;
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

    private void refreshCourseInf() {
        TextView courseCodeTextView
                = (TextView) findViewById(R.id.course_code_text_view);
        TextView courseNameTextView
                = (TextView) findViewById(R.id.course_name_text_view);
        courseCodeTextView.setText(courseData.code);
        courseNameTextView.setText(courseData.name);
    }

    private  void refreshStudentList() {
        listMap.clear();
        if (arrayListStudent.size() > 0) {
            for (int idx = 0; idx < arrayListStudent.size(); idx++) {
                StudentData studentData = arrayListStudent.get(idx);
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
            listMap.add(items);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<CourseStudentListActivity> mActivity;

        DBHandler(CourseStudentListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CourseStudentListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case CourseDataUtils.TAG_FETCH_COURSE_DATA:
                            activity.taskCount--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshCourseInf();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(activity.taskCount>0);
                            break;
                        case StudentDataUtils.TAG_FETCH_LIST:
                            activity.taskCount--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshStudentList();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(activity.taskCount>0);
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
