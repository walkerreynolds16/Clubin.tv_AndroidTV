package com.onesouth.clubin_tv;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendHTTPRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetVersion";


    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

    @Override
    protected String doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://clubin-tv-backend.herokuapp.com/getVersion";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            Log.i(TAG, res);

            return (res);


        } catch(Exception e){
            Log.i(TAG, e.toString());
        }

        return null;
    }
}
