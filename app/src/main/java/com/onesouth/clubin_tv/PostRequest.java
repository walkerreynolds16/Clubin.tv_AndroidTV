package com.onesouth.clubin_tv;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "POST Request";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(20, TimeUnit.SECONDS)
                                .build();
        Log.i(TAG, strings[0]);
        RequestBody body = RequestBody.create(JSON, strings[1]);
        Request request = new Request.Builder()
                .url(strings[0])
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.i(TAG, res);

            return (res);


        } catch(Exception e){
            Log.i(TAG + strings[0], e.toString());
        }

        return null;
    }
}
