package com.weijie.stdmgr;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;


/**
 * Created by weijie on 2018/7/7.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    final static int REQUEST_FOR_LOGIN      = 1;
    final static int REQUEST_FOR_HOSTARESS  = 2;
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
            if (resultCode == HostSettingActivity.RETURN_CODE_OK) {
                jdbcMgrUtils.connect(authUser.hostName, dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
                showLoginProgress(true);
            }
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
        showLoginProgress(true);
    }

    private void initControls() {
        initialLayout = (LinearLayout) findViewById(R.id.initial_layout);
        teacherLayout = (LinearLayout) findViewById(R.id.teacher_layout);
        studentLayout = (LinearLayout) findViewById(R.id.student_layout);

        Button darily = (Button) findViewById(R.id.daily_for_student_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        teacherLayout.setVisibility(View.GONE);
        studentLayout.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

        darily.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.daily_for_student_button:
                startActivity(new Intent(this, DailyMainActivity.class));
                break;
            case R.id.daily_for_teacher_button:
                startActivity(new Intent(this, DailyMainActivity.class));
                break;
            case R.id.logout_button://主菜单的登陆按钮
                responseLogout();
                break;
        }
    }

    private void checkAuth () {
        if (authUser.name.length() > 0 &&
                authUser.password.length() > 0) {
            authUserDataUtils.requestLogin(authUser.name, authUser.password, dbHandler, AuthUserDataUtils.TAG_LOGIN);
            showLoginProgress(true);
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
        authUser.reset();
        startLoginActivity();
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

    private void showLoginProgress(boolean isBussy) {
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
                        activity.showLoginProgress(false);
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
                        activity.showLoginProgress(false);
                    }
            }
            super.handleMessage(msg);
        }
    }
}
