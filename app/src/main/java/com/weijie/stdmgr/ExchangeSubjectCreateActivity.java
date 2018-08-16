package com.weijie.stdmgr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

public class ExchangeSubjectCreateActivity extends AppCompatActivity implements View.OnClickListener {
    final static int RESULT_CODE_CANCEL   = 0;
    final static int RESULT_CODE_COMMINT  = 1;

    private AuthUserData authUser;
    ExchangeSubjectData subjectData;
    ExchangeDetailData  detailData;

    private ArrayList<StudentData> arrayListTo;
    TeacherData teacherData;
    int classId;

    private Spinner toSpinner;
    private EditText subjectEditText, contentEditText;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            setResult(RESULT_CODE_CANCEL);
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.commit_button) {
            if (subjectEditText.getText().length() == 0) {
                Toast.makeText(this, R.string.message_subject_blank_error,
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (contentEditText.getText().length() == 0) {
                Toast.makeText(this, R.string.message_content_blank_error,
                        Toast.LENGTH_LONG).show();
                return;
            }

            subjectData.classId     = classId;
            if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                subjectData.studentId = authUser.studentData.id;
            }
            else {
                subjectData.studentId = arrayListTo.get(toSpinner.getSelectedItemPosition()).id;
            }
            if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                subjectData.direction = ExchangeSubjectData.EXCHANGE_DIRECTION_FROM;
            }
            else {
                subjectData.direction = ExchangeSubjectData.EXCHANGE_DIRECTION_TO;
            }
            subjectData.subject     = subjectEditText.getText().toString();
            subjectData.create      = new Date();
            subjectData.update      = new Date();

            detailData.direction    = subjectData.direction;
            detailData.content      = contentEditText.getText().toString();
            detailData.create       = new Date();

            ExchangeSubjectDataUtils.getInstance()
                    .requestCommit(subjectData, detailData, dbHandler,
                            ExchangeSubjectDataUtils.TAG_COMMIT_DATA);
            showBusyProgress(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_subject_create);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("创建新的交流");

        initData();
        initControls();
        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;

        subjectData = new ExchangeSubjectData();
        detailData  = new ExchangeDetailData();

        Intent intent = getIntent();
        classId = intent.getIntExtra(ClassData.COL_ID, 0);
        arrayListTo = new ArrayList<>();
    }

    private void initControls() {
        toSpinner       = (Spinner) findViewById(R.id.to_spinner);
        subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
        contentEditText = (EditText) findViewById(R.id.content_edit_text);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);
        Button commitButton    = (Button) findViewById(R.id.commit_button);
        commitButton.setOnClickListener(this);
    }

    private void requestData() {

        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            teacherData = new TeacherData();
            int studentId = authUser.studend_id;
            TeacherDataUtils.getInstance().requestFetchClassTeacherOfStudent(studentId, teacherData,
                    dbHandler, TeacherDataUtils.TAG_FETCH_REGIST);
            showBusyProgress(true);
        }
        else {
            if (classId > 0) {
                StudentDataUtils.getInstance()
                        .requestFetchStudentListOfClass(classId, arrayListTo, dbHandler,
                                StudentDataUtils.TAG_FETCH_LIST);
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
        ArrayList<String> dataList = new ArrayList<String>();
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            dataList.add(teacherData.name + "(辅导员)");
        }
        else {
            for (StudentData studentData : arrayListTo) {
                dataList.add(studentData.name + " " + studentData.code);
            }
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(adapter);
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<ExchangeSubjectCreateActivity> mActivity;

        DBHandler(ExchangeSubjectCreateActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final ExchangeSubjectCreateActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case TeacherDataUtils.TAG_FETCH_REGIST:
                        case StudentDataUtils.TAG_FETCH_LIST:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshData();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                        case ExchangeSubjectDataUtils.TAG_COMMIT_DATA:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_commit_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.setResult(RESULT_CODE_COMMINT);
                                                activity.finish();
                                            }
                                        });
                                builder.show();
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
