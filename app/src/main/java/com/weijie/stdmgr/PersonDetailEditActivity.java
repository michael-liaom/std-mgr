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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class PersonDetailEditActivity extends AppCompatActivity implements View.OnClickListener {
    final static int RESULT_CODE_CANCEL   = 0;
    final static int RESULT_CODE_COMMINT  = 1;
    StudentData studentData;

    private AuthUserData authUser;

    private EditText emailEditText, mobileEditText;
    private ProgressBar progressBar;
    private Button editButton;

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
            studentData.email   = emailEditText.getText().toString();
            studentData.mobile  = mobileEditText.getText().toString();
            StudentDataUtils.getInstance()
                    .requestCommitData(studentData, dbHandler,
                            StudentDataUtils.TAG_COMMIT_DATA);
            showBusyProgress(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail_edit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("个人信息修改");

        initData();
        initControls();
        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
    }

    private void initControls() {
        emailEditText   = (EditText) findViewById(R.id.email_edit_text);
        mobileEditText  = (EditText) findViewById(R.id.contact_edit_text);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);
        editButton   = (Button) findViewById(R.id.commit_button);
        editButton.setOnClickListener(this);
    }

    private void requestData() {
        Intent intent = getIntent();
        //获取传递的值

        studentData = new StudentData();
        int studendId = intent.getIntExtra(StudentData.COL_ID, 0);
        if (studendId > 0) {
            StudentDataUtils.getInstance()
                    .requestFetchStudentRegistration(studendId, studentData, dbHandler,
                            StudentDataUtils.TAG_FETCH_REGIST);
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

    private void refreshData() {
        emailEditText.setText(studentData.email);
        mobileEditText.setText(studentData.mobile);
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<PersonDetailEditActivity> mActivity;

        DBHandler(PersonDetailEditActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final PersonDetailEditActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case StudentDataUtils.TAG_FETCH_REGIST:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshData();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                        case StudentDataUtils.TAG_COMMIT_DATA:
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
