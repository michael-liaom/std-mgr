package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbsenceMainActivity extends AppCompatActivity {
    final private static int REQUEST_FOR_ABSENCE_APPLY = 1;

    private ListView listView;
    private ProgressBar progressBar;
    private Button applyBotton;

    private ArrayList <Map<String, Object>> listMap;
    SimpleAdapter simpleAdapter;
    private String[] mapKey = {
            "no",
            "code",
            "name",
            "type",
            "begin",
            "approval"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_type_text_tiew,
            R.id.item_begin_text_view,
            R.id.item_approval_text_view
    };

    private ArrayList <AbsenceApplyData> arrayListAbsenceData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOR_ABSENCE_APPLY) {
            if (requestCode == AbsenceApplyActivity.RESULT_CODE_APPLY_SUCCESS) {
                requestData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_main);

        initData();
        initControls();

        requestData();
    }

    private void initData() {
        arrayListAbsenceData = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView    = (ListView) findViewById(R.id.list_view_dynamic);
        applyBotton = (Button) findViewById(R.id.apply_button);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_absence_main_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);

        applyBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AbsenceMainActivity.this,
                        AbsenceApplyActivity.class), REQUEST_FOR_ABSENCE_APPLY);
            }
        });
    }

    private void requestData() {
        arrayListAbsenceData.clear();
        AbsenceApplyDataUtils.getInstance().requestFetchAllMyApply(arrayListAbsenceData,
                dbHandler, AbsenceApplyDataUtils.TAG_FETCH_ALLMY_APPLY);

        showLoginProgress(true);
    }

    private void showLoginProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
            applyBotton.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.GONE);
            applyBotton.setEnabled(true);
        }
    }

    private void refreshData() {
        if (arrayListAbsenceData.size() > 0) {
            for (int idx = 0; idx < arrayListAbsenceData.size(); idx++) {
                AbsenceApplyData absenceApplyData = arrayListAbsenceData.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], absenceApplyData.studentCode);
                items.put(mapKey[2], absenceApplyData.studentName);
                items.put(mapKey[3], absenceApplyData.type);
                items.put(mapKey[4], CommUtils.toLocalDateString(absenceApplyData.begin));
                items.put(mapKey[5], absenceApplyData.approval);
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
        private final WeakReference<AbsenceMainActivity> mActivity;

        DBHandler(AbsenceMainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final AbsenceMainActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case AbsenceApplyDataUtils.TAG_FETCH_ALLMY_APPLY:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshData();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showLoginProgress(false);
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
