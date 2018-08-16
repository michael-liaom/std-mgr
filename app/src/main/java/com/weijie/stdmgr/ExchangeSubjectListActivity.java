package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExchangeSubjectListActivity extends AppCompatActivity implements View.OnClickListener {
    final private static int REQUEST_FOR_CREATE  = 2;

    private ListView listView;
    private ProgressBar progressBar;
    private Button createButton;
    private int classId;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "subject",
            "from",
            "timestamp"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_subject_text_view,
            R.id.item_from_text_view,
            R.id.item_timestamp_text_tiew
    };

    private ArrayList <ExchangeSubjectData> arrayListSubjectData;
    private AuthUserData authUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_CREATE:
                if (resultCode == ExchangeSubjectCreateActivity.RESULT_CODE_COMMINT) {
                    requestData();
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_button) {
            Intent intent = new Intent(this, ExchangeSubjectCreateActivity.class);
            intent.putExtra(ClassData.COL_ID, classId);
            startActivityForResult(intent, REQUEST_FOR_CREATE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_subject_list);

        authUser = MyApplication.getInstance().authUser;
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            setTitle("与辅导员的信息交流");
        }
        else {
            setTitle("与学生的信息交流");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();

        requestData();
    }
    private void initData() {
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            classId = authUser.studentData.class_id;
        }
        else {
            Intent intent  = getIntent();
            classId = intent.getIntExtra(ClassData.COL_ID, 0);
        }
        arrayListSubjectData = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView    = (ListView) findViewById(R.id.list_view_dynamic);
        createButton= (Button) findViewById(R.id.create_button);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_exchange_subject_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListSubjectData.size()) {
                    /*
                    ExchangeSubjectData subjectData = arrayListSubjectData.get(position);
                    Intent intent = new Intent(ExchangeSubjectListActivity.this,
                            ExchangeDetailListActivity.class);
                    ExchangeSubjectListActivity.this.startActivityForResult(intent,
                            REQUEST_FOR_CREATE);
                            */
                }
            }
        });

        createButton.setOnClickListener(this);
    }

    private void requestData() {
        arrayListSubjectData.clear();
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            ExchangeSubjectDataUtils.getInstance()
                    .requestFetchSubjectListOfStudent(authUser.studend_id, arrayListSubjectData,
                            dbHandler, ExchangeSubjectDataUtils.TAG_FETCH_LIST);
            showBusyProgress(true);
        }
        else {
            if (classId > 0) {
                ExchangeSubjectDataUtils.getInstance()
                        .requestFetchSubjectListOfClass(classId, arrayListSubjectData,
                                dbHandler, ExchangeSubjectDataUtils.TAG_FETCH_LIST);
                showBusyProgress(true);
            }
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
        listMap.clear();
        if (arrayListSubjectData.size() > 0) {
            for (int idx = 0; idx < arrayListSubjectData.size(); idx++) {
                ExchangeSubjectData subjectData = arrayListSubjectData.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], subjectData.subject);
                if (subjectData.direction.equals(ExchangeSubjectData.EXCHANGE_DIRECTION_FROM)) {
                    items.put(mapKey[2], subjectData.studentName);
                }
                else {
                    items.put(mapKey[2], "辅导员");
                }
                items.put(mapKey[3], subjectData.update);
                listMap.add(items);
            }
        }
        else {
            Map<String, Object> items = new HashMap<String, Object>();
            items.put(mapKey[0], "");
            items.put(mapKey[1], "无数据");
            items.put(mapKey[2], "");
            items.put(mapKey[3], "");
            listMap.add(items);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<ExchangeSubjectListActivity> mActivity;

        DBHandler(ExchangeSubjectListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final ExchangeSubjectListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case ExchangeSubjectDataUtils.TAG_FETCH_LIST:
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
