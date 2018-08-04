package com.weijie.stdmgr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by weijie on 2018/5/5.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    final static int RESULT_CODE_LOGIN_SUCCESS  = 1;
    final static int RESULT_CODE_LOGIN_NONE     = 0;

    final private static int REQUEST_FOR_REGISTRATION = 1;

    private AuthUserData authUser;

    private EditText nameEditText,
            passwdEditText;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_REGISTRATION) {
            if (resultCode == RegistrationActivity.RESULT_CODE_REGISTRATION_SUCCESS) {
                setResult(RESULT_CODE_LOGIN_SUCCESS);
                finish();
            }
        }
    }

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
        nameEditText    = (EditText)    findViewById(R.id.user_name_edit_text);
        passwdEditText  = (EditText)    findViewById(R.id.pass_word_edit_text);
        loginButton     = (Button)      findViewById(R.id.login_button);
        registerButton  = (Button)      findViewById(R.id.registration_student_button);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);

        nameEditText.setText(authUser.name);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login_button:
                AuthUserDataUtils.getInstance()
                        .requestLogin(nameEditText.getText().toString(),
                        passwdEditText.getText().toString(),
                        dbHandler, AuthUserDataUtils.TAG_LOGIN);
                showLoginProgress(true);
                break;
            case R.id.registration_student_button:
                startActivityForResult(new Intent(this,
                                RegistrationActivity.class), REQUEST_FOR_REGISTRATION);
                break;

        }
    }

    private void showLoginProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
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
                if (tag != null) {
                    switch (tag) {
                        case AuthUserDataUtils.TAG_LOGIN:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.authUser.name = activity.nameEditText.getText()
                                        .toString();
                                activity.authUser.password = activity.passwdEditText.getText()
                                        .toString();
                                activity.authUser.backupToLocal();
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_login_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.setResult(RESULT_CODE_LOGIN_SUCCESS);
                                                activity.finish();
                                            }
                                        });
                                builder.show();
                            } else {
                                Toast.makeText(activity, R.string.message_db_login_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showLoginProgress(false);
                            break;
                    }
                }
            }
            super.handleMessage(msg);
        }
    }
}
