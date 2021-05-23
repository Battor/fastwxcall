package com.battor.fastwxcall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends Fragment {
    private final int START_LOGIN_ACTIVITY = 0;

    TextView userTextView;
    Button signinButton;
    Button signoutButton;
    Button uploadDataButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        userTextView = view.findViewById(R.id.setting_fragment_user_text);
        signinButton = view.findViewById(R.id.setting_fragment_login_button);
        signoutButton = view.findViewById(R.id.setting_fragment_logout_button);
        uploadDataButton = view.findViewById(R.id.setting_fragment_sync_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, START_LOGIN_ACTIVITY);
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpRequestHelper.Token = null;

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.remove("userName");
                editor.remove("password");
                editor.apply();

                Toast.makeText(getContext(), "已成功退出账号", Toast.LENGTH_SHORT).show();

                signinButton.setVisibility(View.VISIBLE);
                signoutButton.setVisibility(View.GONE);
                userTextView.setText("当前还未登陆");
            }
        });

        uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Contact> contactList = ContactDataBaseHelper.initAndObtain(getContext()).getContactList();
                String contactsJson = new GsonBuilder()
                                            .excludeFieldsWithoutExposeAnnotation()
                                            .setDateFormat("yyyy-MM-dd HH:mm")
                                            .create().toJson(contactList);
                List<File> fileList = new ArrayList<>();
                for (Contact contact: contactList ) {
                    fileList.add(new File(contact.getPhotoImgPath()));
                }
                HttpRequestHelper.uploadContactData(contactsJson, fileList, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "上传数据失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()){
                                    Toast.makeText(getContext(), "上传数据成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "上传数据失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

        SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        final String userName = pref.getString("userName", null);
        final String password = pref.getString("password", null);

        // 如果存在用户名和密码信息则直接自动登录(TODO:应该根据时机判断是否需要自动登录，否则增加服务器压力)
        if(userName != null && password != null){
            HttpRequestHelper.login(userName, password,
                    HttpRequestHelper.buildRegistOrLoginCallback(getActivity(), new Runnable() {
                @Override
                public void run() {
                    userTextView.setText("当前用户：" + userName);
                    signinButton.setVisibility(View.GONE);
                }
            }));
        }else{
            signoutButton.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case START_LOGIN_ACTIVITY:
                if(resultCode == Activity.RESULT_OK){
                    SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                    String userName = pref.getString("userName", null);
                    userTextView.setText("当前用户：" + userName);
                    signinButton.setVisibility(View.GONE);
                    signoutButton.setVisibility(View.VISIBLE);
                }
        }
    }
}
