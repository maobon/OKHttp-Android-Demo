package com.demo.okhttp;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class HttpsUtil {

    private static final String TAG = "HttpUtil";

    // timeout 2 min
    private static final int TIMEOUT_IN_MILLIONS = 120 * 1000;


    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public static String doTlsPost(String url, String param) throws Exception {

        CookieManager coManager = new CookieManager();
        CookieStore cookieStore = coManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie cookie = cookies.get(i);
            Log.e(TAG, "....cookie......" + cookie);
        }

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        MyX509TrustManager xtm = new MyX509TrustManager();
        MyHostnameVerifier hnv = new MyHostnameVerifier();
        try {
            // L.e("doTlsPost url:" + url + " param:" + param);
            VerboseLogger.printE(TAG, "doTlsPost url:" + url + " param:" + param);

            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
                sslContext.init(null, xtmArray, new java.security.SecureRandom());
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            if (sslContext != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            }

            HttpsURLConnection.setDefaultHostnameVerifier(hnv);

            URL realUrl = new URL(url);
            if (!TextUtils.isEmpty(param))
                realUrl = new URL(url + param); // some post interface add query params to url

            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestMethod("POST");

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

            /*if (param != null && !param.trim().equals("")) {
                out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.flush();
            }*/

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            // L.v("doTlsPost  result:" + result);
            VerboseLogger.printV(TAG, "doTlsPost  result:" + result);

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            VerboseLogger.printV(TAG, "Certificate:" + chain[0] + "  AuthType:" + authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            VerboseLogger.printV(TAG, "Warning:" + hostname + "  vs." + session.getPeerHost());
            return true;
        }

    }
}
