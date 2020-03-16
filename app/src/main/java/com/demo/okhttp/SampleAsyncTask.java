package com.demo.okhttp;

import android.os.AsyncTask;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SampleAsyncTask extends AsyncTask<Map<String, String>, Void, String> {

    private static final String TAG = "SampleAsyncTask";


    private OkHttpClient okHttpClient;

    private String targetUrl;

    SampleAsyncTask(String targetUrl) {

        this.targetUrl = targetUrl;

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @SafeVarargs
    @Override
    protected final String doInBackground(Map<String, String>... params) {
        StringBuilder sb = new StringBuilder();

        Map<String, String> param = params[0];
        Set<Map.Entry<String, String>> entries = param.entrySet();

        int pos = 0;
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (pos == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", key, value));
            pos++;
        }

        RequestBody requestBody = RequestBody.create("", MediaType.get("text/plain"));

        Request request = new Request.Builder()
                .url(targetUrl + sb.toString())
                .post(requestBody)
                .build();

        try {

            Response response = okHttpClient.newCall(request).execute();

            return response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        VerboseLogger.printE(TAG, s);

    }
}
