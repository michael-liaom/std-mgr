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
                            if (absenceFormData.classTeacher.id == authUser.teacher_id) {
                                absenceFormData.classApproval = AbsenceFormData.APPROVAL;
                            }
                            if (absenceFormData.courseData.teacherId == authUser.teacher_id) {
                                absenceFormData.courseApproval = AbsenceFormData.APPROVAL;
                            }
                            AbsenceFormDataUtils.getInstance()
                                    .requestSignApply(absenceFormData, dbHandler,
                                            AbsenceFormDataUtils.TAG_APPROVE_APPLY);
                        }
                    })
                    .setNegativeButton(R.string.button_reject, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (absenceFormData.classTeacher.id == authUser.teacher_id) {
                                absenceFormData.classApproval = AbsenceFormData.REJECT;
                            }
                            if (absenceFormData.courseData.teacherId == authUser.teacher_id) {
                                absenceFormData.courseApproval = AbsenceFormData.REJECT;
                            }
                            AbsenceFormDataUtils.getInstance()
                                    .requestSignApply(absenceFormData, dbHandler,
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
        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        absenceFormData = new AbsenceFormData();

    }

    private void initControls() {
        progressBar
                = (ProgressBar) findViewById(R.id.progress_bar);
        commiteButton       = (Button) findViewById(R.id.commit_button);

        commiteButton.setOnClickListener(this);

    }

    private void requestData() {
        Intent intent = getIntent();
        //获取传递的值
        int applyId = intent.getIntExtra(AbsenceFormData.COL_ID, 0);

        if (applyId > 0) {
            AbsenceFormDataUtils.getInstance().requestFetchApply(applyId, absenceFormData,
                    dbHandler, AbsenceFormDataUtils.TAG_FETCH_ONE_APPLY);
            showBusyProgress(true);
        }
    }

    private void refreshDisp() {
        TextView applyFromTextView  = (TextView) findViewById(R.id.apply_from_text_view);
        TextView classNameTextView  = (TextView) findViewById(R.id.class_name_text_view);
        TextView studentCodeTextView= (TextView) findViewById(R.id.student_code_text_View);
        TextView beginTextView      = (TextView) findViewById(R.id.begin_text_view);
        TextView endingTextView     = (TextView) findViewById(R.id.ending_text_view);
        TextView typeTextView       = (TextView) findViewById(R.id.type_text_view);
        TextView causeTextView      = (TextView) findViewById(R.id.cause_text_view);
        TextView classTeacherTextView
                = (TextView) findViewById(R.id.class_teacher_text_view);
        TextView courseTextView     = (TextView) findViewById(R.id.course_text_view);
        TextView courseTeacherTextView
                = (TextView) findViewById(R.id.course_teacher_text_view);
        TextView courseCountTextView= (TextView) findViewById(R.id.course_count_text_view);
        TextView classApprovalTextView
                = (TextView) findViewById(R.id.class_approval_text_view);
        TextView classApprovedByTextView
                = (TextView) findViewById(R.id.class_approved_by_text_view);
        TextView courseApprovalTextView
                = (TextView) findViewById(R.id.course_approval_text_view);
        TextView courseApprovedByTextView
                = (TextView) findViewById(R.id.course_approved_by_text_view);

        applyFromTextView.setText(absenceFormData.studentName);
        classNameTextView.setText(absenceFormData.studentData.classData.name);
        studentCodeTextView.setText(Integer.toString(absenceFormData.studentCode));
        beginTextView.setText(CommUtils.toLocalDateString(absenceFormData.begin));
        endingTextView.setText(CommUtils.toLocalDateString(absenceFormData.ending));
        typeTextView.setText(absenceFormData.type);
        causeTextView.setText(absenceFormData.cause);
        classTeacherTextView.setText(absenceFormData.classTeacher.name);
        courseTextView.setText(absenceFormData.courseData.code + " "
                + absenceFormData.courseData.name);
        courseTeacherTextView.setText(absenceFormData.courseData.teacherName);
        courseCountTextView.setText(Integer.toString(absenceFormData.courseCount));

        classApprovalTextView.setText(absenceFormData.getClassApprovalStatus());
        if (absenceFormData.classApproval == AbsenceFormData.PENDING) {
            classApprovalTextView.setText((""));
        }
        else {
            classApprovedByTextView.setText(absenceFormData.classTeacher.name);
        }

        courseApprovalTextView.setText(absenceFormData.getCourseApprovalStatus());
        if (absenceFormData.courseApproval == AbsenceFormData.PENDING) {
            courseApprovalTextView.setText((""));
        }
        else {
            courseApprovedByTextView.setText(absenceFormData.courseData.teacherName);
        }

        if(absenceFormData.classTeacher.id == authUser.teacher_id &&
                absenceFormData.classApproval == AbsenceFormData.PENDING) {
            commiteButton.setVisibility(View.VISIBLE);
        }
        else if(absenceFormData.courseData.teacherId == authUser.teacher_id &&
                absenceFormData.courseApproval == AbsenceFormData.PENDING) {
            commiteButton.setVisibility(View.VISIBLE);
        }
        else {
            commiteButton.setVisibility(View.GONE);
        }

        if(authUser.genre.equals(AuthUserData.GENRE_TEACHER) &&
                ((absenceFormData.classTeacher.id == authUser.teacher_id &&
                        absenceFormData.classApproval == AbsenceFormData.PENDING)||
                        absenceFormData.courseData.teacherId == authUser.teacher_id &&
                absenceFormData.courseApproval == AbsenceFormData.PENDING)) {
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
