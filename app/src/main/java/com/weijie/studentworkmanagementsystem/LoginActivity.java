package com.weijie.studentworkmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton,resetButton;
    private EditText usernameEditText,passwdEditText;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);   //将登陆按钮绑上监听器
        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this); //将重置重置按钮绑上监听器
        usernameEditText=(EditText) findViewById(R.id.usernameEditText);
        usernameEditText.setOnClickListener(this);
        passwdEditText=(EditText) findViewById(R.id.passwdEditText);
        passwdEditText.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.loginButton:
              /*  String str = usernameEditText.getText().toString();
                if(str.length()<=5||str.length()>=12)
                    Toast.makeText(LoginActivity.this,"用户名格式不对！",Toast.LENGTH_SHORT)
                            .show();
                else if(str.length()==0)
                    Toast.makeText(LoginActivity.this,"请输入用户名！",Toast.LENGTH_SHORT)
                            .show();
                else if(passwdEditText.getText().toString().length()==0)
                    Toast.makeText(LoginActivity.this,"请输入密码！",Toast.LENGTH_SHORT)
                            .show();*/
                //else {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,MenuTeacherActivity.class);
                    startActivity(intent);
                //}
                break;
            case R.id.resetButton:
                usernameEditText.setText("");
                passwdEditText.setText("");
                break;

        }
    }

}
