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

public class RalApplyListActivity extends AppCompatActivity {
    final private static int REQUEST_FOR_CHECK  = 2;

    private ListView listView;
    private ProgressBar progressBar;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "name",
            "amount",
            "term",
            "student",
            "approval"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_name_text_view,
            R.id.item_amount_text_view,
            R.id.item_term_text_tiew,
            R.id.item_student_text_tiew,
            R.id.item_approval_text_tiew
    };

    private ArrayList <RalApplyData> arrayListRalApplyData;
    private AuthUserData authUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_CHECK :
                if (resultCode == RalApplyCheckActivity.RESULT_CODE_COMMIT_SUCCESS) {
                    requestData();
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_apply_list);

        setTitle("奖助贷申请列表");
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
        arrayListRalApplyData = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView    = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_ral_apply_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListRalApplyData.size()) {
                    RalApplyData ralApplyData = arrayListRalApplyData.get(position);
                    Intent intent = new Intent(RalApplyListActivity.this,
                            RalApplyCheckActivity.class);
                    intent.putExtra(RalApplyData.COL_ID, ralApplyData.id);
                    RalApplyListActivity.this.startActivityForResult(intent,
                            REQUEST_FOR_CHECK);
                }
            }
        });
    }

    private void requestData() {
        arrayListRalApplyData.clear();
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            RalApplyDataUtils.getInstance()
                    .requestFetchApplieListFromStudent(authUser.studend_id, arrayListRalApplyData,
                            dbHandler, RalApplyDataUtils.TAG_FETCH_APPLIE_LIST);
        }
        else {
            RalApplyDataUtils.getInstance()
                    .requestFetchApplyListToTeacher(authUser.teacher_id, arrayListRalApplyData,
                            dbHandler, RalApplyDataUtils.TAG_FETCH_APPLIE_LIST);
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
        if (arrayListRalApplyData.size() > 0) {
            for (int idx = 0; idx < arrayListRalApplyData.size(); idx++) {
                RalApplyData ralApplyData = arrayListRalApplyData.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], ralApplyData.ralName);
                items.put(mapKey[2], ralApplyData.ralAmount);
                items.put(mapKey[3], ralApplyData.ralTerm);
                items.put(mapKey[4], ralApplyData.studentName);
                items.put(mapKey[5], ralApplyData.getApprovalStatus());
                listMap.add(items);
            }
        }
        else {
            Map<String, Object> items = new HashMap<String, Object>();
            items.put(mapKey[0], "");
            items.put(mapKey[1], "无数据");
            items.put(mapKey[2], "");
            items.put(mapKey[3], "");
            items.put(mapKey[4], "");
            items.put(mapKey[5], "");
            listMap.add(items);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<RalApplyListActivity> mActivity;

        DBHandler(RalApplyListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final RalApplyListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case RalApplyDataUtils.TAG_FETCH_APPLIE_LIST:
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
