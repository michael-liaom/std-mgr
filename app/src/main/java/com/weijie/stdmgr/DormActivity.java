package com.weijie.stdmgr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by weijie on 2018/7/8.
 */
public class DormActivity extends AppCompatActivity implements View.OnClickListener {
    private DormData dormData;
    private TextView roomNoTextView,
            aviliableNumberTextView;
    private ArrayList<TextView> studentNoTextViews;
    ArrayList<TextView> studentNameTextViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm);


        studentNameTextViews = new ArrayList<>();
        studentNoTextViews = new ArrayList<>();

        dormData = new DormData();
        initControls();
        dormData.read();
        refreshDisp();

    }
    private void initControls(){
        roomNoTextView = (TextView) findViewById(R.id.roomNoTextView);
        aviliableNumberTextView = (TextView) findViewById(R.id.aviliableNumTextView);

        TextView studentNoTextView1 = (TextView) findViewById(R.id.studNoTextView1);
        studentNoTextViews.add(studentNoTextView1);
        TextView studentNameTextView1 = (TextView) findViewById(R.id.studNameTextView1);
        studentNameTextViews.add(studentNameTextView1);

        TextView studentNoTextView2 = (TextView) findViewById(R.id.studNoTextView2);
        studentNoTextViews.add(studentNoTextView2);
        TextView studentNameTextView2 = (TextView) findViewById(R.id.studNameTextView2);
        studentNameTextViews.add(studentNameTextView2);

        TextView studentNoTextView3 = (TextView) findViewById(R.id.studNoTextView3);
        studentNoTextViews.add(studentNoTextView3);
        TextView studentNameTextView3 = (TextView) findViewById(R.id.studNameTextView3);
        studentNameTextViews.add(studentNameTextView3);

        TextView studentNoTextView4 = (TextView) findViewById(R.id.studNoTextView4);
        studentNoTextViews.add(studentNoTextView4);
        TextView studentNameTextView4 = (TextView) findViewById(R.id.studNameTextView4);
        studentNameTextViews.add(studentNameTextView4);

        TextView studentNoTextView5 = (TextView) findViewById(R.id.studNoTextView5);
        studentNoTextViews.add(studentNoTextView5);
        TextView studentNameTextView5 = (TextView) findViewById(R.id.studNameTextView5);
        studentNameTextViews.add(studentNameTextView5);

        TextView studentNoTextView6 = (TextView) findViewById(R.id.studNoTextView6);
        studentNoTextViews.add(studentNoTextView6);
        TextView studentNameTextView6 = (TextView) findViewById(R.id.studNameTextView6);
        studentNameTextViews.add(studentNameTextView6);
    }
    public void onClick(View v) {

    }
    private void refreshDisp(){
        roomNoTextView.setText(dormData.getRoomNo());
        aviliableNumberTextView.setText(Integer.toString(dormData.getAvailiableNumber()));

        for (int idx = 0; idx < dormData.getArrayListStud().size(); idx++) {

            TextView textView;

            textView = studentNoTextViews.get(idx);
            textView.setText( dormData.getArrayListStud().get(idx).no);

            textView = studentNameTextViews.get(idx);
            textView.setText( dormData.getArrayListStud().get(idx).name);
        }
}
}
