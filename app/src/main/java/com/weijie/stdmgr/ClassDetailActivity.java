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

public class ClassDetailActivity extends AppCompatActivity {
    private AuthUserData authUser;
    private ClassData classData;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

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
        classData  = new ClassData();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button button = (Button) findViewById(R.id.action_button);
        button.setVisibility(View.GONE);
    }

    private void requestData() {
        Intent intent = getIntent();
        int classId = intent.getIntExtra(CourseData.COL_ID, 0);

        if (classId > 0) {
            ClassDataUtils.getInstance()
                    .requestFetchClassData(classId, classData, dbHandler,
                            ClassDataUtils.TAG_FETCH_CLASS_DATA);
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
        TextView gradeTextView
                = (TextView) findViewById(R.id.grade_text_view);
        TextView sectionTextView
                = (TextView) findViewById(R.id.section_text_view);
        TextView majorTextView
                = (TextView) findViewById(R.id.major_text_view);

        codeTextView.setText(classData.code);
        nameTextView.setText(classData.name);
        teacherTextView.setText(classData.teacherName);
        gradeTextView.setText(Integer.toString(classData.grade));
        sectionTextView.setText(classData.section);
        majorTextView.setText(classData.major);

        if (authUser.teacher_id == classData.teacherId) {
            Button button = (Button) findViewById(R.id.action_button);
            button.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ClassDetailActivity.this,
                            CourseStudentListActivity.class);
                    intent.putExtra(CourseData.COL_ID, classData.id);
                    startActivity(intent);
                }
            });
        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<ClassDetailActivity> mActivity;

        DBHandler(ClassDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final ClassDetailActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case ClassDataUtils.TAG_FETCH_CLASS_DATA:
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
