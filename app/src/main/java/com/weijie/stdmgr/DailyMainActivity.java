package com.weijie.stdmgr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class DailyMainActivity extends AppCompatActivity  implements View.OnClickListener{
    private AuthUserData authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_main);

        authUser = MyApplication.getInstance().authUser;
        initControls();
    }

    private void initControls() {
        LinearLayout studentLayout = (LinearLayout) findViewById(R.id.student_layout);
        LinearLayout teacherLayout = (LinearLayout) findViewById(R.id.teacher_layout);

        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            teacherLayout.setVisibility(View.GONE);
            Button myCourseButton   = (Button) findViewById(R.id.course_for_student_button);
            Button myAbsence        = (Button) findViewById(R.id.absence_for_student_button);
            Button myHistoryButton  = (Button) findViewById(R.id.history_for_student_button);

            myCourseButton.setOnClickListener(this);
            myAbsence.setOnClickListener(this);
            myHistoryButton.setOnClickListener(this);
        }
        else {
            studentLayout.setVisibility(View.GONE);
            Button myCourseButton   = (Button) findViewById(R.id.course_for_teacher_button);
            Button myAbsence        = (Button) findViewById(R.id.absence_for_teacher_button);
            Button myHistoryButton  = (Button) findViewById(R.id.history_for_teacher_button);

            myCourseButton.setOnClickListener(this);
            myAbsence.setOnClickListener(this);
            myHistoryButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_for_student_button:
                break;
            case R.id.absence_for_student_button:
                break;
            case R.id.history_for_student_button:
                break;
            case R.id.course_for_teacher_button:
                break;
            case R.id.absence_for_teacher_button:
                break;
            case R.id.history_for_teacher_button:
                break;
        }
    }

}