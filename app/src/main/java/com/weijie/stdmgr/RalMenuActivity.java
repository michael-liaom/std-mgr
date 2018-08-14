package com.weijie.stdmgr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class RalMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private AuthUserData authUser;

    private LinearLayout initialLayout, teacherLayout, studentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_menu);

        authUser            = MyApplication.getInstance().authUser;
        initControls();
    }

    private void initControls() {
        initialLayout = (LinearLayout) findViewById(R.id.initial_layout);

        teacherLayout = (LinearLayout) findViewById(R.id.teacher_layout);
        Button teacherRalButton   = (Button) findViewById(R.id.ral_teacher_button);
        Button teachApplyButton   = (Button) findViewById(R.id.check_teacher_button);
        teacherRalButton.setOnClickListener(this);
        teachApplyButton.setOnClickListener(this);

        studentLayout = (LinearLayout) findViewById(R.id.student_layout);
        Button studentApplyButton   = (Button) findViewById(R.id.apply_student_button);
        Button studentCheckButton   = (Button) findViewById(R.id.check_student_button);
        studentApplyButton.setOnClickListener(this);
        studentCheckButton.setOnClickListener(this);

        if (authUser.studend_id > 0) {
            teacherLayout.setVisibility(View.GONE);
            studentLayout.setVisibility(View.VISIBLE);
        }
        else {
            teacherLayout.setVisibility(View.VISIBLE);
            studentLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ral_teacher_button:
                startActivity(new Intent(this, RalListActivity.class));
                break;
            case R.id.check_teacher_button:
                startActivity(new Intent(this, RalApplyListActivity.class));
                break;
            case R.id.apply_student_button:
                startActivity(new Intent(this, RalListActivity.class));
                break;
            case R.id.check_student_button:
                startActivity(new Intent(this, RalApplyListActivity.class));
                break;
        }
    }
}
