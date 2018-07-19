package com.weijie.stdmgr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by weijie on 2018/7/8.
 */
public class AbsenceApplyActivity extends AppCompatActivity implements View.OnClickListener {
    final static int REQUEST_CODE_GETTING_CC    = 0;
    final static int RESULT_CODE_APPLY_CANCEL   = 0;
    final static int RESULT_CODE_APPLY_SUCCESS  = 1;
    final DBHandler dbHandler = new DBHandler(this);

    private View inputForm;
    private Spinner applyToSpinner;
    private TextView applyCcTextView;
    private Spinner applyTypeSpinner;
    private EditText beginDateEditText,
            beginTimeEditText,
            daysEditText,
            hoursEditText,
            causeEditText;
    private Button commiteButton;
    private ProgressBar progressBar;
    private Calendar calendar;

    private AuthUserData authUser;
    private ClassData classData;

    private ArrayList<TeacherData> toTeacherList;
    private ArrayList<TeacherData> ccTeacherList;
    private ArrayList<SimplePickItemData> ccPickItems;

    private ArrayAdapter<String> adapterTo, adapterCc;
    private AbsenceApplyData absenceApplyData;

    private MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            Intent intent = new Intent();
            setResult(RESULT_CODE_APPLY_CANCEL, intent);
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_GETTING_CC: {
                ArrayList<Integer> arrayList
                        = data.getIntegerArrayListExtra(SimpleDataPickerActivity.RETURN_EXTRA_DATA_KEY);
                for (SimplePickItemData itemData : ccPickItems) {
                    itemData.setPicked(false);
                    for (Integer integer : arrayList) {
                        if (itemData.getId().longValue() == integer.longValue()) {
                            itemData.setPicked(true);
                            break;
                        }
                    }
                }
                refreshCcDisp();
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_apply);

        initData();
        initControl();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        absenceApplyData = new AbsenceApplyData();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        toTeacherList  = new ArrayList();
        ccTeacherList  = new ArrayList();

        classData = new ClassData();
        ClassDataUtils.getInstance().requestFetchClassData(authUser.studend_id, classData,
                dbHandler, ClassDataUtils.TAG_FETCH_CLASS_DATA);
    }

    private void initApplyToSpinner() {
        toTeacherList.add(classData.masterTeacher);
        toTeacherList.add(classData.assistantTeacher);

        ArrayList<String> arrayListTo = new ArrayList<>();
        for (TeacherData teacherData : toTeacherList) {
            arrayListTo.add(teacherData.name);
        }
        adapterTo = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayListTo);

        applyToSpinner.setAdapter(adapterTo);
        applyToSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void initControl() {
        inputForm           = findViewById(R.id.input_form);
        applyToSpinner      = (Spinner) findViewById(R.id.apply_to_spinner);
        applyCcTextView      = (TextView) findViewById(R.id.apply_cc_text_view);
        applyTypeSpinner    = (Spinner) findViewById(R.id.apply_type_spinner);
        beginDateEditText   = (EditText) findViewById(R.id.begin_date_edit_text);
        beginTimeEditText   = (EditText) findViewById(R.id.begin_time_edit_text);
        daysEditText        = (EditText) findViewById(R.id.days_edit_text);
        hoursEditText       = (EditText) findViewById(R.id.hours_edit_text);
        causeEditText       = (EditText) findViewById(R.id.cause_edit_text);
        progressBar
                = (ProgressBar) findViewById(R.id.progress_bar);
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

        applyToSpinner.requestFocus();
    }

    private void refreshCcDisp() {
        StringBuilder stringCC = new StringBuilder("未设置");
        int selected = 0;
            for (int idx = 0; idx < ccPickItems.size(); idx++) {
                SimplePickItemData itemData = ccPickItems.get(idx);
                if (itemData.isPicked) {
                    if (selected == 0) {
                        stringCC = new StringBuilder(toTeacherList.get(idx).name);
                    }
                    else if (selected == 1) {
                        stringCC.append(",");
                        stringCC.append(toTeacherList.get(idx).name);
                    }
                    selected++;
                }
            if (selected > 2){
                stringCC.append("等 ");
                stringCC.append(Integer.toString(selected));
                stringCC.append("位老师");
            }
        }
        applyCcTextView.setText(stringCC);
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);

        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }
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
        if (    beginDateEditText.getText().length() > 0 &&
                beginTimeEditText.getText().length() > 0 &&
                (daysEditText.getText().length() > 0 ||
                        hoursEditText.getText().length() > 0))
            return true;
        else {
            return  false;
        }
    }

    private void commiteData() {
        int posi;

        posi = applyToSpinner.getSelectedItemPosition();
        absenceApplyData.toTeacherId = toTeacherList.get(posi).id;

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
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case ClassDataUtils.TAG_FETCH_CLASS_DATA:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.initApplyToSpinner();
                            }
                            else {
                                activity.showBusyProgress(false);
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
