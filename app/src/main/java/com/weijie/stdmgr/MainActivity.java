package com.weijie.stdmgr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;


/**
 * Created by weijie on 2018/5/7.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    final static int REQUEST_FOR_LOGIN      = 1;
    final static int REQUEST_FOR_HOSTARESS  = 2;
    final static String REQUEST_PARAM          = "REQUEST_PARAM";
    final static String REQUEST_PRAM_EXCHANGE   = "student_exchange";
    final static String REQUEST_STUDENT_DATA    = "student_data";

    final DBHandler dbHandler = new DBHandler(this);

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserDataUtils authUserDataUtils;
    private AuthUserData authUser;

    private LinearLayout initialLayout, teacherLayout, studentLayout;
    private Button logoutButton;
    private ProgressBar progressBar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGIN_SUCCESS) {
                responseLoginSuccess();
            }
        }
        else if (requestCode == REQUEST_FOR_HOSTARESS) {
            //if (resultCode == HostSettingActivity.RETURN_CODE_OK) {
                jdbcMgrUtils.connect(authUser.hostName, dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
                showBusyProgress(true);
            //}
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar   = (ProgressBar) findViewById(R.id.progress_bar);

        initData();
        initControls();
    }

    private void initData() {
        jdbcMgrUtils        = JdbcMgrUtils.getInstance();
        authUserDataUtils = AuthUserDataUtils.getInstance();
        authUser            = MyApplication.getInstance().authUser;

        jdbcMgrUtils.connect(authUser.hostName, dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
        showBusyProgress(true);
    }

    private void initControls() {
        initialLayout = (LinearLayout) findViewById(R.id.initial_layout);

        teacherLayout = (LinearLayout) findViewById(R.id.teacher_layout);
        Button teacherExchangeButton  = (Button) findViewById(R.id.exchange_teacher_button);
        Button teacherClassButton   = (Button) findViewById(R.id.reward_lend_teacher_button);
        Button teacherAbsenceButton = (Button) findViewById(R.id.absence_teacher_button);
        Button teacherStudentDataButton  = (Button) findViewById(R.id.studentdata_teacher_button);
        teacherExchangeButton.setOnClickListener(this);
        teacherClassButton.setOnClickListener(this);
        teacherAbsenceButton.setOnClickListener(this);
        teacherStudentDataButton.setOnClickListener(this);

        studentLayout = (LinearLayout) findViewById(R.id.student_layout);
        Button studentExchangeButton  = (Button) findViewById(R.id.exchange_student_button);
        Button studentRalButton = (Button) findViewById(R.id.reward_lend_student_button);
        Button studentAbsenceButton = (Button) findViewById(R.id.myabsence_student_button);
        Button studentMyDataButton  = (Button) findViewById(R.id.mydata_student_button);
        studentExchangeButton.setOnClickListener(this);
        studentRalButton.setOnClickListener(this);
        studentAbsenceButton.setOnClickListener(this);
        studentMyDataButton.setOnClickListener(this);


        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);

        teacherLayout.setVisibility(View.GONE);
        studentLayout.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.studentdata_teacher_button: {
                Intent intent = new Intent(this, ClassListActivity.class);
                intent.putExtra(REQUEST_PARAM, REQUEST_STUDENT_DATA);
                startActivity(intent);
                break;
            }
                /*
            case R.id.myteacher_student_button:
                startActivity(new Intent(this, TeacherListActivity.class));
                break;
                */
            case R.id.reward_lend_teacher_button:
            case R.id.reward_lend_student_button:
                startActivity(new Intent(this, RalMenuActivity.class));
                break;
            case R.id.exchange_teacher_button: {
                Intent intent = new Intent(this, ClassListActivity.class);
                intent.putExtra(REQUEST_PARAM, REQUEST_PRAM_EXCHANGE);
                startActivity(intent);
                break;
            }
            case R.id.exchange_student_button:
                startActivity(new Intent(this, ExchangeSubjectListActivity.class));
                break;
            case R.id.myabsence_student_button:
            case R.id.absence_teacher_button:
                startActivity(new Intent(this, AbsenceFormListActivity.class));
                break;
            case R.id.mydata_student_button:{
                Intent intent = new Intent(this, PersonDetailActivity.class);
                intent.putExtra(AuthUserData.COL_GENRE, authUser.genre);
                if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                    intent.putExtra(StudentData.COL_ID, authUser.studend_id);
                }
                else {
                    intent.putExtra(StudentData.COL_ID, authUser.teacher_id);
                }
                startActivity(intent);
                break;
            }
            case R.id.logout_button://主菜单的登陆按钮
                responseLogout();
                break;
        }
    }

    private void checkAuth () {
        if (authUser.name.length() > 0 &&
                authUser.password.length() > 0) {
            authUserDataUtils.requestLogin(authUser.name, authUser.password,
                    dbHandler, AuthUserDataUtils.TAG_LOGIN);
            showBusyProgress(true);
        }
        else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent,REQUEST_FOR_LOGIN);
    }

    private void responseLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.title_logout_confirmation)
                .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authUser.reset();
                        startLoginActivity();
                    }
                })
                .setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }

    private void responseLoginSuccess() {
        initialLayout.setVisibility(View.GONE);
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            studentLayout.setVisibility(View.VISIBLE);
            teacherLayout.setVisibility(View.GONE);
        }
        else {
            studentLayout.setVisibility(View.GONE);
            teacherLayout.setVisibility(View.VISIBLE);
        }
        logoutButton.setVisibility(View.VISIBLE);
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
        }
    }



    private static class DBHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        DBHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final MainActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                String tag = (String) msg.obj;
                if (tag != null)
                    if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                        switch (tag) {
                            case JdbcMgrUtils.TAG_DB_CONNECT:
                                activity.checkAuth();
                                break;
                            case AuthUserDataUtils.TAG_LOGIN:
                                activity.responseLoginSuccess();
                                break;
                        }
                        activity.showBusyProgress(false);
                    }
                    else {//msg.what == DatabaseMgrUtils.DB_REQUEST_FAILURE
                        switch (tag) {
                            case JdbcMgrUtils.TAG_DB_CONNECT: {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_connect_failure)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(activity, HostSettingActivity.class);
                                                activity.startActivityForResult(intent, REQUEST_FOR_HOSTARESS);
                                            }
                                        });
                                builder.show();
                                break;
                            }
                            case AuthUserDataUtils.TAG_LOGIN:
                                activity.startLoginActivity();
                                break;
                            default: {
                                String message = activity.getResources()
                                        .getString(R.string.message_db_operation_failure);
                                Toast.makeText(activity, message, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                        activity.showBusyProgress(false);
                    }
            }
            super.handleMessage(msg);
        }
    }
}
