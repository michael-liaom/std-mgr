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

/**
 * Created by weijie on 2018/5/21.
 */
public class TeacherListActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "name",
            "name",
            "section"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_section_text_tiew
    };

    private ArrayList <TeacherData> arrayListTeacher;
    private AuthUserData authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

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
        arrayListTeacher = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_teacher_list_item, mapKey, mapResurceId);

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListTeacher.size()) {
                    TeacherData teacherData = arrayListTeacher.get(position);
                    Intent intent = new Intent(TeacherListActivity.this,
                            PersonDetailActivity.class);
                    intent.putExtra(AuthUserData.COL_GENRE, AuthUserData.GENRE_TEACHER);
                    intent.putExtra(TeacherData.COL_ID, teacherData.id);
                    TeacherListActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void requestData() {
        arrayListTeacher.clear();
        TeacherDataUtils.getInstance()
                .requestFetchTeacherListOfStudent(authUser.studend_id, arrayListTeacher,
                        dbHandler, TeacherDataUtils.TAG_FETCH_LIST);
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
        if (arrayListTeacher.size() > 0) {
            for (int idx = 0; idx < arrayListTeacher.size(); idx++) {
                TeacherData teacherData = arrayListTeacher.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], teacherData.code);
                items.put(mapKey[2], teacherData.name);
                items.put(mapKey[3], teacherData.section);
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
        private final WeakReference<TeacherListActivity> mActivity;

        DBHandler(TeacherListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final TeacherListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case TeacherDataUtils.TAG_FETCH_LIST:
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
