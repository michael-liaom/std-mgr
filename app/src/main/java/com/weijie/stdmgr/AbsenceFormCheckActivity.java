package com.weijie.stdmgr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class AbsenceFormCheckActivity extends AppCompatActivity implements View.OnClickListener{
    final static int RESULT_CODE_COMMIT_NONE    = 0;
    final static int RESULT_CODE_COMMIT_SUCCESS = 1;

    private TextView applyFromTextView;
    private TextView applyToTextView;
    private TextView applyCcTextView;
    private TextView applyTypeTextView;
    private TextView beginTextView,
            daysTextView,
            hoursTextView,
            causeTextView;
    private TextView approvalTextView, approvedByTextView;
    private Button commiteButton;
    private ProgressBar progressBar;

    private AbsenceFormData absenceFormData;
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
                            AbsenceFormDataUtils.getInstance()
                                    .requestApproveApply(absenceFormData, dbHandler,
                                            AbsenceFormDataUtils.TAG_APPROVE_APPLY);
                        }
                    })
                    .setNegativeButton(R.string.button_reject, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AbsenceFormDataUtils.getInstance()
                                    .requestRejectApply(absenceFormData, dbHandler,
                                            AbsenceFormDataUtils.TAG_REJECT_APPLY);
                        }
                    })
                    .setNeutralButton(R.string.button_pending, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            builder.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_form_check);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();
        showBusyProgress(true);
    }

    private void initData() {
        Intent intent = getIntent();
        //获取传递的值
        int applyId = intent.getIntExtra(AbsenceFormData.COL_ID, 0);

        authUser = MyApplication.getInstance().authUser;
        absenceFormData = new AbsenceFormData();

        if (applyId > 0) {
            AbsenceFormDataUtils.getInstance().requestFetchApply(applyId, absenceFormData,
                    dbHandler, AbsenceFormDataUtils.TAG_FETCH_ONE_APPLY);
        }
    }

    private void initControls() {
        applyFromTextView   = (TextView) findViewById(R.id.apply_from_text_view);
        applyToTextView     = (TextView) findViewById(R.id.apply_to_text_view);
        applyCcTextView     = (TextView) findViewById(R.id.apply_cc_text_view);
        applyTypeTextView   = (TextView) findViewById(R.id.apply_type_text_view);
        beginTextView       = (TextView) findViewById(R.id.begin_text_view);
        daysTextView        = (TextView) findViewById(R.id.days_text_view);
        hoursTextView       = (TextView) findViewById(R.id.hours_text_view);
        causeTextView       = (TextView) findViewById(R.id.cause_text_view);
        approvalTextView    = (TextView) findViewById(R.id.approval_text_view);
        approvedByTextView  = (TextView) findViewById(R.id.approved_by_text_view);
        progressBar
                = (ProgressBar) findViewById(R.id.progress_bar);
        commiteButton       = (Button) findViewById(R.id.commit_button);

        commiteButton.setOnClickListener(this);

        if(authUser.genre.equals(AuthUserData.GENRE_TEACHER) &&
            absenceFormData.toTeacherId == authUser.teacher_id) {
            commiteButton.setVisibility(View.VISIBLE);
        }
        else {
            commiteButton.setVisibility(View.GONE);
        }
    }

    private void refreshDisp() {
        applyFromTextView.setText(absenceFormData.studentName);
        applyToTextView.setText(absenceFormData.toTeacherName);

        StringBuilder builder = new StringBuilder();
        int idx = 0;
        for (CourseData courseData : absenceFormData.ccList) {
            if (idx > 0) {
                builder.append(", ");
            }
            builder.append(courseData.name);
            builder.append(" / ");
            builder.append(courseData.teacherName);
            idx++;
        }
        applyCcTextView.setText(builder);
        applyTypeTextView.setText(absenceFormData.type);
        beginTextView.setText(CommUtils.toLocalDateString(absenceFormData.begin));
        long duration = (absenceFormData.ending.getTime() - absenceFormData.begin.getTime() ) / 1000;
        int days = (int) (duration / 86400);
        int hours = (int)(duration - days * 86400 ) / 3600;
        if (days > 0) {
            daysTextView.setText(Integer.toString(days));
        }
        else {
            daysTextView.setText("--");
        }

        if (hours >1) {
            hoursTextView.setText(Integer.toString(hours));
        }
        else {
            hoursTextView.setText("1");
        }

        causeTextView.setText(absenceFormData.cause);

        approvalTextView.setText(absenceFormData.getApprovalStatus());
        if (absenceFormData.approval == AbsenceFormData.PENDING) {
            approvedByTextView.setText((""));
        }
        else {
            approvedByTextView.setText(absenceFormData.toTeacherName);
        }

        if(absenceFormData.toTeacherId == authUser.teacher_id &&
                absenceFormData.approval == 0) {
            commiteButton.setVisibility(View.VISIBLE);
        }
        else {
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
        private final WeakReference<AbsenceFormCheckActivity> mActivity;

        DBHandler(AbsenceFormCheckActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final AbsenceFormCheckActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case AbsenceFormDataUtils.TAG_FETCH_ONE_APPLY:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshDisp();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                        case AbsenceFormDataUtils.TAG_APPROVE_APPLY:
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
                        case AbsenceFormDataUtils.TAG_REJECT_APPLY:
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
