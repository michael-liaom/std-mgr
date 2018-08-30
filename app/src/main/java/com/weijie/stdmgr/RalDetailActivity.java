package com.weijie.stdmgr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by weijie on 2018/8/14.
 */
public class RalDetailActivity extends AppCompatActivity {
    final private static int REQUEST_FOR_APPLY = 1;

    private AuthUserData authUser;
    private RalData ralData;

    private ProgressBar progressBar;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_APPLY) {
            if (resultCode == RalApplyCreateActivity.RESULT_CODE_APPLY_SUCCESS) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ral_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("奖助贷信息详情");

        initData();
        initControls();
        requestData();
    }

    private void initData() {
        authUser = MyApplication.getInstance().authUser;
        ralData  = new RalData();
    }

    private void initControls() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button button = (Button) findViewById(R.id.action_button);
        button.setVisibility(View.GONE);
    }

    private void requestData() {
        Intent intent = getIntent();
        int ralId = intent.getIntExtra(RalData.COL_ID, 0);

        if (ralId > 0) {
            RalDataUtils.getInstance()
                    .requestFetchRalData(ralId, ralData, dbHandler,
                            RalDataUtils.TAG_FETCH_DATA);
            showBusyProgress(true);
        }
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
        TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        TextView amountTextView
                = (TextView) findViewById(R.id.amount_text_view);
        TextView gradeTextView
                = (TextView) findViewById(R.id.grade_text_view);
        TextView termTextView
                = (TextView) findViewById(R.id.term_text_view);
        TextView numberTextView
                = (TextView) findViewById(R.id.number_text_view);
        TextView requireTextView
                = (TextView) findViewById(R.id.requirement_text_view);

        nameTextView.setText(ralData.name);
        amountTextView.setText(Integer.toString(ralData.amount));
        gradeTextView.setText(ralData.grade);
        termTextView.setText(ralData.term);
        numberTextView.setText(Integer.toString(ralData.number));
        String text = ralData.requirement.replace("\\n", "\n");
        requireTextView.setText(text);

        if (authUser.studend_id > 0) {
            Button button = (Button) findViewById(R.id.action_button);
            button.setVisibility(View.VISIBLE);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RalDetailActivity.this,
                            RalApplyCreateActivity.class);
                    intent.putExtra(RalData.COL_ID, ralData.id);
                    startActivity(intent);
                }
            });
        }
    }

    final DBHandler dbHandler = new DBHandler(this);
    private static class DBHandler extends Handler {
        private final WeakReference<RalDetailActivity> mActivity;

        DBHandler(RalDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            final RalDetailActivity activity = mActivity.get();
            if (activity != null) {
                boolean is_sucess = false;
                String message = null;
                // ...
                String tag = (String) msg.obj;
                if (tag != null) {
                    switch (tag) {
                        case RalDataUtils.TAG_FETCH_DATA:
                            if (msg.what == JdbcMgrUtils.DB_REQUEST_SUCCESS) {
                                activity.refreshData();
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
