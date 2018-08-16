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
 * Created by weijie on 2018/5/3.
 */
public class ClassListActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "code",
            "name",
            "section"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_section_text_tiew
    };

    private ArrayList <ClassData> arrayListClass;
    private AuthUserData authUser;

    private String requestPrama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

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
        arrayListClass = new ArrayList<>();
        listMap = new ArrayList<>();

        Intent intent = getIntent();
        requestPrama = intent.getStringExtra(MainActivity.REQUEST_PARAM);
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_class_list_item, mapKey, mapResurceId);

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListClass.size()) {
                    ClassData classData = arrayListClass.get(position);
                    Intent intent;
                    if (requestPrama.equals(MainActivity.REQUEST_PRAM_EXCHANGE)) {
                        intent = new Intent(ClassListActivity.this,
                                ExchangeSubjectListActivity.class);
                    }
                    else {
                        intent = new Intent(ClassListActivity.this,
                                ClassStudentListActivity.class);
                    }
                    intent.putExtra(ClassData.COL_ID, classData.id);
                    ClassListActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void requestData() {
        arrayListClass.clear();
        ClassDataUtils.getInstance()
                .requestFetchClassListOfTeacher(authUser.teacher_id, arrayListClass,
                        dbHandler, ClassDataUtils.TAG_FETCH_CLASS_LIST);
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
        if (arrayListClass.size() > 0) {
            for (int idx = 0; idx < arrayListClass.size(); idx++) {
                ClassData classData = arrayListClass.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], classData.code);
                items.put(mapKey[2], classData.name);
                items.put(mapKey[3], classData.section);
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
        private final WeakReference<ClassListActivity> mActivity;

        DBHandler(ClassListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final ClassListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case ClassDataUtils.TAG_FETCH_CLASS_LIST:
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
