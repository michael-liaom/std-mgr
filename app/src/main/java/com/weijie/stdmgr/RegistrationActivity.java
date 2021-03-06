package com.weijie.stdmgr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by weijie on 2018/5/30.
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    final static int STUDENT_CODE_LEN   = 10;
    final static int TEACHER_CODE_LEN   = 4;
    final static int MIN_INVATION_LEN   = 6;
    final static int RESULT_CODE_REGISTRATION_CANCEL  = 0;
    final static int RESULT_CODE_REGISTRATION_SUCCESS = 1;

    private AuthUserData authUser;
    private AuthUserDataUtils authUserDataUtils;

    private LinearLayout mainView;
    private EditText nameEditText,
            passwdEditText,
            repeatEditText,
            codeEditText,
            regCodeEditText;
    private RadioButton studentRadioButton;
    private Button registerButton;
    private ProgressBar progressBar;
    private String name, password;

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            setResult(RESULT_CODE_REGISTRATION_CANCEL);
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initData();
        initControls();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        authUserDataUtils = AuthUserDataUtils.getInstance();
    }

    private void initControls() {
        mainView        = (LinearLayout)findViewById(R.id.main_view);
        nameEditText    = (EditText)    findViewById(R.id.name_edit_text);
        passwdEditText  = (EditText)    findViewById(R.id.password_edit_text);
        repeatEditText  = (EditText)    findViewById(R.id.repeat_edit_text);
        codeEditText    = (EditText)    findViewById(R.id.code_edit_text);
        regCodeEditText  = (EditText)    findViewById(R.id.reg_code_edit_text);

        registerButton  = (Button)      findViewById(R.id.registration_student_button);
        studentRadioButton
                = (RadioButton) findViewById(R.id.student_radio_utton);
        progressBar     = (ProgressBar) findViewById(R.id.progress_bar);

        nameEditText.setText(authUser.name);

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    EditText editText = (EditText)view;
                    if (editText.getText().toString().length() < AuthUserData.MIN_NAME_LEN) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.message_input_name_invalid,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        passwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    EditText editText = (EditText)view;
                    if (editText.getText().toString().length() < AuthUserData.MIN_PASSWORD_LEN) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.message_input_password_invalid,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        repeatEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    EditText editText = (EditText)view;
                    if (editText.getText().toString().length() < AuthUserData.MIN_PASSWORD_LEN) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.message_input_password_invalid,
                                Toast.LENGTH_LONG).show();
                    }
                    if(!editText.getText().toString().equals(passwdEditText.getText().toString())) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.message_input_password_mismatch,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        codeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    EditText editText = (EditText)view;
                    if (RegistrationActivity.this.studentRadioButton.isChecked()) {
                        if (editText.getText().toString().length() != RegistrationActivity.STUDENT_CODE_LEN) {
                            Toast.makeText(RegistrationActivity.this,
                                    R.string.message_input_student_code_invalid,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        if (editText.getText().toString().length() != RegistrationActivity.TEACHER_CODE_LEN) {
                            Toast.makeText(RegistrationActivity.this,
                                    R.string.message_input_teacher_code_invalid,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });
        regCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    EditText editText = (EditText)view;
                    if (editText.getText().toString().length() < RegistrationActivity.MIN_INVATION_LEN) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.message_input_invation_invalid,
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.registration_student_button:
                if (isInputValid()) {
                    authUserDataUtils.requestCheckUserNameValid(nameEditText.getText().toString(),
                            dbHandler, AuthUserDataUtils.TAG_CHECK_NAME_VALID);
                    showRegistrationProgress(true);
                }
                else {
                    Toast.makeText(RegistrationActivity.this,
                            R.string.message_registartion_half_baked,
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private boolean isInputValid() {
        if (nameEditText.getText().toString().length() < AuthUserData.MIN_NAME_LEN ||
                passwdEditText.getText().toString().length() < AuthUserData.MIN_PASSWORD_LEN ||
                !passwdEditText.getText().toString().equals(repeatEditText.getText().toString()) ||
                regCodeEditText.getText().toString().length() < MIN_INVATION_LEN) {
            return false;
        }

        return true;
    }

    private void checkInvation() {
        String genre;

        if (studentRadioButton.isChecked()) {
            genre = AuthUserData.GENRE_STUDENT;
        }
        else {
            genre = AuthUserData.GENRE_TEACHER;
        }
        int unifiedCode = Integer.valueOf(codeEditText.getText().toString());
        authUserDataUtils.requestCheckInviationValid(unifiedCode, regCodeEditText.getText().toString(), genre,
                dbHandler, AuthUserDataUtils.TAG_CHECK_INVATION_VALID);
    }

    private void resistraton() {
        String genre;

        if (studentRadioButton.isChecked()) {
            genre = AuthUserData.GENRE_STUDENT;
        }
        else {
            genre = AuthUserData.GENRE_TEACHER;
        }
        int unifiedCode = Integer.valueOf(codeEditText.getText().toString());

        name        = nameEditText.getText().toString();
        password    = passwdEditText.getText().toString();

        authUserDataUtils.requestRegistration(unifiedCode, regCodeEditText.getText().toString(),
                genre, name, password, dbHandler, AuthUserDataUtils.TAG_REGISTRATION);

    }

    private void showRegistrationProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            registerButton.setEnabled(false);
            mainView.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            registerButton.setEnabled(true);
            mainView.setEnabled(true);
        }
    }

    private void tryLogin() {
        authUserDataUtils.requestLogin(name, password, dbHandler, AuthUserDataUtils.TAG_LOGIN);
        showRegistrationProgress(true);
        progressBar.setProgress(50);
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<RegistrationActivity> mActivity;

        DBHandler(RegistrationActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final RegistrationActivity activity = mActivity.get();
            if (activity != null) {
                // ...
                String tag = (String) msg.obj;
                if (tag != null)
                    switch (tag) {
                        case AuthUserDataUtils.TAG_CHECK_NAME_VALID:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.progressBar.setProgress(30);
                                activity.checkInvation();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_name_invalid,
                                        Toast.LENGTH_LONG).show();
                                activity.showRegistrationProgress(false);
                            }
                            break;
                        case AuthUserDataUtils.TAG_CHECK_INVATION_VALID:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.progressBar.setProgress(60);
                                activity.resistraton();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_invation_invalid,
                                        Toast.LENGTH_LONG).show();
                                activity.showRegistrationProgress(false);
                            }
                            break;
                        case AuthUserDataUtils.TAG_REGISTRATION:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.progressBar.setProgress(99);
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_registration_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        activity.tryLogin();
                                    }
                                });
                                builder.show();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_registration_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showRegistrationProgress(false);
                            break;
                        case AuthUserDataUtils.TAG_LOGIN:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.authUser.name = activity.name;
                                activity.authUser.password = activity.password;
                                activity.authUser.backupToLocal();

                                activity.setResult(RESULT_CODE_REGISTRATION_SUCCESS);
                                activity.finish();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_login_success,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showRegistrationProgress(false);
                            break;
                    }
            }
            super.handleMessage(msg);
        }
    }
}
