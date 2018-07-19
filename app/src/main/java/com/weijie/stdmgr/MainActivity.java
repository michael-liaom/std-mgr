package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;


/**
 * Created by weijie on 2018/7/7.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final static int REQUEST_FOR_LOGIN = 1;
    final DBHandler dbHandler = new DBHandler(this);

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserUtils authUserUtils;
    private AuthUserData authUser;

    private Button logoutButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGIN_SUCCESS) {
                logoutButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initControls();
    }

    private void initData() {
        jdbcMgrUtils        = JdbcMgrUtils.getInstance();
        authUserUtils = AuthUserUtils.getInstance();
        authUser            = MyApplication.getInstance().authUser;

        jdbcMgrUtils.connect(dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
    }

    private void initControls() {
        Button absenceButton = (Button) findViewById(R.id.absence_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        absenceButton.setOnClickListener(this);
        logoutButton.setVisibility(View.INVISIBLE);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.absence_button:
                startActivity(new Intent(this, AbsenceMainActivity.class));
                break;
            case R.id.logout_button://主菜单的登陆按钮
                authUser.reset();
                startLoginActivity();
                break;
        }
    }

    private void checkAuth () {
        if (authUser.name.length() > 0 &&
                authUser.password.length() > 0) {
            authUserUtils.requestLogin(authUser.name, authUser.password, dbHandler, AuthUserUtils.TAG_LOGIN);
        }
        else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent,REQUEST_FOR_LOGIN);
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
                            case AuthUserUtils.TAG_LOGIN:
                                activity.logoutButton.setVisibility(View.VISIBLE);
                                break;
                            case AuthUserUtils.TBL_STUDENT_REGISTATION:
                                break;
                        }
                    }
                    else {//msg.what == DatabaseMgrUtils.DB_REQUEST_FAILURE
                        switch (tag) {
                            case JdbcMgrUtils.TAG_DB_CONNECT: {
                                String message = activity.getResources()
                                        .getString(R.string.message_db_connect_failure);
                                Toast.makeText(activity, message, Toast.LENGTH_LONG)
                                        .show();
                                break;
                            }
                            case AuthUserUtils.TAG_LOGIN:
                                activity.startLoginActivity();
                                break;
                            default: {
                                String message = activity.getResources()
                                        .getString(R.string.message_db_operation_failure);
                                Toast.makeText(activity, message, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
            }
            super.handleMessage(msg);
        }
    }
}