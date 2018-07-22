package com.weijie.stdmgr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
public class AbsenceFormApplyActivity extends AppCompatActivity implements View.OnClickListener {
    final static int REQUEST_CODE_GETTING_CC    = 0;
    final static int RESULT_CODE_APPLY_CANCEL   = 0;
    final static int RESULT_CODE_APPLY_SUCCESS  = 1;
    final static String DATA_PICKER_NOSETTING   = "未设置";

    private View inputForm;
    private TextView applyFrom;
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

    //private ArrayList<TeacherData> toTeacherList;
    private ArrayList<CourseData> ccCourseList;
    private ArrayList<SimplePickItemData> ccPickItems;

    private ArrayAdapter<String> adapterTo, adapterCc;
    private AbsenceFormData absenceFormData;

    private MyOnFocusChangeListener myOnFocusChangeListener = new MyOnFocusChangeListener();

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            setResult(RESULT_CODE_APPLY_CANCEL);
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
                refreshDisp();
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_form_apply);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();

        showBusyProgress(true);
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        absenceFormData = new AbsenceFormData();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //toTeacherList  = new ArrayList();
        ccCourseList   = new ArrayList();
        ccPickItems = new ArrayList<>();


        classData = new ClassData();
        initApplyToListData();
    }

    private void initApplyToListData() {
        ClassDataUtils.getInstance().requestFetchClassData(authUser.studend_id, classData,
                dbHandler, ClassDataUtils.TAG_FETCH_CLASS_DATA);

    }

    private void initApplyCCListData() {
        StudentDataUtils.getInstance()
                .requestFetchStudentCourseData(authUser.studend_id, true,
                        ccCourseList, dbHandler, StudentDataUtils.TAG_FETCH_STUDENT_COURSE);
    }

    private void initApplyToSpinner() {
        //toTeacherList.add(classData.masterTeacher);
        //toTeacherList.add(classData.assistantTeacher);

        ArrayList<String> arrayListTo = new ArrayList<>();
        for (TeacherData teacherData : classData.materList) {
            arrayListTo.add(teacherData.name);
        }
        adapterTo = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayListTo);

        applyToSpinner.setAdapter(adapterTo);
        applyToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        if (absenceFormData.toTeacherId > 0) {
            for (int idx = 0; idx < classData.materList.size(); idx++) {
                TeacherData teacherData = classData.materList.get(idx);
                if (teacherData.id == absenceFormData.id) {
                    applyToSpinner.setSelection(idx);
                }
            }
        }
    }

    private void RefreshApplyCcDataPicker() {
        ccPickItems.clear();
        for (CourseData courseData : ccCourseList) {
            boolean isPicked = false;
            for (CourseData courseDataApp : absenceFormData.ccList) {
                if (courseDataApp.id == courseData.id) {
                    isPicked = true;
                }
            }
            SimplePickItemData pickItemData
                    = new SimplePickItemData(courseData.name + " / " + courseData.teacherName,
                    courseData.id, isPicked);
            ccPickItems.add(pickItemData);
        }

        refreshDisp();
    }

    private void initControls() {
        TextView menuTextView = (TextView) findViewById(R.id.menu_text_view);
        applyFrom           = (TextView) findViewById(R.id.apply_from_text_view);
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

        LinearLayout ccLayout = (LinearLayout) findViewById(R.id.cc_layout);
        ccLayout.setOnClickListener(this);
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

        refreshDisp();

        menuTextView.setFocusable(true);
        menuTextView.setFocusableInTouchMode(true);
        menuTextView.requestFocus();
    }

    private void refreshDisp() {
        if (absenceFormData.studentId > 0) {
            applyFrom.setText(absenceFormData.studentName);
        }
        else {
            applyFrom.setText(authUser.studentData.name);
        }

        StringBuilder stringCC = new StringBuilder(DATA_PICKER_NOSETTING);
        int selected = 0;

        for (int idx = 0; idx < ccPickItems.size(); idx++) {
            SimplePickItemData itemData = ccPickItems.get(idx);
            if (itemData.isPicked) {
                if (selected == 0) {
                    stringCC = new StringBuilder(ccCourseList.get(idx).teacherName);
                } else if (selected == 1) {
                    stringCC.append(", ");
                    stringCC.append(ccCourseList.get(idx).teacherName);
                }
                selected++;
            }
        }
        if (selected > 2){
            stringCC.append("等 ");
            stringCC.append(Integer.toString(selected));
            stringCC.append("位老师");
        }
        applyCcTextView.setText(stringCC);
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);

        }
        else {
            progressBar.setVisibility(View.GONE);
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
        switch (v.getId()) {
            case R.id.cc_layout:
                Intent intent = new Intent(this,
                        SimpleDataPickerActivity.class);
                SimpleDataPickerActivity.Builder builder
                        = new SimpleDataPickerActivity.Builder("抄送任课老师",
                        "", ccPickItems, true);
                intent.putExtra(SimpleDataPickerActivity.PARAM_EXTRA_DATA_KEY, builder);
                startActivityForResult(intent, REQUEST_CODE_GETTING_CC);
                break;
            case R.id.begin_date_edit_text:
                setDatePickerDialog();
                break;
            case R.id.begin_time_edit_text:
                setTimePickerDialog();
                break;
            case R.id.commit_button:
                if (isInputDataVailable()) {
                    commiteData();
                }
                else {
                    String message = AbsenceFormApplyActivity.this.getResources()
                            .getString(R.string.absence_apply_is_not_fully_file);
                    Toast.makeText(this, message, Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    private boolean isInputDataVailable() {
        //Please write the code
        if (    beginDateEditText.getText().length() > 0 &&
                beginTimeEditText.getText().length() > 0 &&
                (daysEditText.getText().length() > 0 ||
                 hoursEditText.getText().length() > 0) &&
                !applyCcTextView.getText().equals(DATA_PICKER_NOSETTING))
            return true;
        else {
            return  false;
        }
    }

    private void commiteData() {
        int posi;

        absenceFormData.studentId = authUser.studend_id;

        posi = applyToSpinner.getSelectedItemPosition();
        absenceFormData.toTeacherId = classData.materList.get(posi).id;

        absenceFormData.type = applyTypeSpinner.getSelectedItem().toString();

        String string = beginDateEditText.getText().toString()
                + " " + beginTimeEditText.getText().toString();
        absenceFormData.begin = toDatetime(string);

        float durationInHour = 0;
        if (daysEditText.getText().length() > 0) {
            String input = daysEditText.getText().toString();
            durationInHour += Integer.valueOf(input) * 24;
        }
        if (hoursEditText.getText().length() > 0) {
            durationInHour += Float.parseFloat(hoursEditText.getText().toString());
        }
        absenceFormData.ending = new Date(absenceFormData.begin.getTime()
                + (long)(durationInHour * 3600 * 1000));
        absenceFormData.cause = causeEditText.getText().toString();

        absenceFormData.ccList.clear();
        for (SimplePickItemData itemData : ccPickItems) {
            for (CourseData courseData : ccCourseList) {
                if (courseData.id == itemData.id &&
                        itemData.isPicked) {
                    absenceFormData.ccList.add(courseData);
                    break;
                }
            }
        }

        AbsenceFormDataUtils applyDataUtils = AbsenceFormDataUtils.getInstance();

        applyDataUtils.requestCommitApply(absenceFormData,
                dbHandler, AbsenceFormDataUtils.TAG_COMMIT_APPLY);
        showBusyProgress(true);
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.begin_date_edit_text) {
                if (hasFocus) {
                    setDatePickerDialog();
                }
            }
            else if (v.getId() == R.id.begin_time_edit_text) {
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
        new TimePickerDialog(AbsenceFormApplyActivity.this,
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

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<AbsenceFormApplyActivity> mActivity;

        DBHandler(AbsenceFormApplyActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final AbsenceFormApplyActivity activity = mActivity.get();
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
                                activity.initApplyCCListData();
                            }
                            else {
                                activity.showBusyProgress(false);
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case StudentDataUtils.TAG_FETCH_STUDENT_COURSE:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.RefreshApplyCcDataPicker();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                        case AbsenceFormDataUtils.TAG_COMMIT_APPLY:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                        .setMessage(R.string.message_db_commit_success)
                                        .setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.setResult(RESULT_CODE_APPLY_SUCCESS);
                                                activity.finish();
                                            }
                                        });
                                builder.show();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            activity.showBusyProgress(false);
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
