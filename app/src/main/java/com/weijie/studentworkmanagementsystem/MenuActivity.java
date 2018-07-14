package com.weijie.studentworkmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;



/**
 * Created by weijie on 2018/7/7.
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
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
        setContentView(R.layout.activity_menu);

        loginbutton = (Button) findViewById(R.id. loginbutton);
        loginbutton.setOnClickListener(this);
        //在登陆按钮管理设置监听器

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginbutton://主菜单的登陆按钮
            Intent intent = new Intent();
            intent.setClass(MenuActivity.this, LoginActivity.class);
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

}
