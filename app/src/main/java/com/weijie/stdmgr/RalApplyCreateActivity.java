package com.weijie.stdmgr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class RalApplyCreateActivity extends AppCompatActivity implements View.OnClickListener {
    final static int RESULT_CODE_APPLY_CANCEL   = 0;
    final static int RESULT_CODE_APPLY_SUCCESS  = 1;

    private View inputForm;
    TextView menuTextView;
    TextView applyFrom;
    TextView nameTextView;
    TextView amountTextView;
    TextView gradeTextView;
    TextView termTextView;
    TextView numberTextView;
    TextView requireTextView;
    private EditText causeEditText;
    private Button commiteButton;

    private ProgressBar progressBar;

    private AuthUserData authUser;
    private ClassData classData;
    private RalData ralData;
    private int ralId;

    private RalApplyData ralApplyData;

    private int requestCounter;

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            setResult(RESULT_CODE_APPLY_CANCEL);
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_apply_create);

        setTitle("奖助贷申请");

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
        ralApplyData= new RalApplyData();
        classData   = new ClassData();
        ralData     = new RalData();

        Intent intent = getIntent();
        ralId = intent.getIntExtra(RalData.COL_ID, 0);
    }

    private void initControls() {
        menuTextView    = (TextView) findViewById(R.id.title_text_view);
        applyFrom       = (TextView) findViewById(R.id.apply_from_text_view);
        inputForm       = findViewById(R.id.input_form);
        nameTextView    = (TextView) findViewById(R.id.name_text_view);
        amountTextView  = (TextView) findViewById(R.id.amount_text_view);
        gradeTextView   = (TextView) findViewById(R.id.grade_text_view);
        termTextView    = (TextView) findViewById(R.id.term_text_view);
        numberTextView  = (TextView) findViewById(R.id.number_text_view);
        requireTextView = (TextView) findViewById(R.id.requirement_text_view);
        causeEditText   = (EditText) findViewById(R.id.cause_edit_text);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);
        commiteButton   = (Button) findViewById(R.id.commit_button);

        commiteButton.setOnClickListener(this);

        //menuTextView.setFocusable(true);
        //menuTextView.setFocusableInTouchMode(true);
        //menuTextView.requestFocus();
    }

    private void requestData() {
        if (ralId > 0) {
            RalDataUtils.getInstance().requestFetchRalData(ralId, ralData,
                    dbHandler, RalDataUtils.TAG_FETCH_DATA);
            ClassDataUtils.getInstance().requestFetchClassData(authUser.studend_id, classData,
                    dbHandler, ClassDataUtils.TAG_FETCH_CLASS_DATA);
            requestCounter = 2;
            showBusyProgress(true);
        }
    }

    private void refreshControlDisp() {
        if (requestCounter == 0) {
            applyFrom.setText(authUser.studentData.name);
            nameTextView.setText(ralData.name);
            amountTextView.setText(Integer.toString(ralData.amount));
            gradeTextView.setText(ralData.grade);
            termTextView.setText(ralData.term);
            numberTextView.setText(Integer.toString(ralData.number));
            String text = ralData.requirement.replace("\\n", "\n");
            requireTextView.setText(text);
            requireTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);

        }
        else {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                if (isInputDataVailable()) {
                    commiteData();
                }
                break;
        }
    }

    private boolean isInputDataVailable() {
        //Please write the code
        if (causeEditText.getText().length() > 0) {
            return true;
        }
        else {
            String message = RalApplyCreateActivity.this.getResources()
                    .getString(R.string.absence_apply_is_not_fully_file);
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                    .show();
            return  false;
        }
    }

    private void commiteData() {
        ralApplyData.ralId      = ralId;
        ralApplyData.studentId  = authUser.studend_id;
        ralApplyData.teacherId  = classData.teacherId;
        ralApplyData.cause      = causeEditText.getText().toString();

        RalApplyDataUtils.getInstance().requestCommitApply(ralApplyData,
                dbHandler, RalApplyDataUtils.TAG_COMMIT_APPLY);
        requestCounter++;
        showBusyProgress(true);
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<RalApplyCreateActivity> mActivity;

        DBHandler(RalApplyCreateActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final RalApplyCreateActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case RalDataUtils.TAG_FETCH_DATA:
                            activity.requestCounter--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshControlDisp();
                            }
                            else {
                                activity.showBusyProgress(false);
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                        case ClassDataUtils.TAG_FETCH_CLASS_DATA:
                            activity.requestCounter--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshControlDisp();
                            }
                            else {
                                activity.showBusyProgress(false);
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                        case RalApplyDataUtils.TAG_COMMIT_APPLY:
                            activity.requestCounter--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_commit_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.setResult(RESULT_CODE_APPLY_SUCCESS);
                                                activity.finish();
                                            }
                                        });
                                builder.show();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
