package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class CourseDetailActivity extends AppCompatActivity {
    private AuthUserData authUser;
    private CourseData courseData;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

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
        courseData  = new CourseData();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button button = (Button) findViewById(R.id.action_button);
        button.setVisibility(View.GONE);
    }

    private void requestData() {
        Intent intent = getIntent();
        int courseId = intent.getIntExtra(CourseData.COL_ID, 0);

        if (courseId > 0) {
            CourseDataUtils.getInstance()
                    .requestFetchCourseData(courseId, courseData, dbHandler,
                            CourseDataUtils.TAG_FETCH_COURSE_DATA);
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
        TextView codeTextView = (TextView) findViewById(R.id.code_text_view);
        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        TextView teacherTextView
                = (TextView) findViewById(R.id.teacher_text_view);
        TextView creditTextView
                = (TextView) findViewById(R.id.credit_text_view);
        TextView sectionTextView
                = (TextView) findViewById(R.id.section_text_view);
        TextView classroomTextView
                = (TextView) findViewById(R.id.classroom_text_view);
        TextView genreTextView
                = (TextView) findViewById(R.id.genre_text_view);
        TextView scheduleTextView
                = (TextView) findViewById(R.id.schedule_text_view);
        TextView termTextView = (TextView) findViewById(R.id.term_text_view);

        codeTextView.setText(courseData.code);
        nameTextView.setText(courseData.name);
        teacherTextView.setText(courseData.teacherName);
        creditTextView.setText(Integer.toString(courseData.credit));
        sectionTextView.setText(courseData.section);
        classroomTextView.setText(courseData.classroom);
        genreTextView.setText(courseData.genre);
        scheduleTextView.setText(courseData.schedule);
        termTextView.setText(Integer.toString(courseData.term));

        if (authUser.teacher_id == courseData.teacherId) {
            Button button = (Button) findViewById(R.id.action_button);
            button.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CourseDetailActivity.this,
                            CourseStudentsActivity.class);
                    intent.putExtra(CourseData.COL_ID, courseData.id);
                    startActivity(intent);
                }
            });
        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<CourseDetailActivity> mActivity;

        DBHandler(CourseDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CourseDetailActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case CourseDataUtils.TAG_FETCH_COURSE_DATA:
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
