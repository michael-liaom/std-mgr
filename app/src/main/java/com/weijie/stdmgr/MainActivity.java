package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
    private AuthUserMgrUtils authUserMgrUtils;
    private AuthUserData authUser;

    private Button logoutButton;

    //if
    /*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmenu);
    }
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initControls();
    }

    private void initData() {
        jdbcMgrUtils        = JdbcMgrUtils.getInstance();
        authUserMgrUtils    = AuthUserMgrUtils.getInstance();
        authUser            = new AuthUserData(this);

        jdbcMgrUtils.connect(dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
    }

    private void initControls() {
        logoutButton = (Button) findViewById(R.id.logout_button);

        logoutButton.setVisibility(View.INVISIBLE);

        logoutButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_LOGIN) {
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_button://主菜单的登陆按钮
                startLoginActivity();
                break;
        }
    }

    private void checkAuth () {
        if (authUser.name.length() > 0 &&
                authUser.password.length() > 0) {
            authUserMgrUtils.requestLogin(authUser.name, authUser.password, dbHandler, AuthUserMgrUtils.TAG_LOGIN);
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
                            case AuthUserMgrUtils.TAG_LOGIN:
                                activity.logoutButton.setVisibility(View.VISIBLE);
                                break;
                            case AuthUserMgrUtils.TBL_STUDENT_REGISTATION:
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
                            case AuthUserMgrUtils.TAG_LOGIN:
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
