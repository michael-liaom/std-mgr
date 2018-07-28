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

/**
 * Created by weijie on 2018/6/10.
 */
public class AbsenceFormListActivity extends AppCompatActivity {
    final private static int REQUEST_FOR_ABSENCE_APPLY      = 1;
    final private static int REQUEST_FOR_ABSENCE_CHECK    = 2;

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
            "class_approval",
            "course_approval"
    };
    private int[] mapResurceId = {
            R.id.item_no_text_view,
            R.id.item_code_text_view,
            R.id.item_name_text_view,
            R.id.item_type_text_tiew,
            R.id.item_begin_text_view,
            R.id.item_class_approval_text_view,
            R.id.item_course_approval_text_view
    };

    private ArrayList <AbsenceFormData> arrayListAbsenceData;
    private AuthUserData authUser;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_ABSENCE_APPLY:
                if (resultCode == AbsenceFormApplyActivity.RESULT_CODE_APPLY_SUCCESS) {
                    requestData();
                }
                break;
            case REQUEST_FOR_ABSENCE_CHECK:
                if (resultCode == AbsenceFormCheckActivity.RESULT_CODE_COMMIT_SUCCESS) {
                    requestData();
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_form_list);

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
        arrayListAbsenceData = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView    = (ListView) findViewById(R.id.list_view_dynamic);
        applyBotton = (Button) findViewById(R.id.apply_button);

        simpleAdapter = new SimpleAdapter(this, listMap,
                R.layout.activity_absence_form_list_item, mapKey, mapResurceId);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < arrayListAbsenceData.size()) {
                    AbsenceFormData absenceFormData = arrayListAbsenceData.get(position);
                    Intent intent = new Intent(AbsenceFormListActivity.this,
                            AbsenceFormCheckActivity.class);
                    intent.putExtra(AbsenceFormData.COL_ID, absenceFormData.id);
                    AbsenceFormListActivity.this.startActivityForResult(intent,
                            REQUEST_FOR_ABSENCE_CHECK);
                }
            }
        });

        applyBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AbsenceFormListActivity.this,
                        AbsenceFormApplyActivity.class), REQUEST_FOR_ABSENCE_APPLY);
            }
        });

        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            applyBotton.setVisibility(View.VISIBLE);
        }
        else {
            applyBotton.setVisibility(View.GONE);
        }
    }

    private void requestData() {
        arrayListAbsenceData.clear();
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            AbsenceFormDataUtils.getInstance()
                    .requestFetchApplieListFromStudent(authUser.studend_id, arrayListAbsenceData,
                            dbHandler, AbsenceFormDataUtils.TAG_FETCH_APPLIE_LIST);
        }
        else {
            AbsenceFormDataUtils.getInstance()
                    .requestFetchApplyListToTeacher(authUser.teacher_id, arrayListAbsenceData,
                            dbHandler, AbsenceFormDataUtils.TAG_FETCH_APPLIE_LIST);
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
        if (arrayListAbsenceData.size() > 0) {
            for (int idx = 0; idx < arrayListAbsenceData.size(); idx++) {
                AbsenceFormData absenceFormData = arrayListAbsenceData.get(idx);
                Map<String, Object> items = new HashMap<String, Object>();
                items.put(mapKey[0], Integer.toString(idx + 1));
                items.put(mapKey[1], absenceFormData.studentCode);
                items.put(mapKey[2], absenceFormData.studentName);
                items.put(mapKey[3], absenceFormData.type);
                items.put(mapKey[4], CommUtils.toLocalDateString(absenceFormData.begin));
                items.put(mapKey[5], absenceFormData.getClassApprovalStatus());
                items.put(mapKey[6], absenceFormData.getCourseApprovalStatus());
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
        private final WeakReference<AbsenceFormListActivity> mActivity;

        DBHandler(AbsenceFormListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final AbsenceFormListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case AbsenceFormDataUtils.TAG_FETCH_APPLIE_LIST:
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
