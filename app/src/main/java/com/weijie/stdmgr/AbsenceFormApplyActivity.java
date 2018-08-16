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

public class AbsenceFormApplyActivity extends AppCompatActivity implements View.OnClickListener {
    final static int REQUEST_CODE_GETTING_CC    = 0;
    final static int RESULT_CODE_APPLY_CANCEL   = 0;
    final static int RESULT_CODE_APPLY_SUCCESS  = 1;
    final static String DATA_PICKER_NOSETTING   = "未设置";

    private View inputForm;
    private TextView applyFrom;
    private Spinner applyTypeSpinner;
    private EditText causeEditText;
    private EditText beginDateEditText,
            endingDateEditText;
    private TextView classTeacherTextView;
    private TextView courseTextView;
    private TextView courseTeacherTextView;
    private EditText courseCountEditText;
    private Button commiteButton;

    private ProgressBar progressBar;

    private AuthUserData authUser;
    private ClassData classData;

    private ArrayList<CourseData> ccCourseList;
    private ArrayList<SimplePickItemData> ccPickItems;

    private ArrayAdapter<String> adapterCc;
    private AbsenceFormData absenceFormData;

    private int requestCounter;

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
                refreshControlDisp();
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

        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        absenceFormData = new AbsenceFormData();

        //toTeacherList  = new ArrayList();
        ccCourseList   = new ArrayList();
        ccPickItems = new ArrayList<>();


        classData = new ClassData();
    }

    private void refreshClassTeacherDisplay() {
        TextView classTeacherTextView = (TextView) findViewById(R.id.class_teacher_text_view);
        classTeacherTextView.setText(classData.teacherName);
    }

    private void refreshCourseDataPicker() {
        ccPickItems.clear();
        for (CourseData courseData : ccCourseList) {
            boolean isPicked = false;
            if (absenceFormData.courseId == courseData.id) {
                isPicked = true;
            }
            SimplePickItemData pickItemData
                    = new SimplePickItemData(courseData.name + " / " + courseData.teacherName,
                    courseData.id, isPicked);
            ccPickItems.add(pickItemData);
        }
    }

    private void initControls() {
        TextView menuTextView = (TextView) findViewById(R.id.title_text_view);
        applyFrom           = (TextView) findViewById(R.id.apply_from_text_view);
        inputForm           = findViewById(R.id.input_form);
        beginDateEditText   = (EditText) findViewById(R.id.begin_date_edit_text);
        endingDateEditText  = (EditText) findViewById(R.id.ending_date_edit_text);
        applyTypeSpinner    = (Spinner) findViewById(R.id.apply_type_spinner);
        causeEditText       = (EditText) findViewById(R.id.cause_edit_text);
        classTeacherTextView
                = (TextView) findViewById(R.id.class_teacher_text_view);
        courseTextView      = (TextView) findViewById(R.id.course_text_view);
        courseTeacherTextView
                = (TextView) findViewById(R.id.course_teacher_text_view);
        courseCountEditText = (EditText) findViewById(R.id.course_count_edit_text);
        progressBar
                = (ProgressBar) findViewById(R.id.progress_bar);
        commiteButton       = (Button) findViewById(R.id.commit_button);

        LinearLayout ccLayout = (LinearLayout) findViewById(R.id.cc_layout);
        ccLayout.setOnClickListener(this);
        beginDateEditText.setOnClickListener(this);
        endingDateEditText.setOnClickListener(this);
        commiteButton.setOnClickListener(this);

        beginDateEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        endingDateEditText.setOnFocusChangeListener(myOnFocusChangeListener);
        beginDateEditText.setInputType(InputType.TYPE_NULL);
        endingDateEditText.setInputType(InputType.TYPE_NULL);

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
        menuTextView.setFocusable(true);
        menuTextView.setFocusableInTouchMode(true);
        menuTextView.requestFocus();
    }

    private void requestData() {
        ClassDataUtils.getInstance().requestFetchClassData(authUser.studend_id, classData,
                dbHandler, ClassDataUtils.TAG_FETCH_CLASS_DATA);
        StudentDataUtils.getInstance()
                .requestFetchStudentCourseData(authUser.studend_id, true,
                        ccCourseList, dbHandler, StudentDataUtils.TAG_FETCH_COURSE);
        requestCounter = 2;
        showBusyProgress(true);
    }

    private void refreshControlDisp() {
        if (requestCounter == 0) {
            if (absenceFormData.studentId > 0) {
                applyFrom.setText(absenceFormData.studentName);
            } else {
                applyFrom.setText(authUser.studentData.name);
            }

            String courseName = "";
            String courseTeacher = "";
            for (int idx = 0; idx < ccPickItems.size(); idx++) {
                SimplePickItemData itemData = ccPickItems.get(idx);
                if (itemData.isPicked) {
                    courseName = ccCourseList.get(idx).code + " " + ccCourseList.get(idx).name;
                    courseTeacher = ccCourseList.get(idx).teacherName;
                    break;
                }
            }
            courseTextView.setText(courseName);
            courseTeacherTextView.setText(courseTeacher);
        }
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);

        }
        else {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cc_layout:
                Intent intent = new Intent(this,
                        SimpleDataPickerActivity.class);
                SimpleDataPickerActivity.Builder builder
                        = new SimpleDataPickerActivity.Builder("抄送任课老师",
                        "", ccPickItems, false);
                intent.putExtra(SimpleDataPickerActivity.PARAM_EXTRA_DATA_KEY, builder);
                startActivityForResult(intent, REQUEST_CODE_GETTING_CC);
                break;
            case R.id.begin_date_edit_text:
                setBeginDatePickerDialog();
                break;
            case R.id.ending_date_edit_text:
                setEndingTimePickerDialog();
                break;
            case R.id.commit_button:
                if (isInputDataVailable()) {
                    commiteData();
                }
                break;
        }
    }

    private boolean isInputDataVailable() {
        //Please write the code
        if (    beginDateEditText.getText().length() > 0 &&
                endingDateEditText.getText().length() > 0 &&
                courseTextView.getText().length() > 0 &&
                courseCountEditText.getText().length() > 0) {
            Date begin  = CommUtils.toDate(beginDateEditText.getText().toString());
            Date ending = CommUtils.toDate(endingDateEditText.getText().toString());
            if (ending.getTime() >= begin.getTime()) {
                return true;
            }
            else {
                String message = AbsenceFormApplyActivity.this.getResources()
                        .getString(R.string.absence_apply_date_invalid);
                Toast.makeText(this, message, Toast.LENGTH_LONG)
                        .show();
                return false;
            }
        }
        else {
            String message = AbsenceFormApplyActivity.this.getResources()
                    .getString(R.string.absence_apply_is_not_fully_file);
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                    .show();
            return  false;
        }
    }

    private void commiteData() {

        absenceFormData.studentId = authUser.studend_id;

        absenceFormData.begin   = CommUtils.toDate(beginDateEditText.getText().toString());
        absenceFormData.ending  = CommUtils.toDate(endingDateEditText.getText().toString());
        absenceFormData.type    = applyTypeSpinner.getSelectedItem().toString();
        absenceFormData.cause   = causeEditText.getText().toString();

        absenceFormData.classTeacherId = classData.teacherId;

        for (int idx = 0; idx < ccPickItems.size(); idx++) {
            SimplePickItemData itemData = ccPickItems.get(idx);
            if (itemData.isPicked) {
                CourseData courseData = ccCourseList.get(idx);
                absenceFormData.courseId  = courseData.id;
                break;
            }
        }

        absenceFormData.courseCount = Integer.valueOf(courseCountEditText.getText().toString());
        AbsenceFormDataUtils.getInstance().requestCommitApply(absenceFormData,
                dbHandler, AbsenceFormDataUtils.TAG_COMMIT_APPLY);
        requestCounter++;
        showBusyProgress(true);
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.begin_date_edit_text) {
                if (hasFocus) {
                    setBeginDatePickerDialog();
                }
            }
            else if (v.getId() == R.id.ending_date_edit_text) {
                if (hasFocus) {
                    setEndingTimePickerDialog();
                }
            }
        }
    }

    private void setBeginDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        DatePickerDialog dialog
                = new DatePickerDialog(this,
                (new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd",
                                Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DATE, i2);
                        beginDateEditText.setText(format.format(calendar.getTime()));
                    }
                }),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(now.getTime());
        dialog.setTitle("请假开始时间");
        dialog.show();
    }

    private void setEndingTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd",
                                Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DATE, i2);
                        endingDateEditText.setText(format.format(calendar.getTime()));
                    }
                }),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(now.getTime());
        dialog.setTitle("请假结束时间");
        dialog.show();
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
                            activity.requestCounter--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshClassTeacherDisplay();
                                activity.refreshControlDisp();
                            }
                            else {
                                activity.showBusyProgress(false);
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                        case StudentDataUtils.TAG_FETCH_COURSE:
                            activity.requestCounter--;
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshCourseDataPicker();
                                activity.refreshControlDisp();
                            }
                            else {
                                Toast.makeText(activity, R.string.message_db_operation_failure,
                                        Toast.LENGTH_LONG).show();
                            }
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                        case AbsenceFormDataUtils.TAG_COMMIT_APPLY:
                            activity.requestCounter--;
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
                            if (activity.requestCounter == 0) {
                                activity.showBusyProgress(false);
                            }
                            break;
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
