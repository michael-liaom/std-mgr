package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by weijie on 2018/7/7.
 */
public class StuInfoActivity extends AppCompatActivity implements View.OnClickListener {
   private Button returnbutton;
    StuInfoActivity stuInfoActivity = new StuInfoActivity();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentinfo);
        returnbutton = (Button) findViewById(R.id.returnbutton);
        returnbutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==returnbutton){
            Intent intent = new Intent();
            intent.setClass(StuInfoActivity.this,MenuTeacherActivity.class);
            startActivity(intent);


        }
    }
}
