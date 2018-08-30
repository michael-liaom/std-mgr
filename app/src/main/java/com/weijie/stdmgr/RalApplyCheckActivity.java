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

/**
 * Created by weijie on 2018/8/16.
 */
public class RalApplyCheckActivity extends AppCompatActivity implements View.OnClickListener {
    final static int RESULT_CODE_COMMIT_NONE    = 0;
    final static int RESULT_CODE_COMMIT_SUCCESS = 1;

    private EditText commentEditText;
    private Button commiteButton;
    private ProgressBar progressBar;

    private RalApplyData ralApplyData;
    private AuthUserData authUser;

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            setResult(RESULT_CODE_COMMIT_NONE);
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.commit_button) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.title_approval_check)
                    .setPositiveButton(R.string.button_approval, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ralApplyData.approvalComment = commentEditText.getText().toString();
                            ralApplyData.approval = RalApplyData.APPROVAL;
                            RalApplyDataUtils.getInstance()
                                    .requestSignApply(ralApplyData, dbHandler,
                                            RalApplyDataUtils.TAG_SIGN_APPLY);
                        }
                    })
                    .setNegativeButton(R.string.button_reject, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ralApplyData.approvalComment = commentEditText.getText().toString();
                            ralApplyData.approval = RalApplyData.REJECT;
                            RalApplyDataUtils.getInstance()
                                    .requestSignApply(ralApplyData, dbHandler,
                                            RalApplyDataUtils.TAG_SIGN_APPLY);
                        }
                    })
                    .setNeutralButton(R.string.button_pending, null);
            builder.show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_apply_check);

        setTitle("奖助贷申请详情");

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
        ralApplyData = new RalApplyData();

    }

    private void initControls() {
        progressBar
                = (ProgressBar) findViewById(R.id.progress_bar);
        commentEditText     = (EditText) findViewById(R.id.comment_edit_text);
        commiteButton       = (Button) findViewById(R.id.commit_button);

        commiteButton.setOnClickListener(this);

    }

    private void requestData() {
        Intent intent = getIntent();
        //获取传递的值
        int applyId = intent.getIntExtra(RalApplyData.COL_ID, 0);

        if (applyId > 0) {
            RalApplyDataUtils.getInstance().requestFetchApply(applyId, ralApplyData,
                    dbHandler, RalApplyDataUtils.TAG_FETCH_ONE_APPLY);
            showBusyProgress(true);
        }
    }

    private void refreshDisp() {
        TextView applyFromTextView  = (TextView) findViewById(R.id.apply_from_text_view);
        TextView nameTextView    = (TextView) findViewById(R.id.name_text_view);
        TextView amountTextView = (TextView) findViewById(R.id.amount_text_view);
        TextView gradeTextView  = (TextView) findViewById(R.id.grade_text_view);
        TextView termTextView   = (TextView) findViewById(R.id.term_text_view);
        TextView numberTextView = (TextView) findViewById(R.id.number_text_view);
        TextView requireTextView
                = (TextView) findViewById(R.id.requirement_text_view);
        TextView causeTextView  = (TextView) findViewById(R.id.cause_text_view);
        TextView commentTextView
                = (TextView) findViewById(R.id.comment_text_view);
        TextView approvalTextView
                = (TextView) findViewById(R.id.class_approval_text_view);
        TextView approvalByTextView
                = (TextView) findViewById(R.id.class_approved_by_text_view);

        applyFromTextView.setText(ralApplyData.studentName);
        nameTextView.setText(ralApplyData.ralData.name);
        amountTextView.setText(Integer.toString(ralApplyData.ralData.amount));
        gradeTextView.setText(ralApplyData.ralData.grade);
        termTextView.setText(ralApplyData.ralData.term);
        numberTextView.setText(Integer.toString(ralApplyData.ralData.number));
        String text = ralApplyData.ralData.requirement.replace("\\n", "\n");
        requireTextView.setText(text);
        requireTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        causeTextView.setText(ralApplyData.cause);
        commentTextView.setText(ralApplyData.approvalComment);

        approvalTextView.setText(ralApplyData.getApprovalStatus());
        if (ralApplyData.approval == AbsenceFormData.PENDING) {
            approvalByTextView.setText((""));
        }
        else {
            approvalByTextView.setText(ralApplyData.classTeacher.name);
        }

        if(authUser.genre.equals(AuthUserData.GENRE_TEACHER) &&
                ralApplyData.teacherId == authUser.teacher_id &&
                ralApplyData.approval == AbsenceFormData.PENDING) {
            commentEditText.setVisibility(View.VISIBLE);
            commentTextView.setVisibility(View.GONE);
            commiteButton.setVisibility(View.VISIBLE);
        }
        else {
            commentEditText.setVisibility(View.GONE);
            commentTextView.setVisibility(View.VISIBLE);
            commiteButton.setVisibility(View.GONE);
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

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<RalApplyCheckActivity> mActivity;

        DBHandler(RalApplyCheckActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final RalApplyCheckActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case RalApplyDataUtils.TAG_FETCH_ONE_APPLY:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshDisp();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                        case RalApplyDataUtils.TAG_SIGN_APPLY:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_commit_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.setResult(RESULT_CODE_COMMIT_SUCCESS);
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
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
