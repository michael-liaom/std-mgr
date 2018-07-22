package com.weijie.stdmgr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HostSettingActivity extends AppCompatActivity {
    final static int RETURN_CODE_CANCEL = 0;
    final static int RETURN_CODE_OK     = 1;
    private EditText hostNameEdiText;
    private AuthUserData authUser;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RETURN_CODE_CANCEL, intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_setting);

        hostNameEdiText = (EditText) findViewById(R.id.host_name_edit_text);
        Button commitButton = (Button) findViewById(R.id.commit_button);

        authUser = MyApplication.getInstance().authUser;

        hostNameEdiText.setText(authUser.hostName);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hostNameEdiText.length() > 0) {
                    authUser.hostName = hostNameEdiText.getText().toString();
                    authUser.backupToLocal();

                    setResult(RETURN_CODE_OK);
                    finish();
                }
            }
        });

    }
}
