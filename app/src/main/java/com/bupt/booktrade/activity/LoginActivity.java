package com.bupt.booktrade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;
import com.bupt.booktrade.utils.UserProxy;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

/**
 * Created by LiuYan on 2015/3/24.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, UserProxy.ILoginListener, UserProxy.ISignUpListener {

    private String TAG;
    private EditText email, userName, passWord;
    private Button confirmButton;
    private TextView register;
    private TextView findPassword;
    private TextView backToLogin;
    private FloatLabeledEditText editTextEmail;
    private final int MODE_LOGIN = 0;
    private final int MODE_REGISTER = 1;
    private int operation = MODE_LOGIN;
    private boolean isActionMenuVisible = false;
    private UserProxy userProxy;

    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TAG = getClass().getSimpleName();
        email = (EditText) findViewById(R.id.login_email);
        userName = (EditText) findViewById(R.id.login_user_name);
        passWord = (EditText) findViewById(R.id.login_pass_word);
        email = (EditText) findViewById(R.id.login_email);
        editTextEmail = (FloatLabeledEditText) findViewById(R.id.float_edittext_email);
        confirmButton = (Button) findViewById(R.id.login_confirm_button);
        register = (TextView) findViewById(R.id.to_register);
        findPassword = (TextView) findViewById(R.id.find_back_password);
        backToLogin = (TextView) findViewById(R.id.back_to_login);
        register.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        backToLogin.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        getActionBar().setTitle(R.string.title_activity_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        userProxy = new UserProxy(mContext);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (operation == MODE_LOGIN) {
                    if (!TextUtils.isEmpty(userName.getText()) && !TextUtils.isEmpty(passWord.getText())) {
                        confirmButton.setEnabled(true);
                        confirmButton.setAlpha(1.0F);
                    } else {
                        confirmButton.setEnabled(false);
                        confirmButton.setAlpha(0.7F);
                    }
                } else if (operation == MODE_REGISTER) {
                    if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(userName.getText()) && !TextUtils.isEmpty(passWord.getText())) {
                        confirmButton.setEnabled(true);
                        confirmButton.setAlpha(1.0F);
                    } else {
                        confirmButton.setEnabled(false);
                        confirmButton.setAlpha(0.7F);
                    }
                }
            }
        };

        email.addTextChangedListener(textWatcher);
        userName.addTextChangedListener(textWatcher);
        passWord.addTextChangedListener(textWatcher);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
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
            case R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_register:
                if (operation == MODE_LOGIN) {
                    switchUI(MODE_REGISTER);
                    operation = MODE_REGISTER;
                }
                break;
            case R.id.back_to_login:
                if (operation == MODE_REGISTER) {
                    switchUI(MODE_LOGIN);
                    operation = MODE_LOGIN;
                }
                break;
            case R.id.login_confirm_button:
                if (operation == MODE_LOGIN) {
                    userProxy.setOnLoginListener(this);
                    isActionMenuVisible = true;
                    invalidateOptionsMenu();
                    userProxy.login(userName.getText().toString().trim(), passWord.getText().toString().trim());
                } else if (operation == MODE_REGISTER) {
                    userProxy.setOnSignUpListener(this);
                    isActionMenuVisible = true;
                    invalidateOptionsMenu();
                    userProxy.signUp(userName.getText().toString().trim(), passWord
                            .getText().toString().trim(), email.getText().toString()
                            .trim());
                }

            default:
                return;
        }
    }

    private void switchUI(int mode) {
        switch (mode) {
            case MODE_LOGIN:
                editTextEmail.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);
                backToLogin.setVisibility(View.INVISIBLE);
                confirmButton.setText(R.string.title_activity_login);
                break;

            case MODE_REGISTER:
                editTextEmail.setVisibility(View.VISIBLE);
                email.requestFocus();
                register.setVisibility(View.INVISIBLE);
                backToLogin.setVisibility(View.VISIBLE);
                confirmButton.setText(R.string.title_activity_register);
                break;

            default:
                return;

        }


    }

    @Override
    public void onLoginSuccess() {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        ToastUtils.showToast(mContext, R.string.login_successfully, Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        LogUtils.i(TAG, "login successfully: " + userProxy.getCurrentUser().getUsername());
        onBackPressed();
        //finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        ToastUtils.showToast(mContext, msg, Toast.LENGTH_SHORT);
        LogUtils.i(TAG, msg);
    }


    @Override
    public void onSignUpSuccess() {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        operation = MODE_LOGIN;
        switchUI(MODE_LOGIN);
        ToastUtils.showToast(mContext, R.string.register_successfully, Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        LogUtils.i(TAG, "register successfully: " + userProxy.getCurrentUser().getUsername());
    }

    @Override
    public void onSignUpFailure(String msg) {
        isActionMenuVisible = false;
        invalidateOptionsMenu();
        ToastUtils.showToast(mContext, msg, Toast.LENGTH_SHORT);
        LogUtils.i(TAG, msg);
    }
}
