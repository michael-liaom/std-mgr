package com.weijie.studentworkmanagementsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;

/**
 * Created by weijie on 2018/7/8.
 */
public class MenuTeacherActivity extends AppCompatActivity implements View.OnClickListener{
    private Button studentImformationbutton, dormimformationbutton,
            studentdailybutton, studentrwdpnybutton, logoutbutton;
    private AlertDialog.Builder builder;
    private Intent intent = new Intent();
    private int i=0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuteacher);
        studentImformationbutton = (Button) findViewById(R.id.studentImformationbutton);
        studentImformationbutton.setOnClickListener(this);
        //在学生信息管理按钮设置监听器

        dormimformationbutton = (Button) findViewById(R.id.dormimformationbutton);
        dormimformationbutton.setOnClickListener(this);
        //在宿舍信息按钮设置监听器
        studentdailybutton = (Button) findViewById(R.id.studentdailybutton);
        studentdailybutton.setOnClickListener(this);
        //在学生日常按钮设置监听器
        studentrwdpnybutton = (Button) findViewById(R.id.studentrwdpnybutton);
        studentrwdpnybutton.setOnClickListener(this);
        //在学生奖惩按钮设置监听器
        logoutbutton = (Button) findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(this);
        //退出登陆设置监听器
        builder = new AlertDialog.Builder(MenuTeacherActivity.this);
        //实例化对象
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //设置对话框图标
        builder.setTitle("提示");
        //设置对话框标题
        builder.setMessage("确认退出登陆？");
        //设置对话框提示文本
        builder.setPositiveButton("返回",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){

            }
        });//监听右侧按钮
        builder.setNegativeButton("确认",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which){
                intent.setClass(MenuTeacherActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        });//监听左侧按钮
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.studentImformationbutton:

                intent.setClass(MenuTeacherActivity.this, StuInfoActivity.class);
                startActivity(intent);
                break;
            //进入学籍查询页面
            case R.id.dormimformationbutton:
                intent.setClass(MenuTeacherActivity.this, DormActivity.class);
                startActivity(intent);
                break;

            case R.id.studentdailybutton:
                intent.setClass(MenuTeacherActivity.this, DailyActivity.class);
                startActivity(intent);
                break;

            case R.id.studentrwdpnybutton:
                intent.setClass(MenuTeacherActivity.this, StudentrwActivity.class);
                startActivity(intent);
                break;
            case R.id.logoutbutton:
                builder.show();
                break;//显示对话框
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            int i=0;
            builder.show();
        }
        return super.onKeyDown(keyCode,event);
    }

}
