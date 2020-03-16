package com.demo.okhttp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private SampleAsyncTask sampleAsyncTask;
    private ExampleAsyncTask exampleAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, String> param = new HashMap<>();
                param.put("key", "0fa5e905bba912c6623be8f73e5a24e6");
                param.put("v", "1.0");
                param.put("month", "3");
                param.put("day", "14");

                *//*SampleAsyncTask sampleAsyncTask = new SampleAsyncTask("http://api.juheapi.com/japi/toh");
                sampleAsyncTask.execute((HashMap) param);*//*

                ExampleAsyncTask exampleAsyncTask = new ExampleAsyncTask("http://api.juheapi.com/japi/toh");
                exampleAsyncTask.execute((HashMap) param);
            }
        });*/


        this.sampleAsyncTask = new SampleAsyncTask("http://api.juheapi.com/japi/toh");
        this.exampleAsyncTask = new ExampleAsyncTask("http://api.juheapi.com/japi/toh");


        initViews();

    }

    private void initViews() {
        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    private Map<String, String> buildRequestParams() {
        Map<String, String> param = new HashMap<>();
        param.put("key", "0fa5e905bba912c6623be8f73e5a24e6");
        param.put("v", "1.0");
        param.put("month", "3");
        param.put("day", "14");
        return param;
    }

    private void okHttpRequestTask() {
        //SampleAsyncTask sampleAsyncTask = new SampleAsyncTask("http://api.juheapi.com/japi/toh");
        sampleAsyncTask.execute((HashMap) buildRequestParams());
    }

    private void sampleCodeRequestTask() {
        //ExampleAsyncTask exampleAsyncTask = new ExampleAsyncTask("http://api.juheapi.com/japi/toh");
        exampleAsyncTask.execute((HashMap) buildRequestParams());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private int index = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            final long tStart = System.currentTimeMillis();

            SerialExecutor serialExecutor = new SerialExecutor();
            serialExecutor.setCallback(new SerialExecutor.Callback() {
                @Override
                public void onComplete() {
                    long tCost = System.currentTimeMillis() - tStart;
                    Log.wtf(TAG, "cost= " + tCost + "ms");
                }
            });

            /*for (int i = 0; i < 10; i++) {
                serialExecutor.execute(new RequestTask("http://api.juheapi.com/japi/toh", buildRequestParams()));
            }*/


            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> entries = buildRequestParams().entrySet();

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


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody requestBody = RequestBody.create("", MediaType.get("text/plain"));

            Request request = new Request.Builder()
                    .url("http://api.juheapi.com/japi/toh" + sb.toString())
                    .post(requestBody)
                    .build();

            final List<Long> cc = new ArrayList<>();

            for (int i = 0; i < 10; i++) {

                okHttpClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        long tCostTime = System.currentTimeMillis() - tStart;
                        cc.add(tCostTime);

                        if (cc.size() == 10) {
                            Long max = Collections.max(cc);
                            Log.wtf(TAG, "max:" + max + "ms");
                        }
                    }
                });
            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_test) {

            new Thread(){
                @Override
                public void run() {

                    try {
                        String s = HttpsUtil.doTlsPost("https://www.baidu.com", null);
                        Log.wtf(TAG, "s= " + s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.start();




        }
    }
}
