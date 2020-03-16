package com.demo.okhttp;

import java.util.Map;
import java.util.Set;

public class RequestTask implements Runnable {

    private String targetUrl;

    private Map<String, String> params;

    public RequestTask(String targetUrl, Map<String, String> params) {
        this.targetUrl = targetUrl;
        this.params = params;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> entries = params.entrySet();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
