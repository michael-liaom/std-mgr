package com.weijie.stdmgr;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeDetailListActivity extends AppCompatActivity implements View.OnClickListener {
    final private static int REQUEST_FOR_CREATE  = 2;

    private View subjectView;
    private ListView listView;
    private ProgressBar progressBar;
    private Button createButton;
    private ExchangeSubjectData subjectData;

    MyBaseAdapter baseAdapter;
    private ArrayList <ExchangeDetailData> arrayListDetailtData;
    private AuthUserData authUser;
    private ExchangeDetailData detailData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FOR_CREATE:
                if (resultCode == ExchangeSubjectCreateActivity.RESULT_CODE_COMMINT) {
                    requestData();
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_button) {
            EditText replyEditText = (EditText) findViewById(R.id.reply_edit_text);
            if (replyEditText.getText().length() == 0) {
                Toast.makeText(this, R.string.message_content_blank_error,
                        Toast.LENGTH_LONG).show();
                return;
            }

            detailData = new ExchangeDetailData();
            detailData.subjectId    = subjectData.id;
            if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                detailData.direction = ExchangeSubjectData.EXCHANGE_DIRECTION_FROM;
            }
            else {
                detailData.direction = ExchangeSubjectData.EXCHANGE_DIRECTION_TO;
            }
            detailData.content      = replyEditText.getText().toString();
            detailData.create       = new Date();

            ExchangeDetailDataUtils.getInstance()
                    .requestCommit(detailData, dbHandler,
                            ExchangeDetailDataUtils.TAG_COMMIT_DATA);
            showBusyProgress(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_detail_list);

        authUser = MyApplication.getInstance().authUser;
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            setTitle("与辅导员的信息交流");
        }
        else {
            setTitle("与学生的信息交流");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initData();
        initControls();

        requestData();
    }

    private void initData() {
        subjectData = new ExchangeSubjectData();
        Intent intent = getIntent();
        subjectData.id = intent.getIntExtra(ExchangeSubjectData.COL_ID, 0);

        arrayListDetailtData = new ArrayList<>();
    }

    private void initControls() {
        subjectView = findViewById(R.id.subject_linear_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        listView    = (ListView) findViewById(R.id.list_view_dynamic);
        createButton= (Button) findViewById(R.id.create_button);

        baseAdapter = new MyBaseAdapter(this, arrayListDetailtData,
                R.layout.activity_exchange_detail_list_item);
        listView.setAdapter(baseAdapter);
        createButton.setOnClickListener(this);

        subjectView.setFocusable(true);
        subjectView.setFocusableInTouchMode(true);
        subjectView.requestFocus();
    }

    private void requestData() {
        ExchangeSubjectDataUtils.getInstance()
                .requestFetchSubjectData(subjectData.id, subjectData,
                        dbHandler, ExchangeSubjectDataUtils.TAG_FETCH_DATA);
        showBusyProgress(true);
    }

    private void requestList() {
        EditText replyEditText = (EditText) findViewById(R.id.reply_edit_text);
        replyEditText.setText("");

        InputMethodManager imm
                = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        arrayListDetailtData.clear();
        ExchangeDetailDataUtils.getInstance()
                .requestFetchDetailListOfSubject(subjectData.id, arrayListDetailtData,
                        dbHandler, ExchangeDetailDataUtils.TAG_FETCH_LIST);
        showBusyProgress(true);
    }

    private void showBusyProgress(boolean isBussy) {
        if (isBussy) {
            progressBar.setVisibility(View.VISIBLE);
            //applyBotton.setEnabled(false);
        }
        else {
            progressBar.setVisibility(View.GONE);
            //applyBotton.setEnabled(true);
        }
    }

    private void refreshData() {
        TextView toUserTextView = (TextView) findViewById(R.id.to_user_text_view);
        TextView subjectTextView= (TextView) findViewById(R.id.subject_text_view);
        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
            toUserTextView.setText("辅导员");
        }
        else {
            toUserTextView.setText(subjectData.studentName
                    + " ("
                    + StudentData.getGenderName(subjectData.studentGender)
                    + ")"
                    + "  "
                    + subjectData.studentCode);
        }
        subjectTextView.setText(subjectData.subject);

        baseAdapter.notifyDataSetChanged();
        if (baseAdapter.getCount() > 0) {
            listView.setSelection(baseAdapter.getCount());
        }
    }

    class MyBaseAdapter extends BaseAdapter {
        private List<ExchangeDetailData> list;
        private Context context;
        private int resource;

        MyBaseAdapter(Context context, List<ExchangeDetailData> list, int resource) {
            this.context    = context;
            this.list       = list;
            this.resource   = resource;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int index) {
            return list.get(index);
        }

        @Override
        public long getItemId(int index) {

            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(resource, null);
            }

            TextView fromTimestampTextView
                    = (TextView) view.findViewById(R.id.from_timestamp_text_view);
            LinearLayout fromLayout
                    = (LinearLayout) view.findViewById(R.id.from_layout);
            TextView fromUserTextView
                    = (TextView) view.findViewById(R.id.from_text_view);
            TextView fromMessageTextView
                    = (TextView) view.findViewById(R.id.from_msg_text_view);

            TextView toTimestampTextView
                    = (TextView) view.findViewById(R.id.to_timestamp_text_view);
            LinearLayout toLayout
                    = (LinearLayout) view.findViewById(R.id.to_layout);
            TextView toUserTextView
                    = (TextView) view.findViewById(R.id.to_text_view);
            TextView toMessageTextView
                    = (TextView) view.findViewById(R.id.to_msg_text_view);

            boolean isOutgoing;
            ExchangeDetailData detailData = list.get(index);
            if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                if(detailData.direction.equals(ExchangeSubjectData.EXCHANGE_DIRECTION_FROM)) {
                    isOutgoing = true;
                }
                else {
                    isOutgoing = false;
                }
            }
            else {
                if(detailData.direction.equals(ExchangeSubjectData.EXCHANGE_DIRECTION_FROM)) {
                    isOutgoing = false;
                }
                else {
                    isOutgoing = true;
                }
            }

            if (isOutgoing) {
                fromTimestampTextView.setVisibility(View.GONE);
                fromUserTextView.setVisibility(View.GONE);
                fromLayout.setVisibility(View.GONE);

                toTimestampTextView.setVisibility(View.VISIBLE);
                toUserTextView.setVisibility(View.VISIBLE);
                toLayout.setVisibility(View.VISIBLE);

                //toUserTextView.setText("我");
                toTimestampTextView
                        .setText(CommUtils.toLocalDatetimeString(detailData.create));
                toMessageTextView.setText(detailData.content);
            }
            else {
                fromTimestampTextView.setVisibility(View.VISIBLE);
                fromUserTextView.setVisibility(View.VISIBLE);
                fromLayout.setVisibility(View.VISIBLE);

                toTimestampTextView.setVisibility(View.GONE);
                toUserTextView.setVisibility(View.GONE);
                toLayout.setVisibility(View.GONE);

                if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                    fromUserTextView.setText("老师");
                }
                else {
                    fromUserTextView.setText("学生");
                }
                fromTimestampTextView
                        .setText(CommUtils.toLocalDatetimeString(detailData.create));
                fromMessageTextView.setText(detailData.content);
            }

            return view;
        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<ExchangeDetailListActivity> mActivity;

        DBHandler(ExchangeDetailListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final ExchangeDetailListActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    activity.showBusyProgress(false);
                    if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                        switch (tag) {
                            case ExchangeSubjectDataUtils.TAG_FETCH_DATA:
                            case ExchangeDetailDataUtils.TAG_COMMIT_DATA:
                                activity.requestList();
                                break;
                            case ExchangeSubjectDataUtils.TAG_FETCH_LIST:
                                activity.refreshData();
                                break;
                        }
                    }
                    else {
                        Toast.makeText(activity, R.string.message_db_operation_failure,
                                Toast.LENGTH_LONG).show();
                    }
                }
                super.handleMessage(msg);
            }
        }
    }
}
