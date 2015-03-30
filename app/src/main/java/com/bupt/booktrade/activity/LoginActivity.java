package com.bupt.booktrade.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;
import com.bupt.booktrade.utils.UserProxy;

/**
 * Created by LiuYan on 2015/3/24.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, UserProxy.ILoginListener {

    private EditText email, userName, passWord;
    private Button confirmButton;

    private int operation = 0;
    private final int MODE_LOGIN = 0;
    private final int MODE_REGISTER = 1;

    private boolean isActionMenuVisible = false;
    private UserProxy userProxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.login_email);
        userName = (EditText) findViewById(R.id.login_user_name);
        passWord = (EditText) findViewById(R.id.login_pass_word);
        confirmButton = (Button) findViewById(R.id.login_confirm_button);
        confirmButton.setOnClickListener(this);
        getActionBar().setTitle(R.string.title_activity_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        userProxy = new UserProxy(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_progress, menu);
        if (!isActionMenuVisible) {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_progress:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_confirm_button:
                if (operation == MODE_LOGIN) {
                    if (TextUtils.isEmpty(userName.getText())) {
                        ToastUtils.showToast(mContext, R.string.empty_username, Toast.LENGTH_SHORT);
                        return;
                    }
                    if (TextUtils.isEmpty(passWord.getText())) {
                        ToastUtils.showToast(mContext, R.string.empty_password, Toast.LENGTH_SHORT);
                        return;
                    }
                    userProxy.setOnLoginListener(this);
                    isActionMenuVisible = true;
                    invalidateOptionsMenu();
                    userProxy.login(userName.getText().toString().trim(), passWord.getText().toString().trim());
                } else if (operation == MODE_REGISTER) {


                }
        }
    }

    @Override
    public void onLoginSuccess() {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        ToastUtils.showToast(mContext, R.string.login_successfully, Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        LogUtils.i(TAG, "login successfully: " + userProxy.getCurrentUser().getUsername());
        //finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        ToastUtils.showToast(mContext, R.string.wrong_username_or_password, Toast.LENGTH_SHORT);
        LogUtils.i(TAG, msg);
    }


}
