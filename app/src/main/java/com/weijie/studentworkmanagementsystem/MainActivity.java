package com.weijie.studentworkmanagementsystem;

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
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    final DBHandler dbHandler = new DBHandler(this);

    private JdbcMgrUtils jdbcMgrUtils;
    Button loginbutton;
    //if
    /*
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmenu);
    }
    */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        loginbutton = (Button) findViewById(R.id. loginbutton);
        loginbutton.setOnClickListener(this);
        //在登陆按钮管理设置监听器

    }

    private void initData() {
       jdbcMgrUtils = JdbcMgrUtils.getInstance();
       jdbcMgrUtils.connect(dbHandler, JdbcMgrUtils.TAG_DB_CONNECT);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginbutton://主菜单的登陆按钮
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode,event);
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
                                AuthUserMgrUtils.getInstance().
                                        requestFetchStudendRegistration(1,
                                                new StudentData(),
                                                activity.dbHandler,
                                                AuthUserMgrUtils.TBL_STUDENT_REGISTATION);
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
