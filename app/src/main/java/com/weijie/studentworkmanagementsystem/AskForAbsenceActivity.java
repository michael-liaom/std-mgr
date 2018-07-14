package com.weijie.studentworkmanagementsystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by weijie on 2018/7/8.
 */
public class AskForAbsenceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText beginDateEditText,
            beginTimeEditText;
    private Button commiteButton;
    private AbsenceApplyData absenceApplyData;

    private MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_absence);

        initData();
        initControl();
    }

    private void initData() {
        absenceApplyData = new AbsenceApplyData();
    }

    private void initControl() {
        beginDateEditText   = (EditText) findViewById(R.id.begin_date_edit_text);
        beginTimeEditText   = (EditText) findViewById(R.id.begin_time_edit_text);
        commiteButton       = (Button) findViewById(R.id.commit_button);


        beginDateEditText.setOnClickListener(this);
        beginTimeEditText.setOnClickListener(this);
        commiteButton.setOnClickListener(this);
        beginDateEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        beginTimeEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        beginDateEditText.setInputType(InputType.TYPE_NULL);
        beginTimeEditText.setInputType(InputType.TYPE_NULL);
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DATE, i2);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            beginDateEditText.setText(format.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, i);
            calendar.set(Calendar.MINUTE, i1);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            beginTimeEditText.setText(format.format(calendar.getTime()));
        }
    };


    @Override
    public void onClick(View v) {
        if (v.equals(beginDateEditText)) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, onDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
        else if (v.equals(beginTimeEditText)) {
            new TimePickerDialog(this, onTimeSetListener,
                    8, 0, true)
                    .show();
        }
        else if (v.equals(commiteButton)) {
            absenceApplyData.begin = toDatetime(beginDateEditText.getText()
                    + " " + beginTimeEditText.getText());
        }
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (v.equals(beginDateEditText)) {
                if (hasFocus) {
                    new DatePickerDialog(AskForAbsenceActivity.this, onDateSetListener, 2018,
                            1, 1).show();
                }
            }
            else if (v.equals(beginTimeEditText)) {
                if (hasFocus) {
                    new TimePickerDialog(AskForAbsenceActivity.this, onTimeSetListener,
                            16, 1, true)
                            .show();
                }
            }
        }
    }

    public static Date toDatetime(String date_str){
        SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        Date date;
        try {
            date = dbSdf.parse(date_str);
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
            date = new Date(0);
        }

        return date;
    }
}
