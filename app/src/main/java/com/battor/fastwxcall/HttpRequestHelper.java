package com.battor.fastwxcall;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestHelper {

    public static String Token = null;

    public static Callback buildRegistOrLoginCallback(final Activity activity, final Runnable extraTodo){
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"网络错误，请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JsonElement element = new JsonParser().parse(response.body().string());
                final JsonObject object = element.getAsJsonObject();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(object.get("Code").getAsInt() == 0){
                            SharedPreferences.Editor editor = activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                            editor.putString("token", object.get("Data").getAsString());
                            editor.apply();

                            HttpRequestHelper.Token = object.get("Data").getAsString();
                            Toast.makeText(activity, "登录成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "失败：" + object.get("Data").getAsString(), Toast.LENGTH_SHORT).show();
                        }
                        activity.runOnUiThread(extraTodo);
                    }
                });

            }
        };
        return callback;
    }

    public static void regist(String userName, String password, Callback callback){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.addFormDataPart("userName", userName);
        multipartBodyBuilder.addFormDataPart("password", password);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(GlobalProperty.SERVER_ADDRESS + "/api/FastWxCall/Regist").put(multipartBodyBuilder.build());
        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void login(String userName, String password, Callback callback){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("userName", userName);
        multipartBodyBuilder.addFormDataPart("password", password);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(GlobalProperty.SERVER_ADDRESS + "/api/FastWxCall/Login").post(multipartBodyBuilder.build());
        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void uploadContactData(String contactsJson, List<File> fileList, Callback callback){
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("contactsJson", contactsJson);

        for (File file: fileList){
            multipartBodyBuilder.addFormDataPart("photoImages", file.getName() , RequestBody.create(file, null));
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Authorization","Bearer " +  HttpRequestHelper.Token);
        requestBuilder.url(GlobalProperty.SERVER_ADDRESS + "/api/FastWxCall/SaveUserContacts").put(multipartBodyBuilder.build());
        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
