package com.battor.fastwxcall;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        final TextView userNameTextView = (TextView) findViewById(R.id.login_username);
        final TextView passwordTextView = (TextView) findViewById(R.id.login_password);

        final Button signinButton = (Button) findViewById(R.id.login_button);
        final Button registButton = (Button) findViewById(R.id.regist_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameTextView.clearFocus();
                passwordTextView.clearFocus();

                final String userName = userNameTextView.getText().toString();
                final String password = passwordTextView.getText().toString();
                if(userName == null || "".equals(userName) || password == null || "".equals(password)){
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }else {
                    hideSoftKeyboard();
                    HttpRequestHelper.login(userName, password,
                            HttpRequestHelper.buildRegistOrLoginCallback(LoginActivity.this, new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    editor.putString("userName", userName);
                                    editor.putString("password", password);
                                    editor.apply();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }));
                }
            }
        });
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameTextView.clearFocus();
                passwordTextView.clearFocus();

                final String userName = userNameTextView.getText().toString();
                final String password = passwordTextView.getText().toString();
                if(userName == null || "".equals(userName) || password == null || "".equals(password)){
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }else{
                    hideSoftKeyboard();
                    HttpRequestHelper.regist(userName, password,
                            HttpRequestHelper.buildRegistOrLoginCallback(LoginActivity.this, new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                    editor.putString("userName", userName);
                                    editor.putString("password", password);
                                    editor.apply();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideSoftKeyboard(){
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
}
