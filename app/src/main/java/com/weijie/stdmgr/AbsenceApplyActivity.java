package com.weijie.stdmgr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by weijie on 2018/7/8.
 */
public class AbsenceApplyActivity extends AppCompatActivity implements View.OnClickListener {
    final DBHandler dbHandler = new DBHandler(this);

    private EditText applyToEditText,
            applyCcEditText;
    private Spinner applyTypeSpinner;
    private EditText beginDateEditText,
            beginTimeEditText,
            daysEditText,
            hoursEditText,
            causeEditText;
    private Button commiteButton;
    private Calendar calendar;

    private AbsenceApplyData absenceApplyData;

    private MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_apply);

        initData();
        initControl();
    }

    private void initData() {
        absenceApplyData = new AbsenceApplyData();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
    }

    private void initControl() {
        applyToEditText     = (EditText) findViewById(R.id.apply_to_edit_text);
        applyCcEditText     = (EditText) findViewById(R.id.apply_cc_edit_text);
        applyTypeSpinner    = (Spinner) findViewById(R.id.apply_type_spinner);
        beginDateEditText   = (EditText) findViewById(R.id.begin_date_edit_text);
        beginTimeEditText   = (EditText) findViewById(R.id.begin_time_edit_text);
        daysEditText        = (EditText) findViewById(R.id.days_edit_text);
        hoursEditText       = (EditText) findViewById(R.id.hours_edit_text);
        causeEditText       = (EditText) findViewById(R.id.cause_edit_text);

        commiteButton       = (Button) findViewById(R.id.commit_button);

        beginDateEditText.setOnClickListener(this);
        beginTimeEditText.setOnClickListener(this);
        commiteButton.setOnClickListener(this);
        beginDateEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        beginTimeEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        beginDateEditText.setInputType(InputType.TYPE_NULL);
        beginTimeEditText.setInputType(InputType.TYPE_NULL);

        applyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.apply_absence_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
    });
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd",
                    Locale.getDefault());
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DATE, i2);
            beginDateEditText.setText(format.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm",
                    Locale.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            beginTimeEditText.setText(format.format(calendar.getTime()));
        }
    };


    @Override
    public void onClick(View v) {
        if (v.equals(beginDateEditText)) {
            setDatePickerDialog();
        }
        else if (v.equals(beginTimeEditText)) {
            setTimePickerDialog();
        }
        else if (v.equals(commiteButton)) {
            if (isInputDataVailable()) {
                commiteData();
            }
            else {
                String message = AbsenceApplyActivity.this.getResources()
                        .getString(R.string.absence_apply_is_not_fully_file);
                Toast.makeText(this, message, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private boolean isInputDataVailable() {
        //Please write the code
        if (applyToEditText.getText().length() > 0 &&
                applyCcEditText.getText().length() > 0 &&
                beginDateEditText.getText().length() > 0 &&
                beginTimeEditText.getText().length() > 0 &&
                (daysEditText.getText().length() > 0 ||
                hoursEditText.getText().length() > 0))
            return true;
        else {
            return  false;
        }
    }

    private void commiteData() {
        absenceApplyData.applyTo = applyToEditText.getText().toString();
        absenceApplyData.applyCC = applyCcEditText.getText().toString();
        absenceApplyData.type = applyTypeSpinner.getSelectedItem().toString();

        String string = beginDateEditText.getText().toString()
                + " " + beginTimeEditText.getText().toString();
        absenceApplyData.begin = toDatetime(string);

        float durationInHour = 0;
        if (daysEditText.getText().length() > 0) {
            durationInHour += Integer.getInteger(daysEditText.getText().toString()) * 24;
        }
        if (hoursEditText.getText().length() > 0) {
            durationInHour += Float.parseFloat(hoursEditText.getText().toString());
        }
        absenceApplyData.ending = new Date(absenceApplyData.begin.getTime()
                + (long)(durationInHour * 3600 * 1000));
        absenceApplyData.cause = causeEditText.getText().toString();
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (v.equals(beginDateEditText)) {
                if (hasFocus) {
                    setDatePickerDialog();
                }
            }
            else if (v.equals(beginTimeEditText)) {
                if (hasFocus) {
                    setTimePickerDialog();
                }
            }
        }
    }

    private void setDatePickerDialog() {
        Date now = new Date();
        DatePickerDialog dialog
                = new DatePickerDialog(this,
                onDateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(now.getTime());
        dialog.show();
    }

    private void setTimePickerDialog() {
        new TimePickerDialog(AbsenceApplyActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    Date toDatetime(String date_str){
        SimpleDateFormat dbSdf
                = new SimpleDateFormat("yyyy.MM.dd HH:mm",Locale.getDefault());
        Date date = new Date(0);
        try {
            date = dbSdf.parse(date_str);
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    private static class DBHandler extends Handler {
        private final WeakReference<AbsenceApplyActivity> mActivity;

        DBHandler(AbsenceApplyActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final AbsenceApplyActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS){

                }
                else {//msg.what == DatabaseMgrUtils.DB_REQUEST_FAILURE

                }

                super.handleMessage(msg);
            }
        }
    }
}
