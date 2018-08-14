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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weijie on 2018/8/14.
 */
public class RalListActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private ArrayList<Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "name",
            "amount",
            "term",
            "grade",
            "number"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_name_text_view,
            R.id.item_amount_text_view,
            R.id.item_term_text_tiew,
            R.id.item_grade_text_tiew,
            R.id.item_number_text_tiew
    };

    private ArrayList <RalData> arrayListRal;
    private AuthUserData authUser;
    private int mTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    Calendar calendar = Calendar.getInstance();
    mTerm = calendar.get(Calendar.YEAR);

    setTitle("奖助贷信息列表");

    initData();
    initControls();

    requestData();
}

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        arrayListRal = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ListView listView   = (ListView) findViewById(R.id.list_view_dynamic);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_ral_list_item, mapKey, mapResurceId);

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListRal.size()) {
                    RalData ralData = arrayListRal.get(position);
                    Intent intent = new Intent(RalListActivity.this,
                            RalDetailActivity.class);
                    intent.putExtra(RalData.COL_ID, ralData.id);
                    RalListActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void requestData() {
        arrayListRal.clear();
        RalDataUtils.getInstance()
                .requestRalDataList(mTerm, arrayListRal,
                        dbHandler, RalDataUtils.TAG_FETCH_LIST);
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
        if (arrayListRal.size() > 0) {
            for (int idx = 0; idx < arrayListRal.size(); idx++) {
                RalData ralData = arrayListRal.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], ralData.name);
                items.put(mapKey[2], ralData.amount);
                items.put(mapKey[3], ralData.term);
                items.put(mapKey[4], ralData.grade);
                items.put(mapKey[5], ralData.number);
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
    private final WeakReference<RalListActivity> mActivity;

    DBHandler(RalListActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(final Message msg) {
        final RalListActivity activity = mActivity.get();
        if (activity != null) {
            boolean is_sucess = false;
            String message = null;
            // ...
            String tag = (String) msg.obj;
            if (tag != null) {
                switch (tag) {
                    case RalDataUtils.TAG_FETCH_LIST:
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
