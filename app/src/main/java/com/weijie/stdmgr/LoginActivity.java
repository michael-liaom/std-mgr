package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private AuthUserData authUser;

    private EditText nameEditText,
            passwdEditText;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initData();
        initControls();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
    }

    private void initControls() {
        nameEditText    = (EditText) findViewById(R.id.user_name_edit_text);
        passwdEditText  = (EditText) findViewById(R.id.pass_word_edit_text);
        loginButton     = (Button) findViewById(R.id.login_button);
        registerButton  = (Button) findViewById(R.id.register_button);

        nameEditText.setText(authUser.name);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login_button:
                AuthUserMgrUtils.getInstance()
                        .requestLogin(nameEditText.getText().toString(),
                        passwdEditText.getText().toString(),
                        dbHandler, AuthUserMgrUtils.TAG_LOGIN);
                break;
            case R.id.register_button:
                break;

        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        DBHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final LoginActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                String tag = (String) msg.obj;
                if (tag != null)
                    if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                        switch (tag) {
                            case AuthUserMgrUtils.TAG_LOGIN:
                                activity.finish();
                                break;
                        }
                    }
                    else {
                        String message = activity.getResources()
                                .getString(R.string.message_db_login_failure);
                        Toast.makeText(activity, message, Toast.LENGTH_LONG)
                                .show();
                    }
            }
            super.handleMessage(msg);
        }
    }
}
