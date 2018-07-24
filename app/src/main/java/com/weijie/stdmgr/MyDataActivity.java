package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MyDataActivity extends AppCompatActivity {
    private AuthUserData authUser;

    LinearLayout studentLayout, teacherLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();
        requestData();
    }

    private void initData() {
        authUser    = MyApplication.getInstance().authUser;
    }

    private void initControls() {

        studentLayout   = (LinearLayout) findViewById(R.id.student_layout);
        teacherLayout   = (LinearLayout) findViewById(R.id.teacher_layout);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);

        studentLayout.setVisibility(View.GONE);
        teacherLayout.setVisibility(View.GONE);
    }

    private void requestData() {
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            authUser.studentData = new StudentData();
            StudentDataUtils.getInstance()
                    .requestFetchStudentRegistration(authUser.studend_id, authUser.studentData, dbHandler,
                            StudentDataUtils.TAG_FETCH_STUDENT_REGISTRATION);
            showBusyProgress(true);
        }
        else {
            authUser.teacherData = new TeacherData();
            TeacherDataUtils.getInstance()
                    .requestFetchTeacherRegistration(authUser.teacher_id, authUser.teacherData, dbHandler,
                            TeacherDataUtils.TAG_FETCH_TEACHER_REGISTRATION);
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
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            StudentData studentData = authUser.studentData;
            TextView nameTextView = (TextView) findViewById(R.id.student_name_text_view);
            TextView codeTextView = (TextView) findViewById(R.id.student_code_text_view);
            TextView sectionTextView
                    = (TextView) findViewById(R.id.student_section_text_view);
            TextView majorTextView= (TextView) findViewById(R.id.student_major_text_view);
            TextView classTextView= (TextView) findViewById(R.id.student_class_text_view);
            TextView roomTextView = (TextView) findViewById(R.id.student_room_text_view);
            TextView emailTextView= (TextView) findViewById(R.id.student_email_text_view);

            nameTextView.setText(studentData.name);
            codeTextView.setText(Integer.toString(studentData.code));
            sectionTextView.setText(studentData.classData.section);
            majorTextView.setText(studentData.classData.major);
            classTextView.setText(studentData.classData.name);
            roomTextView.setText(studentData.room);
            emailTextView.setText(studentData.email);
            studentLayout.setVisibility(View.VISIBLE);
        }
        else {
            TeacherData teacherData = authUser.teacherData;
            TextView nameTextView = (TextView) findViewById(R.id.teacher_name_text_view);
            TextView codeTextView = (TextView) findViewById(R.id.teacher_code_text_view);
            TextView sectionTextView
                    = (TextView) findViewById(R.id.teacher_section_text_view);
            TextView roomTextView = (TextView) findViewById(R.id.teacher_room_text_view);
            TextView emailTextView= (TextView) findViewById(R.id.teacher_email_text_view);

            nameTextView.setText(teacherData.name);
            codeTextView.setText(Integer.toString(teacherData.code));
            sectionTextView.setText(teacherData.section);
            roomTextView.setText(teacherData.room);
            emailTextView.setText(teacherData.email);
            teacherLayout.setVisibility(View.VISIBLE);
        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<MyDataActivity> mActivity;

        DBHandler(MyDataActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final MyDataActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case StudentDataUtils.TAG_FETCH_STUDENT_REGISTRATION:
                        case TeacherDataUtils.TAG_FETCH_TEACHER_REGISTRATION:
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
