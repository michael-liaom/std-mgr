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
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseListActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private ArrayList <Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "code",
            "name",
            "teacher",
            "term",
            "credit"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_teacher_text_tiew,
            R.id.item_term_text_view,
            R.id.item_credit_text_view
    };

    private ArrayList <CourseData> arrayListCourseData;
    private AuthUserData authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

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
        arrayListCourseData = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_course_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <arrayListCourseData.size()) {
                    CourseData courseData = arrayListCourseData.get(position);
                    Intent intent = new Intent(CourseListActivity.this,
                            CourseDetailActivity.class);
                    intent.putExtra(CourseData.COL_ID, courseData.id);
                    CourseListActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void requestData() {
        arrayListCourseData.clear();
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            CourseDataUtils.getInstance()
                    .requestFetchCourseListOfStudent(authUser.studend_id, arrayListCourseData,
                            dbHandler, CourseDataUtils.TAG_FETCH_COURSE_LIST);
        }
        else {
            CourseDataUtils.getInstance()
                    .requestFetchCourseListOfTeacher(authUser.teacher_id, arrayListCourseData,
                            dbHandler, CourseDataUtils.TAG_FETCH_COURSE_LIST);
        }
        showBusyProgress(true);
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
        listMap.clear();
        if (arrayListCourseData.size() > 0) {
            for (int idx = 0; idx < arrayListCourseData.size(); idx++) {
                CourseData courseData = arrayListCourseData.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], courseData.code);
                items.put(mapKey[2], courseData.name);
                items.put(mapKey[3], courseData.teacherName);
                items.put(mapKey[4], Integer.toString(courseData.term));
                items.put(mapKey[5], Integer.toString(courseData.credit));
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
        private final WeakReference<CourseListActivity> mActivity;

        DBHandler(CourseListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CourseListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case CourseDataUtils.TAG_FETCH_COURSE_LIST:
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
