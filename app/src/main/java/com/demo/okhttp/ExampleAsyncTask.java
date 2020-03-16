package com.demo.okhttp;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;
import java.util.Set;

public class ExampleAsyncTask extends AsyncTask<Map<String, String>, Void, String> {

    private static final String TAG = "ExampleAsyncTask";

    private String targetUrl;

    public ExampleAsyncTask(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @SafeVarargs
    @Override
    protected final String doInBackground(Map<String, String>... maps) {

        if (maps == null || maps.length == 0) return null;

        StringBuilder sb = new StringBuilder();

        Map<String, String> map = maps[0];
        Set<Map.Entry<String, String>> entries = map.entrySet();
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

        try {
            String s = HttpsUtil.doTlsPost(targetUrl, sb.toString());
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.wtf(TAG, "s:" + s);
    }
}
